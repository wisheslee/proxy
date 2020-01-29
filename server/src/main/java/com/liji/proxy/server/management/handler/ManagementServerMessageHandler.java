package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.common.utils.MessageResponseFactory;
import com.liji.proxy.server.common.ProxyContext;
import com.liji.proxy.server.proxy.handler.NewConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
@ChannelHandler.Sharable
public class ManagementServerMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        //newProxy
        if (msg.getBody().is(MessageProto.NewProxy.class)) {
            LOGGER.info("receive newProxy message");
            MessageProto.NewProxy newProxy = msg.getBody().unpack(MessageProto.NewProxy.class);
            //在指定端口新开一个serverBootstrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(ProxyContext.proxyBossGroup, ProxyContext.proxyWorkerGroup);
            serverBootstrap
                    .channel(NioServerSocketChannel.class)
                    //重点，不要auto_read
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NewConnectionHandler());
                        }
                    });
            ChannelFuture bindFuture = serverBootstrap.bind("127.0.0.1", newProxy.getProxyPort());
            bindFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    MessageProto.Header header = MessageFactory.newHeader();
                    if (future.isSuccess()) {
                        if (ProxyContext.putIfAbsent(newProxy.getProxyPort(), newProxy.getLocalHost(), newProxy.getLocalPort(), ctx.channel())){
                            ctx.writeAndFlush(MessageResponseFactory.success(header));
                            return;
                        }
                        ctx.writeAndFlush(MessageResponseFactory.fail(header, "端口已被占用"));
                    }
                    if (future.cause() != null) {
                        ctx.writeAndFlush(MessageResponseFactory.fail(header, future.cause().getMessage()));
                    }
                    ctx.writeAndFlush(MessageResponseFactory.fail(header));
                    ctx.close();
                }
            });
        }

    }


}

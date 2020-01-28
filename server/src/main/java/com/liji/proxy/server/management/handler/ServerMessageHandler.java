package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.utils.MessageResponseFactory;
import com.liji.proxy.server.proxy.ProxyContext;
import com.liji.proxy.server.proxy.handler.NewConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ServerMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        //auth
        if (msg.getMessageBody().is(MessageProto.Authentication.class)) {
            LOGGER.info("receive auth message");
            MessageProto.Authentication authentication = msg.getMessageBody().unpack(MessageProto.Authentication.class);
            LOGGER.info("username={}, password={}", authentication.getUsername(), authentication.getPassword());
            ctx.writeAndFlush(MessageProto.Response.newBuilder().setStatus(200));
            //记录一个client
        }
        //newProxy
        if (msg.getMessageBody().is(MessageProto.NewProxy.class)) {
            LOGGER.info("receive newProxy message");
            MessageProto.NewProxy newProxy = msg.getMessageBody().unpack(MessageProto.NewProxy.class);
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
            bindFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        if (ProxyContext.putIfAbsent(newProxy.getProxyPort(), newProxy.getLocalHost(), newProxy.getLocalPort(), bindFuture.channel())){
                            ctx.writeAndFlush(MessageResponseFactory.success());
                            return;
                        }
                        ctx.writeAndFlush(MessageResponseFactory.fail("端口已被占用"));
                    }
                    if (future.cause() != null) {
                        ctx.writeAndFlush(MessageResponseFactory.fail(future.cause().getMessage()));
                    }
                    ctx.writeAndFlush(MessageResponseFactory.fail());
                    ctx.close();
                }
            });
        }

    }
}

package com.liji.proxy.client.handler;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.model.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getBody().is(MessageProto.Response.class)) {
            MessageProto.Response response = msg.getBody().unpack(MessageProto.Response.class);
            LOGGER.info("response = {}", response);
        } else {
            LOGGER.info("receive a message={}", msg);
        }

        if (msg.getBody().is(MessageProto.NewConnectionFromOuter.class)) {
            LOGGER.info("receive newConnectionFromOuter message");
            MessageProto.NewConnectionFromOuter newConnectionFromOuter = msg.getBody().unpack(MessageProto.NewConnectionFromOuter.class);
            String reqId = msg.getHeader().getReqId();

            //向本地服务发起连接
            Bootstrap localServerBootstrap = new Bootstrap();
            localServerBootstrap.group(ctx.channel().eventLoop());
            ChannelFuture localServerConnectFuture = localServerBootstrap.channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LocalServerHandler());
                        }
                    }).connect(newConnectionFromOuter.getLocalHost(), newConnectionFromOuter.getLocalPort());
            localServerConnectFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        //向数据服务接口发起连接
                        Bootstrap bootstrap = new Bootstrap();
                        bootstrap.group(ctx.channel().eventLoop());
                        ChannelFuture connectFuture = bootstrap.channel(NioSocketChannel.class)
                                .handler(new ChannelInitializer<NioSocketChannel>() {
                                    @Override
                                    protected void initChannel(NioSocketChannel ch) throws Exception {
                                        ChannelPipeline pipeline = ch.pipeline();
                                        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                                        pipeline.addLast(new ClientProxyHandler(localServerConnectFuture.channel()));
                                    }
                                }).connect(DefaultConstants.SERVER_HOST, DefaultConstants.SERVER_DATA_PORT);
                        connectFuture.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if (future.isSuccess()) {
                                    localServerConnectFuture.channel().pipeline().addLast(new LocalServerHandler(future.channel()));
                                    future.channel().writeAndFlush(ctx.alloc().buffer().writeBytes(reqId.getBytes(StandardCharsets.UTF_8))).addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future) throws Exception {
                                            System.out.println(future);
                                        }
                                    });
                                }
                            }
                        });
                        connectFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
                    } else {
                        LOGGER.error(future.cause().getMessage(), future.cause());
                    }
                }
            });
        }
    }
}

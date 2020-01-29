package com.liji.proxy.server.management;

import com.liji.proxy.common.handler.ExceptionHandler;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.server.management.handler.AuthHandler;
import com.liji.proxy.server.management.handler.ManagementServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ManagementServer implements Runnable {

    private int port;

    public ManagementServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("managementServerBoss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("managementServerWorker"));

        try {
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
            ExceptionHandler exceptionHandler = new ExceptionHandler();
            AuthHandler authHandler = new AuthHandler();
            ManagementServerMessageHandler messageHandler = new ManagementServerMessageHandler();

            serverBootstrap
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("managementServerLoggingHanlder", loggingHandler);

                            //protobuf编解码器
                            pipeline.addLast("managementServerFrameDecoder", new ProtobufVarint32FrameDecoder());
                            pipeline.addLast("managementMessageDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast("managementServerFrameEncoder", new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast("managementServerMessageEncoder", new ProtobufEncoder());

                            pipeline.addLast("managementServerAuthHandler", authHandler);
                            pipeline.addLast("managementServerMessageHandler", messageHandler);
                            pipeline.addLast("managementServerExceptionHandler", exceptionHandler);
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

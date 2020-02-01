package com.liji.proxy.server.management;

import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.management.handler.ServerManagementAuthHandler;
import com.liji.proxy.server.management.handler.ServerManagementNewProxyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
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
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端管理服务
 * 负责与client建立指令通讯通道，指令是指Message.proto里的所有message
 *
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ServerManagementImpl implements ServerManagement {

    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getInstance();

    public ServerManagementImpl() {

    }

    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("managementServerBoss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("managementServerWorker"));

        try {
            ServerManagementAuthHandler authHandler = new ServerManagementAuthHandler(this);
            ServerManagementNewProxyHandler newProxyHandler = new ServerManagementNewProxyHandler(this);

            serverBootstrap
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("managementServerLog", GlobalSharableHandlerFactory.getLoggingHandler());

                            //protobuf编解码器
                            pipeline.addLast("managementServerFrameDecoder", new ProtobufVarint32FrameDecoder());
                            pipeline.addLast("managementMessageDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast("managementServerFrameEncoder", new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast("managementServerMessageEncoder", new ProtobufEncoder());

                            pipeline.addLast("managementServerAuth", authHandler);
                            pipeline.addLast("managementServerMessageHandler", newProxyHandler);
                            pipeline.addLast("managementServerException", GlobalSharableHandlerFactory.getExceptionHandler());
                        }
                    });
            int port = serverApplicationContext.getServerConfig().getServerManagement().getPort();
            ChannelFuture future = serverBootstrap.bind(port).sync();
            LOGGER.info("serverManagement start successful ar port " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public boolean authenticate(MessageProto.Authentication authentication) {
        return serverApplicationContext.getServerConfig().getServerManagementSecret().equals(authentication.getAuthSecret());
    }

    @Override
    public void newServerProxy(Proxy proxy, Channel channel, MessageProto.Header header) {
        serverApplicationContext.getProxyContext().newProxyServer(proxy, channel, header);
    }
}

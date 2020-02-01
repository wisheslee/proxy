package com.liji.proxy.server.management;

import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.server.common.context.ProxyContext;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.management.handler.ServerManagementAuthHandler;
import com.liji.proxy.server.management.handler.ServerManagementChannelCloseHandler;
import com.liji.proxy.server.management.handler.ServerManagementNewProxyHandler;
import com.liji.proxy.server.proxy.ServerProxy;
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

import java.net.InetSocketAddress;
import java.util.List;

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
        NioEventLoopGroup boss = new NioEventLoopGroup(0, new DefaultThreadFactory("serverManagementBoss"));
        NioEventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("serverManagementWorker"));

        try {
            ServerManagementAuthHandler authHandler = new ServerManagementAuthHandler(this);
            ServerManagementNewProxyHandler newProxyHandler = new ServerManagementNewProxyHandler(this);
            ServerManagementChannelCloseHandler serverManagementChannelCloseHandler = new ServerManagementChannelCloseHandler(this);

            serverBootstrap
                    .channel(NioServerSocketChannel.class)
                    .group(boss, worker)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("serverManagementLog", GlobalSharableHandlerFactory.getLoggingHandler());

                            //protobuf编解码器
                            pipeline.addLast("serverManagementFrameDecoder", new ProtobufVarint32FrameDecoder());
                            pipeline.addLast("managementMessageDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast("serverManagementFrameEncoder", new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast("serverManagementMessageEncoder", new ProtobufEncoder());

                            pipeline.addLast("serverManagementAuth", authHandler);
                            pipeline.addLast("serverManagementMessageHandler", newProxyHandler);
                            pipeline.addLast("serverManagementChannelCloseHandler", serverManagementChannelCloseHandler);

                            pipeline.addLast("serverManagementException", GlobalSharableHandlerFactory.getExceptionHandler());
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
        serverApplicationContext.getProxyContext().newServerProxy(proxy, channel, header);
    }

    @Override
    public void closeClientProxyList(Channel serverManagementChannel) {
        ProxyContext proxyContext = serverApplicationContext.getProxyContext();
        InetSocketAddress address = (InetSocketAddress) serverManagementChannel.remoteAddress();
        List<ServerProxy> clientAllProxyList = proxyContext.getClientAllProxyList(address);
        proxyContext.removeClient(address);
        for (ServerProxy serverProxy : clientAllProxyList) {
            LOGGER.info("close serverProxy at port {}", serverProxy.getProxy().getProxyPort());
            serverProxy.getServerProxyChannel().close();
        }


    }
}

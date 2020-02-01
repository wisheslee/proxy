package com.liji.proxy.client.management;

import com.liji.proxy.client.common.config.ClientConfig;
import com.liji.proxy.client.common.context.ClientApplicationContext;
import com.liji.proxy.client.common.context.ClientApplicationContextImpl;
import com.liji.proxy.client.management.handler.ServerManagementClientNewConnectionFromProxyHandler;
import com.liji.proxy.client.management.handler.ServerManagementClientResponseHandler;
import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.model.Server;
import com.liji.proxy.common.utils.MessageFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * client与server保持长连接，用来做管理消息的交换。数据通道需要额外建立
 * client与server使用protobuf通信
 *
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ServerManagementClientImpl implements ServerManagementClient {

    private ClientApplicationContext clientApplicationContext = ClientApplicationContextImpl.getInstance();

    public ServerManagementClientImpl() {
    }

    @Override
    public void start() {
        ClientConfig clientConfig = clientApplicationContext.getClientConfig();
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("clientGroup"));

        ChannelHandler clientResponseHandler = new ServerManagementClientResponseHandler(this);
        ChannelHandler newConnectionFromProxyHandler = new ServerManagementClientNewConnectionFromProxyHandler(this);

        try {
            bootstrap
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //log
                            pipeline.addLast("clientLog", GlobalSharableHandlerFactory.getLoggingHandler());
                            //protobuf
                            pipeline.addLast("clientProtoFrameDecoder", new ProtobufVarint32FrameDecoder());
                            pipeline.addLast("clientProtoMessageDecoder", new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast("clientProtoFrameEncoder", new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast("clientProtoMessageEncoder", new ProtobufEncoder());
                            //biz
                            pipeline.addLast("clientResponseHandler", clientResponseHandler);
                            pipeline.addLast("clientNewConnectionFromProxyHandler", newConnectionFromProxyHandler);
                            //exception
                            pipeline.addLast("clientException", GlobalSharableHandlerFactory.getExceptionHandler());
                        }
                    });
            //连接到远程服务，sync表示将连接操作同步化
            Server serverManagement = clientConfig.getServerManagement();
            ChannelFuture channelFuture = bootstrap.connect(serverManagement.getHost(), serverManagement.getPort()).sync();

            LOGGER.info("client start successful");

            //发送secret进行认证
            sendAuth(channelFuture.channel());

            //根据配置新建代理
            for (Proxy proxy : clientConfig.getProxyList()) {
                // TODO: jili 2020/2/1 建立成功后放入 clientProxyContext
                newProxyClient(channelFuture.channel(), proxy);
            }
            //阻塞一直到channel被关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            //channel关闭后，关闭线程组，结束java进程
            eventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    public void sendAuth(Channel clientChannel) {
        String secret = clientApplicationContext.getClientConfig().getServerManagementSecret();
        MessageProto.Authentication authentication = MessageProto.Authentication.newBuilder().setAuthSecret(secret).build();
        clientChannel.writeAndFlush(MessageFactory.newMessage(authentication));
    }

    @Override
    public void newProxyClient(Channel clientChannel, Proxy proxy) {
        MessageProto.NewProxy newProxy = MessageProto.NewProxy.newBuilder()
                .setProxyPort(proxy.getProxyPort())
                .setLocalHost(proxy.getLocalServer().getHost())
                .setLocalPort(proxy.getLocalServer().getPort())
                .build();
        clientChannel.writeAndFlush(MessageFactory.newMessage(newProxy));
    }

    @Override
    public void handleResponse(MessageProto.Response response) {
        if (response.getStatus() != DefaultConstants.SUCCESS_STATUS) {
            LOGGER.error("operation fail, message = {}", response.getMsg());
        }
    }
}

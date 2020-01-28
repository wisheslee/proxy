package com.liji.proxy.client;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.liji.proxy.client.handler.ClientMessageHandler;
import com.liji.proxy.common.constants.ChannelConstants;
import com.liji.proxy.common.model.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;

/**
 * client与server保持长连接，用来做管理消息的交换。数据通道需要额外建立
 * client与server使用protobuf通信
 *
 * @author jili
 * @date 2020/1/16
 */
public class Client {
    /**
     * 代理的本地服务地址
     */
    private static String localHost;
    /**
     * 代理的本地服务端口
     */
    private static int localPort;

    /**
     * 要在远程服务器打开的代理端口
     */
    private static int proxyPort;

    public Client(String localHost, int localPort, int proxyPort) {
        Client.localHost = localHost;
        Client.localPort = localPort;
        Client.proxyPort = proxyPort;
    }

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("client"));
        try {
            bootstrap
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            //需要处理的消息
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new ClientMessageHandler());
                        }
                    });
            //连接到远程服务，sync表示将连接操作同步化
            ChannelFuture channelFuture = bootstrap.connect(ChannelConstants.getServerHost(), ChannelConstants.getServerManagementPort()).sync();
            MessageProto.NewProxy newProxy = MessageProto.NewProxy.newBuilder().setProxyPort(proxyPort).setLocalPort(localPort).setLocalHost(localHost).build();
            channelFuture.channel().writeAndFlush(MessageProto.Message.newBuilder().setMessageBody(Any.pack(newProxy)).build());

            //阻塞一直到channel被关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //channel关闭后，关闭线程组，结束java进程
            eventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InvalidProtocolBufferException, InterruptedException {
        new Client("127.0.0.1", 8080, 30000).start();
    }

    public static String getLocalHost() {
        return localHost;
    }

    public static int getLocalPort() {
        return localPort;
    }

    public static int getProxyPort() {
        return proxyPort;
    }
}

package com.liji.proxy.client;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.liji.proxy.client.handler.ClientMessageHandler;
import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.utils.MessageFactory;
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

/**
 * client与server保持长连接，用来做管理消息的交换。数据通道需要额外建立
 * client与server使用protobuf通信
 *
 * @author jili
 * @date 2020/1/16
 */
public class ClientApplication {
    /**
     * 代理的本地服务地址
     */
    private String localHost;
    /**
     * 代理的本地服务端口
     */
    private int localPort;

    /**
     * 要在远程服务器打开的代理端口
     */
    private int proxyPort;

    private String serverHost;

    private int managementPort;

    private int dataPort;

    public ClientApplication(String localHost, int localPort, int proxyPort, String serverHost, int managementPort, int dataPort) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.proxyPort = proxyPort;
        this.serverHost = serverHost;
        this.managementPort = managementPort;
        this.dataPort = dataPort;
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
                            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            //protobuf
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new ClientMessageHandler());
                        }
                    });
            //连接到远程服务，sync表示将连接操作同步化
            ChannelFuture channelFuture = bootstrap.connect(this.serverHost, this.managementPort).sync();
            MessageProto.NewProxy newProxy = MessageProto.NewProxy.newBuilder().setProxyPort(proxyPort).setLocalPort(localPort).setLocalHost(localHost).build();
            channelFuture.channel().writeAndFlush(MessageFactory.newMessage(newProxy));
            //阻塞一直到channel被关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //channel关闭后，关闭线程组，结束java进程
            eventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InvalidProtocolBufferException, InterruptedException {
        new ClientApplication("127.0.0.1", 8080, 30000, DefaultConstants.SERVER_HOST,
                DefaultConstants.SERVER_MANAGEMENT_PORT, DefaultConstants.SERVER_DATA_PORT).start();
    }

}

package com.liji.proxy.client;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.liji.proxy.client.handler.ClientMessageHandler;
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
import io.netty.util.concurrent.DefaultThreadFactory;

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
    private String localHost;
    /**
     * 代理的本地服务端口
     */
    private int localPort;
    /**
     * 代理的远程服务器地址
     */
    private String remoteHost;
    /**
     * 远程服务器代理端口
     */
    private int remotePort;

    /**
     * 要在远程服务器打开的代理端口
     */
    private int proxyPort;

    public Client(String localHost, int localPort, String remoteHost, int remotePort, int proxyPort) {
        this.localHost = localHost;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.proxyPort = proxyPort;
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
                            //需要处理的消息
                            pipeline.addLast(new ProtobufVarint32FrameDecoder());
                            pipeline.addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
                            pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
                            pipeline.addLast(new ProtobufEncoder());

                            pipeline.addLast(new ClientMessageHandler());
                        }
                    });
            //连接到远程服务，sync表示将连接操作同步化
            ChannelFuture channelFuture = bootstrap.connect(remoteHost, remotePort).sync();
            channelFuture.channel().writeAndFlush(MessageProto.Message.newBuilder().setMessageBody(Any.pack( MessageProto.Authentication.newBuilder().setUsername("liji").setPassword("password").build())).build());

            //阻塞一直到channel被关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //channel关闭后，关闭线程组，结束java进程
            eventLoopGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InvalidProtocolBufferException, InterruptedException {
        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setReqId(1L)
                .setTimestamp(System.currentTimeMillis())
                .setVersion(1)
                .setMessageBody(Any.pack(MessageProto.Authentication.newBuilder().setUsername("liji").setPassword("passwd").build()))
                .build();
        System.out.println("message=");
        System.out.println(message.toString());
        MessageProto.Message message1 = MessageProto.Message.parseFrom(message.getMessageBody().toByteArray());
        System.out.println("message1=");
        System.out.println(message1.toString());
        if (message1.getMessageBody().is(MessageProto.Authentication.class)) {
            MessageProto.Authentication authentication = message1.getMessageBody().unpack(MessageProto.Authentication.class);
            System.out.println("authentication=");
            System.out.println(authentication.toString());
        } else {
            System.out.println("type invalid");
        }
//        new Client("127.0.0.1", 8081, "127.0.0.1", 8888, 30000).start();
    }
}

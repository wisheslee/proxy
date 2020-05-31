package com.liji.proxy.client.data;

import com.liji.proxy.client.common.context.ClientApplicationContext;
import com.liji.proxy.client.common.context.ClientApplicationContextImpl;
import com.liji.proxy.client.data.handler.ServerDataClientTransferToLocalServerHandler;
import com.liji.proxy.client.local.LocalServerClient;
import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import com.liji.proxy.common.utils.MessageResponseFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author jili
 * @date 2020/1/31
 */
@Slf4j
public class ServerDataClientImpl implements ServerDataClient {

    private ClientApplicationContext clientApplicationContext = ClientApplicationContextImpl.getInstance();
    private Channel localServerClientChannel;
    private Channel channel;

    public ServerDataClientImpl(LocalServerClient localServerClient, Channel managementClientChannel, String reqId, MessageProto.Header header) {


        setLocalServerClientChannel(localServerClient.getChannel());

        Server serverData = clientApplicationContext.getClientConfig().getServerData();

        //向server数据服务接口发起连接
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(managementClientChannel.eventLoop());

        ServerDataClient that = this;

        ChannelFuture connectFuture = bootstrap
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                        ch.attr(AttributeKey.valueOf("localServerClientChannel")).set(localServerClientChannel);

                        ChannelPipeline pipeline = ch.pipeline();
                        //log
                        pipeline.addLast("serverDataClientLog", GlobalSharableHandlerFactory.getLoggingHandler());
                        //biz
                        pipeline.addLast("serverDataClientTransfer", new ServerDataClientTransferToLocalServerHandler(that));
                        //exception
                        pipeline.addLast("serverDataClientException", GlobalSharableHandlerFactory.getExceptionHandler());

                    }
                }).connect(serverData.getHost(), serverData.getPort());

        channel = connectFuture.channel();
        localServerClient.setServerDataClientChannel(channel);

        connectFuture.addListener(new ChannelFutureListener() {
            @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    //往localServerClientChannel里面塞serverDataClientChannel
                    localServerClientChannel.attr(AttributeKey.valueOf("serverDataClientChannel")).set(future.channel());
                    managementClientChannel.writeAndFlush(MessageResponseFactory.success(header));
                    //给serverData发送reqId
                    future.channel().writeAndFlush(future.channel().alloc().buffer().writeBytes(reqId.getBytes(StandardCharsets.UTF_8)));
                    localServerClientChannel.read();
                } else {
                    Throwable cause = future.cause();
                    LOGGER.error(cause.getMessage(), cause);
                    managementClientChannel.writeAndFlush(MessageResponseFactory.fail(header, cause.getMessage()));
                }

            }
        });
        connectFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void transferToLocalServer(Object msg) {
        localServerClientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    channel.read();
                }
            }
        });
    }

    @Override
    public void setLocalServerClientChannel(Channel localServerClientChannel) {
        this.localServerClientChannel = localServerClientChannel;
    }

    @Override
    public Channel getLocalServerClientChannel() {
        return localServerClientChannel;
    }
}

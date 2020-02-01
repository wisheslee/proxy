package com.liji.proxy.client.local;

import com.liji.proxy.client.common.context.ClientApplicationContext;
import com.liji.proxy.client.common.context.ClientApplicationContextImpl;
import com.liji.proxy.client.data.ServerDataClient;
import com.liji.proxy.client.data.ServerDataClientImpl;
import com.liji.proxy.client.local.handler.LocalServerTransferToServerDataHandler;
import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import com.liji.proxy.common.utils.MessageResponseFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * 连接到代理的本地服务
 *
 * @author jili
 * @date 2020/1/29
 */
@Slf4j
public class LocalServerClientImpl implements LocalServerClient {

    private ClientApplicationContext clientApplicationContext = ClientApplicationContextImpl.getInstance();
    private Channel serverDataClientChannel;
    private Channel channel;

    public LocalServerClientImpl(Channel managementClientChannel, Server localServer, String reqId, MessageProto.Header header) {
        Bootstrap localServerBootstrap = new Bootstrap();
        localServerBootstrap.group(managementClientChannel.eventLoop());

        LocalServerClient that = this;

        ChannelFuture localServerConnectFuture = localServerBootstrap
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //log
                        ch.pipeline().addLast("localServerClientLog", GlobalSharableHandlerFactory.getLoggingHandler());
                        //biz
                        ch.pipeline().addLast("localServerTransfer", new LocalServerTransferToServerDataHandler(that));
                        //exception
                        ch.pipeline().addLast("localServerClientException", GlobalSharableHandlerFactory.getExceptionHandler());
                    }
                })
                .connect(localServer.getHost(), localServer.getPort());

        this.channel = localServerConnectFuture.channel();

        localServerConnectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    newClientToServerData(future.channel(), reqId, header);
                } else {
                    Throwable cause = future.cause();
                    LOGGER.error(cause.getMessage(), cause);
                    managementClientChannel.writeAndFlush(MessageResponseFactory.fail(header, cause.getMessage()));
                }
            }
        });
        localServerConnectFuture.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void newClientToServerData(Channel managementClientChannel, String reqId, MessageProto.Header header) {
        new ServerDataClientImpl(this, managementClientChannel, reqId, header);
    }

    @Override
    public void transferToServerData(Object msg) {
        serverDataClientChannel.writeAndFlush(msg);
    }

    @Override
    public void setServerDataClientChannel(Channel serverDataClientChannel) {
        this.serverDataClientChannel = serverDataClientChannel;
    }

    @Override
    public Channel getServerDataClientChannel() {
        return serverDataClientChannel;
    }
}

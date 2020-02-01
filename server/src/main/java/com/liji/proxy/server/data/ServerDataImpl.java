package com.liji.proxy.server.data;

import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.server.common.context.ProxyConnection;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.data.handler.TransferProxyConnectionDataToClientHandler;
import com.liji.proxy.server.data.handler.ServerDataNewConnectionFromClientHandler;
import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;


/**
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class ServerDataImpl implements ServerData {

    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getInstance();

    public ServerDataImpl() {
    }

    @Override
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("dataServerBoss"));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("dataServerWorker"));
        ServerDataImpl that = this;
        try {
            int port = serverApplicationContext.getServerConfig().getServerData().getPort();
            ChannelFuture connectFuture = serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("serverDataLog", GlobalSharableHandlerFactory.getLoggingHandler());
                            ch.pipeline().addLast("serverDataNewConnectionHandler", new ServerDataNewConnectionFromClientHandler(that));
                            ch.pipeline().addLast("serverDataTransferHandler", new TransferProxyConnectionDataToClientHandler(that));
                            ch.pipeline().addLast("serverDataException", GlobalSharableHandlerFactory.getExceptionHandler());
                        }
                        // TODO: jili 2020/1/29 auto_read 是否必要
                    })
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(port)
                    .sync();
            LOGGER.info("serverData start successful at port " + port);
            connectFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void handleClientNewConnection(String reqId, Channel serverDataChannel) {
        ProxyConnection proxyConnection = serverApplicationContext.getConnectionContext().getConnection(reqId);
        serverDataChannel.attr(PROXY_CONNECTION_KEY).set(proxyConnection);
        proxyConnection.getProxyConnectionChannel().attr(ServerProxy.dataServerChannelKey).set(serverDataChannel);
        proxyConnection.getServerProxy().startRead(proxyConnection.getProxyConnectionChannel());
    }


    @Override
    public void transferToProxy(Channel proxyConnectionChannel, Object msg) {
        proxyConnectionChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().read();
            }
        });
    }
}

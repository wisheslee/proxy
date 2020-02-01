package com.liji.proxy.server.proxy.handler;

import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;


/**
 * @author jili
 * @date 2020/1/28
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerProxyTransferDataToServerDataHandler extends ChannelInboundHandlerAdapter {

    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //getServerDataChannel
        Channel serverDataChannel = ctx.channel().attr(ServerProxy.dataServerChannelKey).get();

        int proxyLocalPort = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
        ServerProxy serverProxy = getServerProxy(proxyLocalPort);
        serverProxy.transferDataToServerData(serverDataChannel, msg);
    }

    private ServerProxy getServerProxy(int port) {
        return serverApplicationContext.getProxyContext().getServerProxy(port);
    }
}

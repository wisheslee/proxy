package com.liji.proxy.server.proxy.handler;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.server.common.context.*;
import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * 处理代理收到的新请求
 *
 * @author jili
 * @date 2020/1/23
 */
@ChannelHandler.Sharable
public class ServerProxyNewConnectionHandler extends ChannelInboundHandlerAdapter {

    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getServerApplicationContext();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //有新连接，需要在连接建立后，让client和serverData建立连接
        int proxyLocalPort = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
        ServerProxy serverProxy = getServerProxy(proxyLocalPort);
        serverProxy.notifyServerManagementNewConnection(ctx.channel());
    }

    private ServerProxy getServerProxy(int port) {
        return serverApplicationContext.getProxyContext().getServerProxy(port);
    }
}

package com.liji.proxy.server.data.handler;

import com.liji.proxy.server.common.context.ProxyConnection;
import com.liji.proxy.server.data.ServerData;
import io.netty.channel.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class TransferProxyConnectionDataToClientHandler extends ChannelInboundHandlerAdapter {

    private ServerData serverData;

    public TransferProxyConnectionDataToClientHandler(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ProxyConnection proxyConnection = ctx.channel().attr(ServerData.PROXY_CONNECTION_KEY).get();
        serverData.transferProxyConnectionDataToClient(proxyConnection.getProxyConnectionChannel(), msg);
    }
}


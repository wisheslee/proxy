package com.liji.proxy.server.proxy.handler;

import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;


/**
 * @author jili
 * @date 2020/1/28
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerProxyTransferDataToServerDataHandler extends ChannelInboundHandlerAdapter {

    private ServerProxy serverProxy;

    public ServerProxyTransferDataToServerDataHandler(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    public ServerProxyTransferDataToServerDataHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        serverProxy.transferDataToServerData(ctx.channel(), msg);
    }
}

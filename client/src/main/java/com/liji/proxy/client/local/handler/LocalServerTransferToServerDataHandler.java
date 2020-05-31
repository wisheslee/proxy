package com.liji.proxy.client.local.handler;

import com.liji.proxy.client.local.LocalServerClient;
import io.netty.channel.*;

/**
 * @author jili
 * @date 2020/1/28
 */
public class LocalServerTransferToServerDataHandler extends ChannelInboundHandlerAdapter {

    private LocalServerClient localServerClient;

    public LocalServerTransferToServerDataHandler(LocalServerClient localServerClient) {
        this.localServerClient = localServerClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        localServerClient.transferToServerData(msg, ctx.channel());
    }
}

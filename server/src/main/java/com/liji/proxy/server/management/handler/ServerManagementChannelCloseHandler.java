package com.liji.proxy.server.management.handler;

import com.liji.proxy.server.management.ServerManagement;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/2/2
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerManagementChannelCloseHandler extends ChannelInboundHandlerAdapter {

    private ServerManagement serverManagement;

    public ServerManagementChannelCloseHandler(ServerManagement serverManagement) {
        this.serverManagement = serverManagement;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("a client closed, address = {}", ctx.channel().remoteAddress().toString());
        serverManagement.closeClientProxyList(ctx.channel());
        super.channelInactive(ctx);
    }
}

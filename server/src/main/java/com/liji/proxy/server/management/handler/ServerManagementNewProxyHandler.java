package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.server.management.ServerManagement;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerManagementNewProxyHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private ServerManagement serverManagement;

    public ServerManagementNewProxyHandler(ServerManagement serverManagement) {
        this.serverManagement = serverManagement;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getBody().is(MessageProto.NewProxy.class)) {
            LOGGER.info("receive newProxy message");
            MessageProto.NewProxy newProxy = msg.getBody().unpack(MessageProto.NewProxy.class);
            //在指定端口新开一个serverBootstrap
            serverManagement.newServerProxy(Proxy.from(newProxy), ctx.channel(), msg.getHeader());
        }
    }


}

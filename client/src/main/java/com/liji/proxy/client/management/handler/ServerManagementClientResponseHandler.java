package com.liji.proxy.client.management.handler;

import com.liji.proxy.client.management.ServerManagementClient;
import com.liji.proxy.common.model.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/31
 */
@Slf4j
public class ServerManagementClientResponseHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private ServerManagementClient client;

    public ServerManagementClientResponseHandler(ServerManagementClient client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getBody().is(MessageProto.Response.class)) {
            MessageProto.Response response = msg.getBody().unpack(MessageProto.Response.class);
            client.handleResponse(response);
        }
        ctx.fireChannelRead(msg);
    }
}

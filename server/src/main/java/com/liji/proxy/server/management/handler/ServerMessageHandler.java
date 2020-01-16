package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.model.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ServerMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getMessageBody().is(MessageProto.Authentication.class)) {
            MessageProto.Authentication authentication = msg.getMessageBody().unpack(MessageProto.Authentication.class);
            LOGGER.info("username={}, password={}", authentication.getUsername(), authentication.getPassword());
            ctx.writeAndFlush(MessageProto.Response.newBuilder().setStatus(200));
        }
    }
}

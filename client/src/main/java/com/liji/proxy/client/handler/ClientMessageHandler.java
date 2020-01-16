package com.liji.proxy.client.handler;

import com.liji.proxy.common.model.MessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
public class ClientMessageHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getMessageBody().is(MessageProto.Response.class)) {
            MessageProto.Response response = msg.getMessageBody().unpack(MessageProto.Response.class);
            LOGGER.info("response = {}", response);
        } else {
            LOGGER.info("receive a message={}", msg);
        }
    }
}

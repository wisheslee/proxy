package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.common.utils.MessageResponseFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author jili
 * @date 2020/1/29
 */
@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<MessageProto.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if (msg.getBody().is(MessageProto.Authentication.class)) {
            LOGGER.info("receive auth message");
            MessageProto.Authentication authentication = msg.getBody().unpack(MessageProto.Authentication.class);
            if (DefaultConstants.AUTH_SECRET.equals(authentication.getAuthSecret())) {
                ctx.writeAndFlush(MessageResponseFactory.success(msg.getHeader()));
                ctx.pipeline().remove(this);
            } else {
                LOGGER.info("client发送了错误的密码，client ip = {}, port = {}", socketAddress.getHostName(), socketAddress.getPort());
                ctx.close();
            }
        } else {
            LOGGER.error("client第一条消息不是auth message, client ip = {}, port = {}", socketAddress.getHostName(), socketAddress.getPort());
            ctx.close();
        }
    }
}

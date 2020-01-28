package com.liji.proxy.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author jili
 * @date 2020/1/28
 */
public class LocalServerHandler extends ChannelInboundHandlerAdapter {

    private Channel toServerChannel;

    public LocalServerHandler(Channel toServerChannel) {
        this.toServerChannel = toServerChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        toServerChannel.writeAndFlush(msg);
    }
}

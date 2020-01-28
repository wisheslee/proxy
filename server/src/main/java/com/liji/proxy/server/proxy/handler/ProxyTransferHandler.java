package com.liji.proxy.server.proxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * @author jili
 * @date 2020/1/28
 */
public class ProxyTransferHandler extends ChannelInboundHandlerAdapter {
    private Channel toClientChannel;

    public ProxyTransferHandler(Channel toClientChannel) {
        this.toClientChannel = toClientChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        toClientChannel.writeAndFlush(msg);
        ctx.read();
        super.channelRead(ctx, msg);
    }
}

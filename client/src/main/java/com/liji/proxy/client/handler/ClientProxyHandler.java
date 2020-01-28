package com.liji.proxy.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author jili
 * @date 2020/1/27
 */
public class ClientProxyHandler extends ChannelInboundHandlerAdapter {

    private Channel localServerChannel;

    public ClientProxyHandler(Channel localServerChannel) {
        this.localServerChannel = localServerChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        localServerChannel.writeAndFlush(msg);
        ctx.channel().read();
        super.channelRead(ctx, msg);
    }
}

package com.liji.proxy.server.proxy.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;


/**
 * @author jili
 * @date 2020/1/28
 */
@Slf4j
public class ProxyTransferHandler extends ChannelInboundHandlerAdapter {
    private Channel toClientChannel;

    public ProxyTransferHandler(Channel toClientChannel) {
        this.toClientChannel = toClientChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        toClientChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ctx.channel().read();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }
}

package com.liji.proxy.client.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class ClientProxyHandler extends ChannelInboundHandlerAdapter {

    private Channel localServerChannel;

    public ClientProxyHandler(Channel localServerChannel) {
        this.localServerChannel = localServerChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        localServerChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
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

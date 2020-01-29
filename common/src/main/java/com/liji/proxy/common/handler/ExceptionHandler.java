package com.liji.proxy.common.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * @author jili
 * @date 2020/1/29
 */

@Slf4j
public class ExceptionHandler extends ChannelDuplexHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise.addListener(outBoundExceptionListener()));
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.bind(ctx, localAddress, promise.addListener(outBoundExceptionListener()));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise.addListener(outBoundExceptionListener()));
    }

    private ChannelFutureListener outBoundExceptionListener() {
        return new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOGGER.error(future.cause().getMessage(), future.cause());
                    future.channel().close();
                }
            }
        };
    }
}

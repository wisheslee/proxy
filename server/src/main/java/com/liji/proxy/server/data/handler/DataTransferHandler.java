package com.liji.proxy.server.data.handler;

import com.liji.proxy.server.proxy.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;

    public DataTransferHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        connection.getServerProxyChannel().writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ctx.channel().read();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }
}


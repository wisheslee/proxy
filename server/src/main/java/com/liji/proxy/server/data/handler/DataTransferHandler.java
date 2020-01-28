package com.liji.proxy.server.data.handler;

import com.liji.proxy.server.proxy.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author jili
 * @date 2020/1/27
 */
public class DataTransferHandler extends ChannelInboundHandlerAdapter {

    private Connection connection;

    public DataTransferHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        connection.getServerProxyChannel().writeAndFlush(msg);
        super.channelRead(ctx, msg);
    }
}

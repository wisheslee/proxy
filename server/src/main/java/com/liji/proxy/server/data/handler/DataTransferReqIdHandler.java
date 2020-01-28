package com.liji.proxy.server.data.handler;

import com.liji.proxy.server.proxy.Connection;
import com.liji.proxy.server.proxy.ConnectionContext;
import com.liji.proxy.server.proxy.handler.ProxyTransferHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author jili
 * @date 2020/1/27
 */
public class DataTransferReqIdHandler extends ChannelInboundHandlerAdapter {

    private final int reqIdLength = 36;
    private ByteBuf innerBuffer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().read();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (innerBuffer == null) {
            innerBuffer = ctx.alloc().buffer(36);
        }
        ByteBuf byteMsg = (ByteBuf) msg;
        innerBuffer.writeBytes(byteMsg);
        if (innerBuffer.readableBytes() < reqIdLength) {
            ctx.channel().read();
            return;
        }
        String reqId = innerBuffer.readBytes(36).toString(StandardCharsets.UTF_8);
        Connection connection = ConnectionContext.get(reqId);
        ctx.pipeline().addLast(new DataTransferHandler(connection));

        ctx.fireChannelRead(innerBuffer);
        ctx.pipeline().remove(this);
        connection.getServerProxyChannel().pipeline().addLast(new ProxyTransferHandler(ctx.channel()));
    }
}

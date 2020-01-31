package com.liji.proxy.server.data.handler;

import com.liji.proxy.server.common.context.ProxyConnection;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.data.ServerData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author jili
 * @date 2020/1/27
 */
public class ServerDataNewConnectionFromClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf innerBuffer;
    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getServerApplicationContext();
    private ServerData serverData;

    public ServerDataNewConnectionFromClientHandler(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        int reqIdLength = serverApplicationContext.getServerConfig().getServerDataReqIdLength();
        if (innerBuffer == null) {
            innerBuffer = ctx.alloc().buffer(36);
        }
        ByteBuf byteMsg = (ByteBuf) msg;
        innerBuffer.writeBytes(byteMsg);
        if (innerBuffer.readableBytes() < reqIdLength) {
            //数据不够，再读数据
            ctx.channel().read();
            return;
        }

        String reqId = innerBuffer.readBytes(reqIdLength).toString(StandardCharsets.UTF_8);
        serverData.handleClientNewConnection(reqId, ctx.channel());

        ctx.channel().read();
        if (innerBuffer.readableBytes() > 0) {
            ctx.fireChannelRead(innerBuffer.readRetainedSlice(innerBuffer.readableBytes()));
            innerBuffer.release();
        }
        ctx.pipeline().remove(this);
    }
}

package com.liji.proxy.server.proxy.handler;

import com.google.protobuf.Any;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.server.proxy.ConnectionContext;
import com.liji.proxy.server.proxy.Proxy;
import com.liji.proxy.server.proxy.ProxyContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author jili
 * @date 2020/1/23
 */
public class NewConnectionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //有新连接，需要在连接建立后，让client和server建立新通路
        int localPort = ((InetSocketAddress) ctx.channel().localAddress()).getPort();
        Proxy proxy = ProxyContext.get(localPort);
        String reqId = UUID.randomUUID().toString();
        ConnectionContext.putIfAbsent(reqId, ctx.channel());
        proxy.getChannel().writeAndFlush(MessageFactory.wrap(MessageFactory.newConnectionFromOuter(UUID.randomUUID().toString())));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}

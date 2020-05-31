package com.liji.proxy.server.data;


import com.liji.proxy.server.common.context.ProxyConnection;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 *
 *
 * @author jili
 * @date 2020/1/31
 */
public interface ServerData {

    AttributeKey<ProxyConnection> PROXY_CONNECTION_KEY = AttributeKey.valueOf("proxyConnection");

    void handleClientNewConnection(String reqId, Channel serverDataChannel);

    void transferToProxy(Channel proxyConnectionChannel, ChannelHandlerContext ctx, Object msg);

    void start();

}

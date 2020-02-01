package com.liji.proxy.server.common.context;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface ProxyContext {

    /**
     * 创建一个proxyServer，是一个同步操作，防止同一端口被重复绑定
     *
     * @author jili
     * @date 2020/1/30
     */
    void newServerProxy(Proxy proxy, Channel serverManagementChannel, MessageProto.Header header);

    ServerProxy getServerProxy(int port);

    void initClientProxyList(InetSocketAddress address);

    List<ServerProxy> getClientAllProxyList(InetSocketAddress address);

    void removeClient(InetSocketAddress address);
}

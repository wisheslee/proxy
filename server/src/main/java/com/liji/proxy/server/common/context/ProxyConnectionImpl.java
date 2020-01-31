package com.liji.proxy.server.common.context;

import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.Channel;
import lombok.Getter;


/**
 * @author jili
 * @date 2020/1/23
 */
public class ProxyConnectionImpl implements ProxyConnection {

    private ServerProxy serverProxy;
    private Channel proxyConnectionChannel;


    public ProxyConnectionImpl(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    @Override
    public void startRead() {
        proxyConnectionChannel.read();
    }

    @Override
    public ServerProxy getServerProxy() {
        return serverProxy;
    }

    @Override
    public Channel getProxyConnectionChannel() {
        return proxyConnectionChannel;
    }
}

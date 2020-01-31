package com.liji.proxy.server.common.context;

import com.liji.proxy.server.proxy.ServerProxy;
import io.netty.channel.Channel;

/**
 * @author jili
 * @date 2020/1/31
 */
public interface ProxyConnection {
    void startRead();

    ServerProxy getServerProxy();

    Channel getProxyConnectionChannel();

}

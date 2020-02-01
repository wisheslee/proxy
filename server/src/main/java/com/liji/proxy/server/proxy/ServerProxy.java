package com.liji.proxy.server.proxy;

import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.model.Server;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface ServerProxy {

    AttributeKey<Channel> dataServerChannelKey = AttributeKey.valueOf("dataServerChannel");

    void notifyServerManagementNewConnection(Channel proxyConnectionChannel);

    void startRead(Channel proxyConnectionChannel);

    void transferDataToServerData(Channel proxyToServerDataChannel, Object msg);

    Proxy getProxy();

    Channel getServerProxyChannel();
}

package com.liji.proxy.server.proxy;

import com.liji.proxy.common.model.Server;
import io.netty.channel.Channel;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface ServerProxy {

    void notifyServerManagementNewConnection(Channel proxyConnectionChannel);

    void startRead(Channel proxyConnectionChannel);

    void transferDataToServerData(Channel proxyToServerDataChannel, Object msg);
}

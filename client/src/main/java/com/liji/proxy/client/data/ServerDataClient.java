package com.liji.proxy.client.data;

import io.netty.channel.Channel;

/**
 * @author jili
 * @date 2020/1/31
 */
public interface ServerDataClient {

    void transferToLocalServer(Object msg);

    void setLocalServerClientChannel(Channel localServerClientChannel);

    Channel getLocalServerClientChannel();
}

package com.liji.proxy.server.proxy;

import io.netty.channel.Channel;
import lombok.Getter;


/**
 * @author jili
 * @date 2020/1/23
 */
@Getter
public class Connection {
    private Channel serverProxyChannel;

    public Connection(Channel channel) {
        this.serverProxyChannel = channel;
    }
}

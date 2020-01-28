package com.liji.proxy.server.proxy;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @author jili
 * @date 2020/1/23
 */
@Builder
@Getter
public class Proxy {
    private int proxyPort;
    private String remoteHost;
    private int remotePort;
    private Channel channel;
}

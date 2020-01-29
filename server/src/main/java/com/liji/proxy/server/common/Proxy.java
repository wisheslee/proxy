package com.liji.proxy.server.common;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;

/**
 * @author jili
 * @date 2020/1/23
 */
@Builder
@Getter
public class Proxy {
    private int proxyPort;
    private String localHost;
    private int localPort;
    private Channel channel;
}

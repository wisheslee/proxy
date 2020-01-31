package com.liji.proxy.common.model;

import lombok.Getter;

/**
 * @author jili
 * @date 2020/1/30
 */
@Getter
public class Proxy {
    private int proxyPort;
    private Server localServer;

    public Proxy(int proxyPort, Server localServer) {
        this.proxyPort = proxyPort;
        this.localServer = localServer;
    }

    public static Proxy from(MessageProto.NewProxy newProxy) {
        return new Proxy(newProxy.getProxyPort(), new Server(newProxy.getLocalHost(), newProxy.getLocalPort()));
    }
}

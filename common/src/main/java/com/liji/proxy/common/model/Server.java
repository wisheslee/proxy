package com.liji.proxy.common.model;

/**
 * @author jili
 * @date 2020/1/30
 */

public class Server {
    private String host;
    private int port;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

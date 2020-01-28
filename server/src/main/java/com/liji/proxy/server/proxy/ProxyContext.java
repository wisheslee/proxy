package com.liji.proxy.server.proxy;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jili
 * @date 2020/1/23
 */
public class ProxyContext {

    private static ConcurrentHashMap<Integer, Proxy> map = new ConcurrentHashMap<>();

    public static NioEventLoopGroup proxyBossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("proxyBossGroup"));
    public static NioEventLoopGroup proxyWorkerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("proxyWorkerGroup"));

    public static synchronized boolean putIfAbsent(int port, String remoteHost, int remotePort, Channel channel) {
        if (map.get(port) != null) {
            return false;
        }
        map.put(port, Proxy.builder().proxyPort(port).remoteHost(remoteHost).remotePort(remotePort).channel(channel).build());
        return true;
    }

    public static Proxy get(int port) {
        return map.get(port);
    }

}

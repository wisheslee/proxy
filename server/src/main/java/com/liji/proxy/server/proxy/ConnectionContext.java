package com.liji.proxy.server.proxy;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jili
 * @date 2020/1/23
 */
public class ConnectionContext {
    public static Map<String, Connection> map = new ConcurrentHashMap<>();

    public static synchronized boolean putIfAbsent(String reqId, Channel channel) {
        if (map.get(reqId) != null) {
            return false;
        }
        map.put(reqId, new Connection(channel));
        return true;
    }

    public static Connection get(String reqId) {
        return map.get(reqId);
    }

}

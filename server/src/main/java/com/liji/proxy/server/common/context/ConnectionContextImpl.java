package com.liji.proxy.server.common.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jili
 * @date 2020/1/30
 */
public class ConnectionContextImpl implements ConnectionContext{

    private static ConnectionContext connectionContext = new ConnectionContextImpl();

    public static ConnectionContext newInstance() {
        return connectionContext;
    }

    private Map<String, ProxyConnection> map = new ConcurrentHashMap<>();


    @Override
    public ProxyConnection getConnection(String reqId) {
        return map.get(reqId);
    }

    @Override
    public void newConnection(String reqId, ProxyConnection connection) {
        map.put(reqId, connection);
    }
}

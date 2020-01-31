package com.liji.proxy.server.common.context;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface ConnectionContext {
    void newConnection(String reqId, ProxyConnection connection);

    ProxyConnection getConnection(String reqId);
}

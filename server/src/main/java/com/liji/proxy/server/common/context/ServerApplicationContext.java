package com.liji.proxy.server.common.context;

import com.liji.proxy.server.common.config.ServerConfig;

/**
 * @author jili
 * @date 2020/1/31
 */
public interface ServerApplicationContext {

    ProxyContext getProxyContext();

    ConnectionContext getConnectionContext();

    ServerConfig getServerConfig();
}

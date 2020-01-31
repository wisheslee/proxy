package com.liji.proxy.server.common.context;

import com.liji.proxy.server.common.config.ServerConfig;
import com.liji.proxy.server.common.config.ServerConfigImpl;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ServerApplicationContextImpl implements ServerApplicationContext {
    private static ServerApplicationContext serverApplicationContext = new ServerApplicationContextImpl();
    private ConnectionContext connectionContext = new ConnectionContextImpl();
    private ProxyContext proxyContext = new ProxyContextImpl();
    private ServerConfig serverConfig = new ServerConfigImpl();

    private ServerApplicationContextImpl() {
    }

    public static ServerApplicationContext getServerApplicationContext() {
        return serverApplicationContext;
    }

    @Override
    public ProxyContext getProxyContext() {
        return proxyContext;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}

package com.liji.proxy.server.common.context;

import com.liji.proxy.common.config.ConfigResolver;
import com.liji.proxy.server.common.config.ServerConfig;
import com.liji.proxy.server.common.config.ServerConfigImpl;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ServerApplicationContextImpl implements ServerApplicationContext {

    private static ServerConfig serverConfig;
    private static ServerApplicationContext serverApplicationContext;

    static {
        serverConfig = new ServerConfigImpl();
        serverApplicationContext = new ServerApplicationContextImpl();
    }

    public static ServerApplicationContext getInstance() {
        return serverApplicationContext;
    }


    private ConnectionContext connectionContext = new ConnectionContextImpl();
    private ProxyContext proxyContext = new ProxyContextImpl();


    private ServerApplicationContextImpl() {
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

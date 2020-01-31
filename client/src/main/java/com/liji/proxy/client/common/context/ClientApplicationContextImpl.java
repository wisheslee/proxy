package com.liji.proxy.client.common.context;

import com.liji.proxy.client.common.config.ClientConfig;
import com.liji.proxy.client.common.config.ClientConfigImpl;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientApplicationContextImpl implements ClientApplicationContext{

    private static ClientApplicationContext clientApplicationContext = new ClientApplicationContextImpl();
    private ClientConfig clientConfig = new ClientConfigImpl();

    public static ClientApplicationContext newInstance() {
        return clientApplicationContext;
    }

    private ClientApplicationContextImpl() { }

    @Override
    public ClientConfig getClientConfig() {
        return clientConfig;
    }
}

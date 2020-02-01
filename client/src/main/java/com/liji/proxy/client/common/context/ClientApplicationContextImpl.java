package com.liji.proxy.client.common.context;

import com.liji.proxy.client.common.config.ClientConfig;
import com.liji.proxy.client.common.config.ClientConfigImpl;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientApplicationContextImpl implements ClientApplicationContext{

    static {
        clientConfig = new ClientConfigImpl();
        clientApplicationContext = new ClientApplicationContextImpl();
    }

    private static ClientConfig clientConfig;
    private static ClientApplicationContext clientApplicationContext;

    private ClientApplicationContextImpl() {
    }

    public static ClientApplicationContext getInstance() {
        return clientApplicationContext;
    }


    @Override
    public ClientConfig getClientConfig() {
        return clientConfig;
    }
}

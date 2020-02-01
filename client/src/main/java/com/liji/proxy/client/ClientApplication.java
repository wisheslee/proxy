package com.liji.proxy.client;

import com.liji.proxy.client.common.config.ClientConfig;
import com.liji.proxy.client.common.config.ClientConfigImpl;
import com.liji.proxy.client.common.context.ClientApplicationContext;
import com.liji.proxy.client.common.context.ClientApplicationContextImpl;
import com.liji.proxy.client.management.ServerManagementClient;
import com.liji.proxy.client.management.ServerManagementClientImpl;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientApplication {
    public static void main(String[] args) {
        ServerManagementClient client = new ServerManagementClientImpl();
        client.start();
    }
}

package com.liji.proxy.server;

import com.liji.proxy.server.common.config.ServerConfig;
import com.liji.proxy.server.common.config.ServerConfigImpl;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.data.ServerData;
import com.liji.proxy.server.data.ServerDataImpl;
import com.liji.proxy.server.management.ServerManagement;
import com.liji.proxy.server.management.ServerManagementImpl;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        ServerData serverData = new ServerDataImpl();
        new Thread(() -> serverData.start()).run();

        ServerManagement serverManagement = new ServerManagementImpl();
        serverManagement.start();
    }
}

package com.liji.proxy.server;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.server.data.ServerDataImpl;
import com.liji.proxy.server.management.ServerManagementImpl;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        Thread managementThread = new Thread(new ServerManagementImpl());
        Thread dataThread = new Thread(new ServerDataImpl());
        managementThread.start();
        dataThread.start();
        managementThread.join();
        dataThread.join();
    }
}

package com.liji.proxy.server;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.server.data.DataServer;
import com.liji.proxy.server.management.ManagementServer;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        Thread managementThread = new Thread(new ManagementServer(DefaultConstants.SERVER_MANAGEMENT_PORT));
        Thread dataThread = new Thread(new DataServer(DefaultConstants.SERVER_DATA_PORT));
        managementThread.start();
        dataThread.start();
        managementThread.join();
        dataThread.join();
    }
}

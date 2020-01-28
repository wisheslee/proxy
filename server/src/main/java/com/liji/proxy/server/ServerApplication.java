package com.liji.proxy.server;

import com.liji.proxy.server.data.DataTransferServer;
import com.liji.proxy.server.management.ManagementServer;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        new ManagementServer().start();
        new DataTransferServer().start();
    }
}

package com.liji.proxy.server;

import com.liji.proxy.server.data.DataTransferServer;
import com.liji.proxy.server.management.ManagementServer;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ServerApplication {
    public static void main(String[] args) throws InterruptedException {
        Thread manageThread = new Thread(() -> {
            try {
                new ManagementServer().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        Thread dataThread = new Thread(() -> {
            try {
                new DataTransferServer().start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        manageThread.start();
        dataThread.start();
        manageThread.join();
        dataThread.join();
    }
}

package com.liji.proxy.common.constants;

/**
 * @author jili
 * @date 2020/1/28
 */
public class ChannelConstants {
    private static String serverHost = "127.0.0.1";
    private static int serverManagementPort = 8888;
    private static int serverDataPort = 8889;

    public static String getServerHost() {
        return serverHost;
    }

    public static int getServerManagementPort() {
        return serverManagementPort;
    }

    public static int getServerDataPort() {
        return serverDataPort;
    }
}

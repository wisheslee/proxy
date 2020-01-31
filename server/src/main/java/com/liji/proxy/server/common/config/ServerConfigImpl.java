package com.liji.proxy.server.common.config;

import com.liji.proxy.common.model.Server;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ServerConfigImpl implements ServerConfig {
    @Override
    public Server getServerManagement() {
        return null;
    }

    @Override
    public Server getServerData() {
        return null;
    }

    @Override
    public String getServerManagementSecret() {
        return null;
    }

    @Override
    public int getServerDataReqIdLength() {
        return 0;
    }
}

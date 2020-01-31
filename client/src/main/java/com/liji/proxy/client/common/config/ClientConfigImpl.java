package com.liji.proxy.client.common.config;

import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.model.Server;

import java.util.List;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientConfigImpl implements ClientConfig {
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

    @Override
    public List<Proxy> getProxyList() {
        return null;
    }
}

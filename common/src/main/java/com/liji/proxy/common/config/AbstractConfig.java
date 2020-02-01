package com.liji.proxy.common.config;

import com.liji.proxy.common.model.Server;

import java.util.Map;

/**
 * @author jili
 * @date 2020/1/31
 */
public abstract class AbstractConfig implements Config {

    protected ConfigResolver configResolver;

    protected void initConfigResolver(Map<String, String> config) {
        this.configResolver = new ConfigResolverImpl(config);
    }

    @Override
    public Server getServerManagement() {
        String managementHost = configResolver.getString("management.host");
        int port = configResolver.getInt("management.port");
        return new Server(managementHost, port);
    }

    @Override
    public Server getServerData() {
        String dataHost = configResolver.getString("data.host");
        int port = configResolver.getInt("data.port");
        return new Server(dataHost, port);
    }

    @Override
    public String getServerManagementSecret() {
        return configResolver.getString("management.secret");
    }

    @Override
    public int getServerDataReqIdLength() {
        return configResolver.getInt("reqId.length");
    }
}

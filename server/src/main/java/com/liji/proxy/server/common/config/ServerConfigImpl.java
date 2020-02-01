package com.liji.proxy.server.common.config;

import com.liji.proxy.common.config.AbstractConfig;

import java.util.Map;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ServerConfigImpl extends AbstractConfig implements ServerConfig {

    public ServerConfigImpl() {
        Map<String, Object> config = new ServerConfigReader().getConfig();
        initConfigResolver(config);
    }
}

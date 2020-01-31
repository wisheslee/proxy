package com.liji.proxy.client.common.config;

import com.liji.proxy.common.config.Config;
import com.liji.proxy.common.model.Proxy;

import java.util.List;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface ClientConfig extends Config {
    List<Proxy> getProxyList();
}

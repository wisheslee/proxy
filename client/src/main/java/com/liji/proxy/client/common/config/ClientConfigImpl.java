package com.liji.proxy.client.common.config;

import com.liji.proxy.common.config.AbstractConfig;
import com.liji.proxy.common.exception.ConfigException;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.model.Server;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientConfigImpl extends AbstractConfig implements ClientConfig {

    private static final String PROXY_SEPARATOR = ":";

    public ClientConfigImpl() {
        Map<String, String> config = new ClientConfigReader().getConfig();
        initConfigResolver(config);
    }

    @Override
    public List<Proxy> getProxyList() {
        String key = "proxy";
        String str = configResolver.getString(key);
        if (str == null || str.length() == 0) {
            throw new ConfigException("请在client.config中必须配置proxy信息，格式参考注释");
        }
        List<String> proxyStrList = configResolver.getStringList(key);
        List<Proxy> proxyList = new LinkedList<>();
        for (String proxyStr : proxyStrList) {
            String[] proxyInfoArr = proxyStr.split(PROXY_SEPARATOR);
            Server localServer = new Server(proxyInfoArr[1], Integer.parseInt(proxyInfoArr[2]));
            proxyList.add(new Proxy(Integer.parseInt(proxyInfoArr[0]), localServer));
        }
        return proxyList;
    }
}

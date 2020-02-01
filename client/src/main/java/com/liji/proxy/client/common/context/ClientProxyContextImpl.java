package com.liji.proxy.client.common.context;

import com.liji.proxy.common.model.Proxy;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jili
 * @date 2020/1/31
 */
public class ClientProxyContextImpl implements ClientProxyContext {

    private Map<Integer, ProxyStatus> proxyStatusMap = new ConcurrentHashMap<>();

    @Override
    public Collection<ProxyStatus> getAllClientProxy() {
        return proxyStatusMap.values();
    }

    @Override
    public void addProxy(int proxyPort, ProxyStatus proxyStatus) {
        proxyStatusMap.put(proxyPort, proxyStatus);
    }

    @Override
    public void removeProxy(int proxyPort) {
        proxyStatusMap.remove(proxyPort);
    }
}

package com.liji.proxy.client.common.context;

import com.liji.proxy.common.model.Proxy;

import java.util.Collection;
import java.util.List;

/**
 * @author jili
 * @date 2020/1/31
 */
public interface ClientProxyContext {

    Collection<ProxyStatus> getAllClientProxy();

    void addProxy(int proxyPort, ProxyStatus proxyStatus);

    void removeProxy(int proxyPort);


}

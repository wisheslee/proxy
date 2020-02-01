package com.liji.proxy.client.common.context;

import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.model.Server;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jili
 * @date 2020/2/1
 */
@Getter
@Setter
public class ProxyStatus extends Proxy {
    private boolean ready;

    public ProxyStatus(int proxyPort, Server localServer) {
        super(proxyPort, localServer);
    }
}

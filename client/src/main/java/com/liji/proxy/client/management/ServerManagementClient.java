package com.liji.proxy.client.management;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import io.netty.channel.Channel;


/**
 * @author jili
 * @date 2020/1/31
 */

public interface ServerManagementClient {
    void newProxyClient(Channel clientChannel, Proxy proxy);

    void handleResponse(MessageProto.Response response);

}

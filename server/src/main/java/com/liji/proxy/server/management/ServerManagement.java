package com.liji.proxy.server.management;

import com.liji.proxy.common.Daemon;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import io.netty.channel.Channel;


/**
 * @author jili
 * @date 2020/1/31
 */
public interface ServerManagement extends Daemon {

    boolean authenticate(MessageProto.Authentication authentication);

    void newServerProxy(Proxy proxy, Channel channel, MessageProto.Header header);

    void closeClientProxyList(Channel serverManagementChannel);

}

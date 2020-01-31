package com.liji.proxy.client.local;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import io.netty.channel.Channel;

/**
 * @author jili
 * @date 2020/1/31
 */
public interface LocalServerClient {
    void newClientToServerData(Channel managementClientChannel, String reqId, MessageProto.Header header);

    void transferToServerData(Object msg);

    void setServerDataClientChannel(Channel serverDataClientChannel);

    Channel getServerDataClientChannel();

}

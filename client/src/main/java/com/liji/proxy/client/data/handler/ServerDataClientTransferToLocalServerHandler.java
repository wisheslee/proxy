package com.liji.proxy.client.data.handler;

import com.liji.proxy.client.data.ServerDataClient;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 收到serverData转发的serverProxy的数据，继续转发给localServer
 *
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class ServerDataClientTransferToLocalServerHandler extends ChannelInboundHandlerAdapter {

    private ServerDataClient serverDataClient;

    public ServerDataClientTransferToLocalServerHandler(ServerDataClient serverDataClient) {
        this.serverDataClient = serverDataClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        serverDataClient.transferToLocalServer(msg);
    }
}

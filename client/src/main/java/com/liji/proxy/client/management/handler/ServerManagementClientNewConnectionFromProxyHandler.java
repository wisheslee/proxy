package com.liji.proxy.client.management.handler;

import com.liji.proxy.client.data.ServerDataClientImpl;
import com.liji.proxy.client.data.handler.ServerDataClientTransferToLocalServerHandler;
import com.liji.proxy.client.local.LocalServerClientImpl;
import com.liji.proxy.client.local.handler.LocalServerTransferToServerDataHandler;
import com.liji.proxy.client.management.ServerManagementClient;
import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author jili
 * @date 2020/1/16
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerManagementClientNewConnectionFromProxyHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private ServerManagementClient serverManagementClient;

    public ServerManagementClientNewConnectionFromProxyHandler(ServerManagementClient serverManagementClient) {
        this.serverManagementClient = serverManagementClient;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        if (msg.getBody().is(MessageProto.NewConnectionFromOuter.class)) {
            LOGGER.info("receive newConnectionFromOuter message");
            MessageProto.NewConnectionFromOuter newConnectionFromOuter = msg.getBody().unpack(MessageProto.NewConnectionFromOuter.class);
            String reqId = msg.getHeader().getReqId();

            //向代理的本地服务发起连接
            Server localServer = new Server(newConnectionFromOuter.getLocalHost(), newConnectionFromOuter.getLocalPort());
            new LocalServerClientImpl(ctx.channel(), localServer, reqId, msg.getHeader());
        }
        ctx.fireChannelRead(msg);
    }
}

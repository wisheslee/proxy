package com.liji.proxy.server.proxy;

import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Server;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.server.common.context.ProxyConnectionImpl;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import io.netty.channel.*;

import java.util.UUID;


/**
 * @author jili
 * @date 2020/1/29
 */
public class ServerProxyImpl implements ServerProxy {

    private Proxy proxy;
    private Channel serverProxyChannel;
    private Channel serverManagementChannel;
    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getInstance();


    public ServerProxyImpl(Proxy proxy, Channel serverProxyChannel, Channel serverManagementChannel) {
        this.proxy = proxy;
        this.serverProxyChannel = serverProxyChannel;
        this.serverManagementChannel = serverManagementChannel;
    }


    @Override
    public void notifyServerManagementNewConnection(Channel proxyConnectionChannel) {
        String reqId = UUID.randomUUID().toString();
        serverApplicationContext.getConnectionContext().newConnection(reqId, new ProxyConnectionImpl(this, proxyConnectionChannel));
        Server localServer = proxy.getLocalServer();
        serverManagementChannel.writeAndFlush(MessageFactory.newMessage(
                MessageProto.NewConnectionFromOuter.newBuilder().setLocalHost(localServer.getHost()).setLocalPort(localServer.getPort()).build(),
                reqId));
    }

    @Override
    public void startRead(Channel proxyConnectionChannel) {
        proxyConnectionChannel.read();
    }

    @Override
    public void transferDataToServerData(Channel proxyToServerDataChannel, Object msg) {
        proxyToServerDataChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    future.channel().read();
                }
            }
        });
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public Channel getServerProxyChannel() {
        return serverProxyChannel;
    }
}

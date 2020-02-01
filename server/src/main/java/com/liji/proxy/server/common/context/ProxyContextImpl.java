package com.liji.proxy.server.common.context;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.handler.GlobalSharableHandlerFactory;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.model.Proxy;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.common.utils.MessageResponseFactory;
import com.liji.proxy.server.proxy.ServerProxy;
import com.liji.proxy.server.proxy.ServerProxyImpl;
import com.liji.proxy.server.proxy.handler.ServerProxyNewConnectionHandler;
import com.liji.proxy.server.proxy.handler.ServerProxyTransferDataToServerDataHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局单例
 *
 * @author jili
 * @date 2020/1/30
 */
public class ProxyContextImpl implements ProxyContext {

    private static ProxyContext proxyContext = new ProxyContextImpl();
    // TODO: jili 2020/1/31 记得关闭
    private EventLoopGroup proxyServerBossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("proxyServerBossGroup"));
    private EventLoopGroup proxyServerWorkerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("proxyServerWorkerGroup"));
    private Map<Integer, ServerProxy> map = new ConcurrentHashMap<>();
    private Map<InetSocketAddress, List<ServerProxy>> clientProxyListMap = new ConcurrentHashMap<>();

    public static ProxyContext newInstance() {
        return proxyContext;
    }

    // TODO: jili 2020/1/30 端口绑定的并发处理
    @Override
    public void newServerProxy(Proxy proxy, Channel serverManagementChannel, MessageProto.Header header) {
        if (map.containsKey(proxy.getProxyPort())) {
            serverManagementChannel.writeAndFlush(MessageResponseFactory.fail(header, "端口已被占用"));
            return;
        }
        //在指定端口进行监听
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(proxyServerBossGroup, proxyServerWorkerGroup);

        ServerProxyNewConnectionHandler serverProxyNewConnectionHandler = new ServerProxyNewConnectionHandler();
        ServerProxyTransferDataToServerDataHandler serverProxyTransferDataToServerDataHandler = new ServerProxyTransferDataToServerDataHandler();

        // TODO: jili 2020/2/2 idle检测
        serverBootstrap
                .channel(NioServerSocketChannel.class)
                //不能AUTO_READ, 需要在收到新请求，在client与serverData连接建立成功后手动读；
                .childOption(ChannelOption.AUTO_READ, false)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();

                        pipeline.addLast("serverProxyLog", GlobalSharableHandlerFactory.getLoggingHandler());

                        pipeline.addLast("serverProxyNewConnection", serverProxyNewConnectionHandler);
                        pipeline.addLast("serverProxyTransferData", serverProxyTransferDataToServerDataHandler);

                        pipeline.addLast("serverProxyException", GlobalSharableHandlerFactory.getExceptionHandler());
                    }
                });
        serverBootstrap.bind(DefaultConstants.LOCAL_HOST, proxy.getProxyPort())
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        MessageProto.Header header = MessageFactory.newHeader();
                        if (future.isSuccess()) {
                            ServerProxyImpl serverProxy = new ServerProxyImpl(proxy, future.channel(), serverManagementChannel);

                            //更新client对应proxyServer列表
                            clientProxyListMap.get(serverManagementChannel.remoteAddress()).add(serverProxy);

                            if (map.putIfAbsent(proxy.getProxyPort(), serverProxy) == null) {
                                serverManagementChannel.writeAndFlush(MessageResponseFactory.success(header));
                            } else {
                                serverManagementChannel.writeAndFlush(MessageResponseFactory.fail(header, "端口已被占用"));
                            }
                        } else {
                            serverManagementChannel.writeAndFlush(MessageResponseFactory.fail(header, future.cause().getMessage()));
                        }
                    }
                });
    }

    @Override
    public ServerProxy getServerProxy(int port) {
        return map.get(port);
    }

    @Override
    public void initClientProxyList(InetSocketAddress address) {
        clientProxyListMap.put(address, new LinkedList<>());
    }

    @Override
    public List<ServerProxy> getClientAllProxyList(InetSocketAddress address) {
        return clientProxyListMap.get(address);
    }


    @Override
    public void removeClient(InetSocketAddress address) {
        clientProxyListMap.remove(address);
    }
}

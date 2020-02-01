package com.liji.proxy.server.management.handler;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.model.MessageProto;
import com.liji.proxy.common.utils.MessageFactory;
import com.liji.proxy.common.utils.MessageResponseFactory;
import com.liji.proxy.server.common.context.ServerApplicationContext;
import com.liji.proxy.server.common.context.ServerApplicationContextImpl;
import com.liji.proxy.server.management.ServerManagement;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author jili
 * @date 2020/1/29
 */
@Slf4j
@ChannelHandler.Sharable
public class ServerManagementAuthHandler extends SimpleChannelInboundHandler<MessageProto.Message> {

    private ServerManagement serverManagement;
    private ServerApplicationContext serverApplicationContext = ServerApplicationContextImpl.getInstance();

    public ServerManagementAuthHandler(ServerManagement serverManagement) {
        this.serverManagement = serverManagement;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProto.Message msg) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        if (msg.getBody().is(MessageProto.Authentication.class)) {
            LOGGER.info("receive auth message");
            MessageProto.Authentication authentication = msg.getBody().unpack(MessageProto.Authentication.class);
            if (serverManagement.authenticate(authentication)) {
                ctx.writeAndFlush(MessageResponseFactory.success(msg.getHeader()));

                //添加proxyContext, 在该client被关闭时，关闭该client开启的所有proxyServer
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                serverApplicationContext.getProxyContext().initClientProxyList(address);

                //只在第一条消息进行鉴权
                ctx.pipeline().remove(this);
            } else {
                LOGGER.info("client发送了错误的密码，client ip = {}, port = {}", socketAddress.getHostName(), socketAddress.getPort());
                ctx.close();
            }
        } else {
            LOGGER.error("client第一条消息不是auth message, client ip = {}, port = {}", socketAddress.getHostName(), socketAddress.getPort());
            ctx.close();
        }
    }
}

package com.liji.proxy.server.data;

import com.liji.proxy.common.constants.DefaultConstants;
import com.liji.proxy.common.handler.ExceptionHandler;
import com.liji.proxy.server.data.handler.DataTransferReqIdHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jili
 * @date 2020/1/27
 */
@Slf4j
public class DataServer implements Runnable {

    private int serverDataPort;

    public DataServer(int serverDataPort) {
        this.serverDataPort = serverDataPort;
    }

    @Override
    public void run() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("dataServerBoss"));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("dataServerWorker"));
        try {
            LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
            ExceptionHandler exceptionHandler = new ExceptionHandler();

            ChannelFuture connectFuture = serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("dataServerLoggingHandler", loggingHandler);
                            ch.pipeline().addLast("dataServerReqIdHandler", new DataTransferReqIdHandler());
                            ch.pipeline().addLast("dataServerExceptionHandler", exceptionHandler);
                        }
                        // TODO: jili 2020/1/29 auto_read 是否必要
                    }).childOption(ChannelOption.AUTO_READ, true)
                    .bind(serverDataPort).sync();
            connectFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

package com.dili.registry.component;

import com.dili.registry.boot.RegistryNettyConfig;
import com.dili.registry.initializer.RegistryNettyTerminalInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * 心跳服务端
 *
 * @author wangmi
 */
@Component
public class RegistryTerminalServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(RegistryTerminalServer.class);

    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();

    @Resource
    private RegistryNettyConfig registryNettyConfig;

    private final RegistryNettyTerminalInitializer registryNettyTerminalInitializer;

    @Autowired
    public RegistryTerminalServer(RegistryNettyTerminalInitializer registryNettyTerminalInitializer) {
        this.registryNettyTerminalInitializer = registryNettyTerminalInitializer;
    }

    /**
     * 启动 Netty
     *
     * @throws InterruptedException
     */
    public void start() throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(registryNettyConfig.getTerminalPort()))
                /**
                 * 对于ChannelOption.SO_BACKLOG的解释：
                 * 服务器端TCP内核维护有两个队列，我们称之为A、B队列。客户端向服务器端connect时，会发送带有SYN标志的包（第一次握手），服务器端
                 * 接收到客户端发送的SYN时，向客户端发送SYN ACK确认（第二次握手），此时TCP内核模块把客户端连接加入到A队列中，然后服务器接收到
                 * 客户端发送的ACK时（第三次握手），TCP内核模块把客户端连接从A队列移动到B队列，连接完成，应用程序的accept会返回。也就是说accept
                 * 从B队列中取出完成了三次握手的连接。
                 * A队列和B队列的长度之和就是backlog。当A、B队列的长度之和大于ChannelOption.SO_BACKLOG时，新的连接将会被TCP内核拒绝。
                 * 所以，如果backlog过小，可能会出现accept速度跟不上，A、B队列满了，导致新的客户端无法连接。要注意的是，backlog对程序支持的
                 * 连接数并无影响，backlog影响的只是还没有被accept取出的连接
                 */
                .option(ChannelOption.SO_BACKLOG, 128) //设置TCP缓冲区, 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                .option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                .option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小
                .childOption(ChannelOption.SO_KEEPALIVE, true)  //保持长连接
                //保持长连接
                .childHandler(registryNettyTerminalInitializer);
        ChannelFuture future = bootstrap.bind().sync();
        LOGGER.info("注册中心心跳服务端开始监听端口：" + registryNettyConfig.getTerminalPort());
        //关闭channel和块，直到它被关闭
        future.channel().closeFuture().sync();
        if (future.isSuccess()) {
            LOGGER.info("启动 注册中心心跳服务 成功");
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        LOGGER.info("关闭 注册中心心跳服务 成功");
    }
}
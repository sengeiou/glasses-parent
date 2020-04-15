package com.dili.glasses.component;

import com.dili.glasses.boot.NettyServerConfig;
import com.dili.glasses.initializer.NettyServerChannelInitializer;
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

import java.net.InetSocketAddress;

/**
 * netty服务启动类
 * @author wangmi
 **/
@Component
public class NettyServer {
    protected static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Autowired
    private NettyServerConfig nettyConfig;

    @Autowired
    private NettyServerChannelInitializer nettyServerChannelInitializer;

    public void start() {
        InetSocketAddress address = new InetSocketAddress(nettyConfig.getPort());
        //配置服务端的NIO线程组
        //用于处理服务器端接收客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //进行网络通信（读写）
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    // 绑定线程池
                    .group(bossGroup, workerGroup)
                    //指定NIO的模式
                    .channel(NioServerSocketChannel.class)
                    .localAddress(address)
                    .childHandler(nettyServerChannelInitializer)//编码解码
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
                    .option(ChannelOption.SO_BACKLOG, 1024) //设置TCP缓冲区, 服务端接受连接的队列长度，如果队列已满，客户端连接将被拒绝
                    .option(ChannelOption.SO_SNDBUF, 32 * 1024) //设置发送数据缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 32 * 1024) //设置接受数据缓冲大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  //保持长连接

            //绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(address).sync();
            log.info("netty服务器开始监听端口：" + address.getPort());
            //关闭channel和块，直到它被关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
package com.dili.client.component;

import com.dili.client.consts.NettyCache;
import com.dili.client.initializer.NettyClientChannelInitializer;
import com.dili.ss.util.DateUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 客户端启动类
 * @author wangmi
 **/
public class NettyClient implements Runnable {
    protected static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    //服务端host
    private String host;
    //服务端口
    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new NettyClientChannelInitializer());
            ChannelFuture future = b.connect(host, port).sync();
            NettyCache.channel = future.channel();
            NettyCache.channel.writeAndFlush("client msg:" + DateUtils.format(new Date()));
//            while (num < COUNT_PER_THREAD) {
//                num++;
//                future.channel().writeAndFlush("client msg:" + DateUtils.format(new Date()));
//                try {
//                    //休眠一段时间
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                }
//            }
//            LOGGER.info("发送"+num+"次消息");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

}
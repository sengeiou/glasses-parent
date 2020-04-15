package com.dili.glasses.component;

import com.dili.glasses.boot.NettyRegistryConfig;
import com.dili.glasses.boot.NettyServerConfig;
import com.dili.glasses.consts.NettyCMD;
import com.dili.glasses.initializer.HeartbeatChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册和心跳组件
 * Spring启动运行
 * @author wangmi
 */
@Component
public class HeartbeatClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartbeatClient.class);

    @Autowired
    NettyRegistryConfig nettyConfig;

    @Autowired
    NettyServerConfig nettyServerConfig;

    public void start() {
        try{
            connect(nettyConfig.getHost(), nettyConfig.getPort());
        } catch (Exception e){
            LOGGER.error("连接注册中心失败:"+e.getMessage());
        }
    }

    private void connect(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture future = null;
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HeartbeatChannelInitializer(nettyConfig));
            future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                LOGGER.info("连接注册中心成功");
            }
            SocketChannel channel = (SocketChannel) future.channel();
            //REGISTER PORT
            channel.writeAndFlush(new StringBuilder().append(NettyCMD.REGISTER).append(" ").append(nettyServerConfig.getPort()).toString());
            channel.closeFuture().sync();
        }finally {
//            group.shutdownGracefully();
            if (null != future) {
                if (future.channel() != null && future.channel().isOpen()) {
                    future.channel().close();
                }
            }
            System.out.println("准备重连：" + nettyConfig.getHost() + ":" + nettyConfig.getPort());
            connect(nettyConfig.getHost(), nettyConfig.getPort());
            System.out.println("重连成功");
        }
    }

}

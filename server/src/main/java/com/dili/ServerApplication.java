package com.dili;

import com.dili.glasses.component.HeartbeatClient;
import com.dili.glasses.component.NettyServer;
import com.dili.ss.dto.DTOScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 由MyBatis Generator工具自动生成
 * @author wangmi
 */
@SpringBootApplication
@ComponentScan(basePackages={"com.dili.ss.util","com.dili.ss.redis","com.dili.glasses"})
@MapperScan(basePackages = {"com.dili.glasses.dao", "com.dili.ss.dao"})
@DTOScan({"com.dili.ss.dto", "com.dili.glasses"})
@EnableScheduling
public class ServerApplication implements CommandLineRunner {

    @Autowired
    private NettyServer nettyServer;

    @Autowired
    private HeartbeatClient heartbeatClient;

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Override
    public void run(String... strings) {
        new Thread(() -> {
            heartbeatClient.start();
        }).start();
        nettyServer.start();
//        connectToRegistry(DateUtils.format(new Date()));
    }

    /**
     * 连接注册中心
     * @param content
     * @throws InterruptedException
     */
//    private void connectToRegistry(String content) throws InterruptedException {
//        // Configure the client.
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new NettyServerRegistryChannelInitializer());
//            ChannelFuture future = b.connect(NettyConstant.SOCKET_IP, NettyConstant.SERVER_PORT).sync();
//            future.channel().writeAndFlush(content);
//            future.channel().closeFuture().sync();
//        } finally {
//            group.shutdownGracefully();
//        }
//    }
}

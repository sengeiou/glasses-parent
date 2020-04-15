package com.dili.client.component;

import com.dili.client.boot.NettyRegistryConfig;
import com.dili.client.consts.NettyCMD;
import com.dili.client.consts.NettyConstant;
import com.dili.client.domain.ApplyAddressProtocol;
import com.dili.client.domain.GlassesProtocol;
import com.dili.client.domain.Protocol;
import com.dili.client.initializer.FindServerChannelInitializer;
import com.dili.ss.util.DateUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 获取服务端地址组件
 * Spring启动运行
 *
 * @author wangmi
 */
@Component
public class FindServerComponent {

    private final static Logger LOGGER = LoggerFactory.getLogger(FindServerComponent.class);

    private EventLoopGroup group = new NioEventLoopGroup();

    @Resource
    NettyRegistryConfig nettyConfig;

    public void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new FindServerChannelInitializer());

            ChannelFuture future = bootstrap.connect(nettyConfig.getHost(), nettyConfig.getPort()).sync();
            if (future.isSuccess()) {
                LOGGER.info("启动 Netty 成功");
            }
            SocketChannel channel = (SocketChannel) future.channel();
            ApplyAddressProtocol protocol = new ApplyAddressProtocol();
//            protocol.setStx(NettyConstant.HEAD_DATA);
//            protocol.setLength((short) 128);
            protocol.setTerminalId(1);
            protocol.setCmd((byte) 2);
            protocol.setSource((byte) 2);
            protocol.setTransferType((byte) 8);
            long startTime = DateUtils.formatDateStr2Date("2018-01-01 00:00:00").getTime();
            protocol.setTerminalTime(new Long(System.currentTimeMillis() - startTime).intValue());
            protocol.setEndMark((byte) 0x68);
            protocol.setChecksum(16);

            GlassesProtocol glassesProtocol = new GlassesProtocol((short) 128, protocol.toByteArray());
            glassesProtocol.setStx(NettyConstant.HEAD_DATA);
            channel.writeAndFlush(glassesProtocol);
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}

package com.dili.glasses.initializer;

import com.dili.glasses.boot.NettyRegistryConfig;
import com.dili.glasses.decoder.HeartbeatDecoder;
import com.dili.glasses.encoder.HeartbeatEncoder;
import com.dili.glasses.handler.RegistryHeartbeatHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 客户端心跳初始化器
 * @author wangmi
 */
public class HeartbeatChannelInitializer extends ChannelInitializer<Channel> {

    private NettyRegistryConfig nettyRegistryConfig;

    public HeartbeatChannelInitializer(NettyRegistryConfig nettyRegistryConfig){
        this.nettyRegistryConfig = nettyRegistryConfig;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                //nettyRegistryConfig.getWriterIdleTimeSeconds()秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new IdleStateHandler(nettyRegistryConfig.getReaderIdleTimeSeconds(), nettyRegistryConfig.getWriterIdleTimeSeconds(), nettyRegistryConfig.getAllIdleTimeSeconds()))
                .addLast("encoder", new HeartbeatEncoder())
                .addLast("decoder", new HeartbeatDecoder())
                .addLast(new RegistryHeartbeatHandler());
    }

}
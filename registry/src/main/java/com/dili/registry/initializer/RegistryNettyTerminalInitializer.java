package com.dili.registry.initializer;

import com.dili.registry.boot.RegistryNettyConfig;
import com.dili.registry.decoder.ProtocolDecoder;
import com.dili.registry.encoder.ProtocolEncoder;
import com.dili.registry.handler.RegistryTerminalHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 终端初始化器
 *
 * @author wangmi
 */
@Component
public class RegistryNettyTerminalInitializer extends ChannelInitializer<Channel> {

    private final RegistryNettyConfig registryNettyConfig;

    private final RegistryTerminalHandler registryTerminalHandler;

    @Autowired
    public RegistryNettyTerminalInitializer(RegistryNettyConfig registryNettyConfig, RegistryTerminalHandler registryTerminalHandler) {
        this.registryNettyConfig = registryNettyConfig;
        this.registryTerminalHandler = registryTerminalHandler;
    }

    @Override
    protected void initChannel(Channel ch) {
        ByteBuf delimiter = Unpooled.copiedBuffer("]".getBytes());
        ch.pipeline()
                //五秒没有收到消息 将IdleStateHandler 添加到 ChannelPipeline 中
//                .addLast(new IdleStateHandler(registryNettyConfig.getReaderIdleTimeSeconds(), registryNettyConfig.getWriterIdleTimeSeconds(), registryNettyConfig.getAllIdleTimeSeconds()))
//                .addLast("encoder", new HeartbeatEncoder())
//                .addLast("decoder", new HeartbeatDecoder())
//                .addLast("decoder",new ByteArrayDecoder())
//                .addLast("encoder",new ByteArrayEncoder())

//                .addLast("decoder", new ProtocolDecoder())
//                .addLast("encoder", new ByteArrayEncoder())

                .addLast("framer", new DelimiterBasedFrameDecoder(4096, delimiter))
                .addLast("decoder", new ProtocolDecoder())
                .addLast("encoder", new ProtocolEncoder())
                .addLast(registryTerminalHandler);
    }
}
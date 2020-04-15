package com.dili.registry.initializer;

import com.dili.registry.handler.RegistryServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调，设置出站编码器和入站解码器
 *
 * @author wangmi
 **/
@Component
public class RegistryNettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final RegistryServerHandler registryNettyServerHandler;

    @Autowired
    public RegistryNettyServerInitializer(RegistryServerHandler registryNettyServerHandler) {
        this.registryNettyServerHandler = registryNettyServerHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast("decoder", new StringDecoder(CharsetUtil.ISO_8859_1));
        channel.pipeline().addLast("encoder", new StringEncoder(CharsetUtil.ISO_8859_1));
        channel.pipeline().addLast(registryNettyServerHandler);
    }
}
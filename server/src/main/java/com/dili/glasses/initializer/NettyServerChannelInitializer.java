package com.dili.glasses.initializer;

import com.dili.glasses.boot.NettyServerConfig;
import com.dili.glasses.decoder.ProtocolDecoder;
import com.dili.glasses.handler.NettyServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调，设置出站编码器和入站解码器
 *
 * @author wangmi
 **/
@Component
public class NettyServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyServerConfig nettyServerConfig;

    private final NettyServerHandler nettyServerHandler;

    @Autowired
    public NettyServerChannelInitializer(NettyServerHandler nettyServerHandler, NettyServerConfig nettyServerConfig) {
        this.nettyServerHandler = nettyServerHandler;
        this.nettyServerConfig = nettyServerConfig;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new IdleStateHandler(nettyServerConfig.getReaderIdleTimeSeconds(),
                nettyServerConfig.getWriterIdleTimeSeconds(), nettyServerConfig.getAllIdleTimeSeconds()));
        channel.pipeline().addLast("decoder", new ProtocolDecoder());
        channel.pipeline().addLast("encoder", new ByteArrayEncoder());
        channel.pipeline().addLast(nettyServerHandler);
    }
}
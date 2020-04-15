package com.dili.client.initializer;

import com.dili.client.decoder.ProtocolDecoder;
import com.dili.client.encoder.ProtocolEncoder;
import com.dili.client.handler.FindServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * 客户端初始化，客户端与服务器端连接一旦创建，这个类中方法就会被回调，设置出站编码器和入站解码器，客户端服务端编解码要一致
 * @author wangmi
 **/
public class FindServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
//        channel.pipeline().addLast("decoder",new StringDecoder(Charset.forName(NettyConstant.CHARSET)));
//        channel.pipeline().addLast("encoder",new StringEncoder(Charset.forName(NettyConstant.CHARSET)));
//        channel.pipeline().addLast("decoder",new ByteArrayDecoder());
//        channel.pipeline().addLast("encoder",new ByteArrayEncoder());
        channel.pipeline().addLast("decoder",new ProtocolDecoder())
            .addLast("encoder",new ProtocolEncoder())
            .addLast(new FindServerHandler());
    }
}
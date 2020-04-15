package com.dili.glasses.encoder;

import com.dili.glasses.consts.NettyConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 客户端中心编码器
 * @author wangmi
 */
public class HeartbeatEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.getBytes(NettyConstant.CHARSET)) ;
    }
}

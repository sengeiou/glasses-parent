package com.dili.client.encoder;

import com.dili.client.domain.GlassesProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtocolEncoder extends MessageToByteEncoder<GlassesProtocol> {

    @Override
    protected void encode(ChannelHandlerContext tcx, GlassesProtocol msg,
                          ByteBuf out) {
        // 写入消息SmartCar的具体内容
        // 1.写入消息的开头的信息标志(int类型)
        out.writeShort(msg.getStx());
        // 2.写入消息的长度(int 类型)
        out.writeShort(msg.getLength());
        // 3.写入消息的内容(byte[]类型)
        out.writeBytes(msg.getContent());
    }
}

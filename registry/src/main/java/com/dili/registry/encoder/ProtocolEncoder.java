package com.dili.registry.encoder;

import com.dili.registry.consts.NettyConstant;
import com.dili.registry.domain.GlassesProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;

public class ProtocolEncoder extends MessageToByteEncoder<GlassesProtocol> {

    @Override
    protected void encode(ChannelHandlerContext tcx, GlassesProtocol msg,
                          ByteBuf out) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(msg.getType()).append(",")
                .append(msg.getImei()).append(",")
                .append(msg.getLength()).append(",")
                .append(msg.getData()).append(",")
                .append(msg.getCrc()).append("]");
        out.writeBytes(sb.toString().getBytes(NettyConstant.CHARSET));
    }
}

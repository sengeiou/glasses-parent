package com.dili.registry.encoder;

import com.dili.registry.consts.NettyConstant;
import com.dili.registry.domain.response.BaseResponseProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.UnsupportedEncodingException;

public class ProtocolEncoder extends MessageToByteEncoder<BaseResponseProtocol> {

    @Override
    protected void encode(ChannelHandlerContext tcx, BaseResponseProtocol msg,
                          ByteBuf out) throws UnsupportedEncodingException {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[").append(msg.getType()).append(",")
//                .append(msg.getImei()).append(",")
//                .append(msg.getLength()).append(",")
//                .append(msg.encodeDatas()).append(",")
//                .append(msg.getCrc()).append("]");
        out.writeBytes(msg.encodeDatas().getBytes(NettyConstant.CHARSET));
    }
}

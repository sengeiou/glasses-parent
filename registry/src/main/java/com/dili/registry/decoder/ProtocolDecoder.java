package com.dili.registry.decoder;

import com.dili.registry.consts.NettyConstant;
import com.dili.registry.domain.AbstractProtocol;
import com.dili.registry.domain.GlassesProtocol;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * 自己定义的协议
 *  数据包格式
 * +——----——+——-----——+——----——+
 * |协议开始标志|  长度             |   数据       |
 * +——----——+——-----——+——----——+
 * 1.协议开始标志head_data，为short类型的数据，16进制表示为0X76
 * 2.传输数据的长度contentLength，short类型
 * 3.要传输的数据,长度不应该超过2048，防止socket流的攻击
 * </pre>
 */
@Slf4j
public class ProtocolDecoder extends ByteToMessageDecoder {
    /**
     * <pre>
     * 协议开始的标准head_data，short类型，占据2个字节.
     * 表示数据的长度contentLength，short类型，占据2个字节.
     * </pre>
     */
    public final int BASE_LENGTH = 2 + 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer,
                          List<Object> out) throws Exception {
        // 消息的长度
        int length = buffer.readableBytes();
        if(length <2){
            return;
        }
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        String dataStr = new String(bytes).trim();
        dataStr = dataStr.substring(1, dataStr.length());
        String[] dataArray = dataStr.split(",");
        if(dataArray.length != 5){
            return;
        }
        GlassesProtocol protocol = new GlassesProtocol(dataArray[0], dataArray[1], dataArray[2], dataArray[3],dataArray[4]);
//        if (!checkSum(protocol)) {
//            log.error("校验和不通过");
//            return;
//        }
        out.add(protocol);
    }

    /**
     * 计算校验和
     *
     * @param protocol 协议
     * @return 是否通过
     */
    protected boolean checkSum(GlassesProtocol protocol) {

        return true;
    }
}

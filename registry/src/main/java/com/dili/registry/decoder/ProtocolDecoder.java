package com.dili.registry.decoder;

import com.dili.registry.domain.BaseProtocol;
import com.dili.registry.utils.CRC16Util;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * 协议解码器
 * 用于在获取到消息后处理数据
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
        if(length < BASE_LENGTH){
            return;
        }
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.readBytes(bytes);
        //原始数据字符串
        String dataStr = new String(bytes).trim();
        //去掉方括号
        dataStr = dataStr.substring(1, dataStr.length()-1);
        String[] dataArray = dataStr.split(",");
        //type, imei, data, crc至少四位
        if(dataArray.length <= 4){
            return;
        }
        BaseProtocol protocol = new BaseProtocol();
        protocol.setType(dataArray[0]);
        protocol.setImei(dataArray[1]);
        protocol.setCrc(dataArray[dataArray.length - 1]);
        //只取除type, imei和crc外的数据区
        protocol.setDatas(Arrays.copyOfRange(dataArray, 2, dataArray.length-1));
        if (!checkSum(dataStr.substring(1, dataStr.lastIndexOf(",")), protocol.getCrc())) {
            log.error("校验和不通过");
            return;
        }
        out.add(protocol);
    }

    /**
     * CRC16校验
     * @param checkStr 检查字符串
     * @param crc
     * @return 是否通过
     */
    protected boolean checkSum(String checkStr, String crc) {
        return CRC16Util.getCRC16Format(checkStr).equals(crc);
    }
}

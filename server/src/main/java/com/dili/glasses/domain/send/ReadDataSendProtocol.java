package com.dili.glasses.domain.send;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-10 17:11
 * @description 读取终端数据发送协议
 **/
@Getter
@Setter
public class ReadDataSendProtocol extends AbstractSendProtocol {
    /**
     * 记录序号，随机数，设备回复的时候也会带上改随机数
     */
    private Integer recordNo;

    /**
     * 数据长度
     */
    private byte dataLength;

    /**
     * 标识数组
     */
    private Byte[] markArr;

    @Override
    public byte[] contentToInformation() {
        ByteBuf buf = Unpooled.buffer(5 + markArr.length);
        buf.writeByte(dataLength);
        buf.writeBytes(ByteArrayUtils.toPrimitives(markArr));
        buf.writeInt(recordNo);
        return buf.array();
    }

    public ReadDataSendProtocol(Integer terminalId, Byte cmd, Integer recordNo, Byte[] markArr) {
        super(terminalId, cmd);
        this.recordNo = recordNo;
        this.dataLength = (byte) markArr.length;
        this.markArr = markArr;
    }
}

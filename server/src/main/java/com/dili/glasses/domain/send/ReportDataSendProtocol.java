package com.dili.glasses.domain.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-03 11:34
 * @description
 **/
@Getter
@Setter
public class ReportDataSendProtocol extends AbstractSendProtocol {

    /**
     * 数据的数量
     */
    private byte dataLength;

    @Override
    public byte[] contentToInformation() {
        ByteBuf buf = Unpooled.buffer(1);
        buf.writeByte(dataLength);
        return buf.array();
    }

    public ReportDataSendProtocol(Integer terminalId, Byte cmd, byte length) {
        super(terminalId, cmd);
        this.dataLength = length;
    }
}

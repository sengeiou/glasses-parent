package com.dili.client.domain;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 交互类协议，有记录号
 */
public class InteractiveProtocol extends Protocol {
    //记录号4字节 高1字节是随机码无意义(保留加密用),低3字节是记录序号
    private Integer recordNumber;

    public Integer getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(Integer recordNumber) {
        this.recordNumber = recordNumber;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer(64);
        if(getStx() != null) {
            buf.writeBytes(ByteArrayUtils.short2bytes2(getStx()));
        }
        if(getLength() != null) {
            buf.writeShort(getLength());
        }
        buf.writeInt(getTerminalId());
        buf.writeByte(getCmd());
        buf.writeByte(getSource());
        buf.writeByte(getTransferType());
        buf.writeBytes(ByteArrayUtils.toPrimitives(getInformation()));
        buf.writeInt(getRecordNumber());
        buf.writeByte(getEndMark());
        buf.writeInt(buf.array().length);
        return buf.array();
    }
}

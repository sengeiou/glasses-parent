package com.dili.client.domain;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 登录类传输协议
 * @author wangmi
 */
public class LoginTypeProtocol extends Protocol {

    //终端类型:默认是0，主要是备用，如果有多种类型时用这个字节区分
    private Byte terminalType;

    public Byte getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(Byte terminalType) {
        this.terminalType = terminalType;
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
        buf.writeByte(getTerminalType());
        buf.writeByte(getEndMark());
        buf.writeInt(buf.array().length);
        return buf.array();
    }
}

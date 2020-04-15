package com.dili.client.domain;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 传输协议
 * @author wangmi
 */
public class ApplyAddressProtocol extends Protocol {

    private Integer terminalTime;

    private Byte[] hostPort;

    public Byte[] getHostPort() {
        return hostPort;
    }

    public void setHostPort(Byte[] hostPort) {
        this.hostPort = hostPort;
    }

    public Integer getTerminalTime() {
        return terminalTime;
    }

    public void setTerminalTime(Integer terminalTime) {
        this.terminalTime = terminalTime;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuf buf = Unpooled.buffer(128);
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
        buf.writeInt(getTerminalTime());
        buf.writeByte(getEndMark());
        buf.writeInt(buf.array().length);
        return buf.array();
    }
}

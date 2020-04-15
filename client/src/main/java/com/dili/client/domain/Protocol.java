package com.dili.client.domain;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 传输协议
 * @author wangmi
 */
public class Protocol {
    //起始帧2字节: 0x4E(78"N") 0x57(87"W")
    private Short stx;
    //帧长度2字节
    private Short length;
    //终端号4字节: 最高8位眼镜管理备用号，低24位是终端号(最高一字节是保留默认00，低三字节是唯一ID号)
    private Integer terminalId;
    //命令字: 0x01:心跳 0x02:登录 0x03:退出
    //0x04服务器读数据 0x05 服务器写数据 0x06终端主动上传违规数据 0x08 读取服务器时间以及下次登录的IP地址
    private Byte cmd;
    //帧来源: 0: 主站 1:APP 2:眼镜终端
    private Byte source;
    //传输类型:0:请求帧 1:应答帧 2:主动上报
    private Byte transferType;
    //信息域 N字节
    private Byte[] information;
    //结束标识 0x68
    private Byte endMark;
    //校验和4字节
    private Integer checksum;

    public Short getStx() {
        return stx;
    }

    public void setStx(Short stx) {
        this.stx = stx;
    }

    public Short getLength() {
        return length;
    }

    public void setLength(Short length) {
        this.length = length;
    }

    public Integer getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(Integer terminalId) {
        this.terminalId = terminalId;
    }

    public Byte getCmd() {
        return cmd;
    }

    public void setCmd(Byte cmd) {
        this.cmd = cmd;
    }

    public Byte getSource() {
        return source;
    }

    public void setSource(Byte source) {
        this.source = source;
    }

    public Byte getTransferType() {
        return transferType;
    }

    public void setTransferType(Byte transferType) {
        this.transferType = transferType;
    }

    public Byte[] getInformation() {
        return information;
    }

    public void setInformation(Byte[] information) {
        this.information = information;
    }

    public Byte getEndMark() {
        return endMark;
    }

    public void setEndMark(Byte endMark) {
        this.endMark = endMark;
    }

    public Integer getChecksum() {
        return checksum;
    }

    public void setChecksum(Integer checksum) {
        this.checksum = checksum;
    }

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
        buf.writeByte(getEndMark());
        buf.writeInt(buf.array().length);
        return buf.array();
    }
}

package com.dili.client.domain;

import com.dili.client.consts.NettyConstant;

import java.util.Arrays;

/**
 * 眼镜传输协议
 *
 * @author wangmi
 */
public class GlassesProtocol {
    //起始帧2字节: 0x4E(78"N") 0x57(87"W")
    private short stx;
    //帧长度2字节
    private short length;

    private byte[] content;

    /**
     * 用于初始化，GlassesProtocol
     *
     * @param length  协议里面，消息数据的长度
     * @param content 协议里面，消息的数据
     */
    public GlassesProtocol(short length, byte[] content) {
        this.length = length;
        this.content = content;
    }

    public short getStx() {
        return stx;
    }

    public void setStx(short stx) {
        this.stx = stx;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SmartCarProtocol [stx=" + stx + ", length="
                + length + ", content=" + Arrays.toString(content) + "]";
    }
}

package com.dili.glasses.domain;

import com.dili.glasses.consts.NettyConstant;
import lombok.Data;

import java.util.Arrays;

/**
 * 眼镜传输协议
 *
 * @author wangmi
 */
@Data
public class GlassesProtocol {
    /**
     * 起始帧2字节: 0x4E(78"N") 0x57(87"W")
     */
    private short stx;

    /**
     * 帧长度2字节
     */
    private short length;

    /**
     * 帧内容
     */
    private byte[] content;

    /**
     * 用于初始化，GlassesProtocol
     *
     * @param length  协议里面，消息数据的长度
     * @param content 协议里面，消息的数据
     */
    public GlassesProtocol(short length, byte[] content) {
        stx = NettyConstant.HEAD_DATA;
        this.length = length;
        this.content = content;
    }


    @Override
    public String toString() {
        return "SmartCarProtocol [stx=" + stx + ", length="
                + length + ", content=" + Arrays.toString(content) + "]";
    }
}

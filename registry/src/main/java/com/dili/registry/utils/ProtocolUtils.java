package com.dili.registry.utils;

import com.dili.registry.domain.BaseProtocol;

/**
 * 协议处理工具
 * 主要处理长度和CRC16内容
 */
public class ProtocolUtils {
    /**
     * 获取协议长度
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends BaseProtocol> void fillLengthAndCrc16(T t){
        t.setLength("");
        t.setCrc("");
    }

}

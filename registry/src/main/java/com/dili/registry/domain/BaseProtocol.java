package com.dili.registry.domain;

import lombok.Data;

/**
 * 传输协议
 *
 * @author wangmi
 */
@Data
public class BaseProtocol {
    /**
     * 命令类型
     */
    private String type;

    /**
     * 设备IMEI号15位
     */
    private String imei;

    /**
     * 长度,这帧有多少字节包括帧头帧尾(0000-9999)	4个字节
     */
    private String length;

    /**
     * 校验,只校验类型,IMEI号,长度,数据区, [类型,IMEI号,长度,数据区,CRC16校验]
     */
    private String crc;

    /**
     * 数据域
     */
    private String[] datas;

}

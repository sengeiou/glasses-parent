package com.dili.glasses.domain;

import com.dili.glasses.consts.NettyConstant;
import lombok.Data;

/**
 * 传输协议
 *
 * @author wangmi
 */
@Data
public abstract class AbstractProtocol {
    /**
     * 起始帧2字节: 0x4E(78"N") 0x57(87"W")
     */
    protected static final Short STX = NettyConstant.HEAD_DATA;

    /**
     * 结束标识 0x68
     */
    protected static final Byte END_MARK = 0x68;

    /**
     * 终端号4字节: 最高8位眼镜管理备用号，低24位是终端号(最高一字节是保留默认00，低三字节是唯一ID号)
     */
    private Integer terminalId;

    /**
     * 命令字: 0x01:心跳 0x02:登录 0x03:退出
     * 0x04服务器读数据 0x05 服务器写数据 0x06终端主动上传违规数据 0x08 读取服务器时间以及下次登录的IP地址
     */
    private Byte cmd;

    /**
     * 帧来源: 0: 主站 1:APP 2:眼镜终端
     */
    private Byte source;

    /**
     * 传输类型:0:请求帧 1:应答帧 2:主动上报
     */
    private Byte transferType;

    /**
     * 信息域 N字节
     */
    private Byte[] information;
}
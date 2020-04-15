package com.dili.glasses.consts;

/**
 * @author Ren HongWei
 * @date 2019-09-11 10:41
 * @description 业务服务器消息类型
 **/
public interface BusinessMessageTypeConstants {

    /**
     * 从终端读取数据
     */
    byte READ_FROM_TERMINAL = 0x04;

    /**
     * 数据写入终端
     */
    byte WRITE_TO_TERMINAL = 0x05;

    /**
     * 更新配置
     */
    byte UPDATE_CONFIG = 0x0f;
}

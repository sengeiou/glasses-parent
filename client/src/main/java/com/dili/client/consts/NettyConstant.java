package com.dili.client.consts;

import org.springframework.stereotype.Component;

/**
 * Netty服务器常量
 * @author wangmi
 */
@Component
public class NettyConstant {

    /**
     * 统一字符集
     */
    public static final String CHARSET = "ISO-8859-1";

    /**
     * 开始帧
     */
    public static final Short HEAD_DATA = 20055;

    /**
     * 最大线程量
     */
    public static final int MAX_THREADS = 1024;
    /**
     * 数据包最大长度
     */
    public static final int MAX_FRAME_LENGTH = 65535;

    /**
     * 连接服务端的地址
     */
    public static String serverHost = null;
    /**
     * 连接服务端端口
     */
    public static int serverPort = 0;
}

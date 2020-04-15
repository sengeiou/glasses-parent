package com.dili.glasses.consts;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty服务器常量
 *
 * @author wangmi
 */
@Component
public class NettyConstant {
    /**
     * 开始帧
     */
    public static final Short HEAD_DATA = 20055;

    /**
     * 保存ChannelId与连接上下文，在终端连接时赋值, 终端心跳超时或下线时清空
     * 用于接收MQ消息向指定终端下发命令
     */
    public static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();
    /**
     * 保存终端号和ChannelId的关系，设备上线时赋值
     * 用于Server向终端直接下发命令(一般是心跳命令和直接回复命令)
     */
    public static final ConcurrentHashMap<Integer, ChannelId> TERMINAL_CHANNEL_ID_MAP = new ConcurrentHashMap<>();
    /**
     * 保存ChannelId和终端号的关系，设备上线时赋值
     * 在设备第一次连接时更新，用于终端断开时，更新redis
     */
    public static final ConcurrentHashMap<ChannelId, Integer> CHANNEL_ID_TERMINAL_MAP = new ConcurrentHashMap<>();
    /**
     * 统一字符集
     */
    public static final String CHARSET = "ISO-8859-1";
    /**
     * 最大线程量
     */
    public static final int MAX_THREADS = 1024;
    /**
     * 数据包最大长度
     */
    public static final int MAX_FRAME_LENGTH = 65535;

    /**
     * redis中设备终端号前缀
     */
    public static final String TERMINAL_KEY = "glasses:terminal:";

    /**
     * redis中不久前发送过消息的终端编号
     */
    public static final String DELAY_TERMINAL_KEY = "glasses:delay:terminal:";

    /**
     * 系统配置key
     */
    public static final String SYSTEM_CONFIG_KEY = "glasses:systemConfig";

    /**
     * socket服务器读取设备信息的记录号
     */
    public static final Integer SOCKET_READ_DATA_RECORD_NO = -1;
}

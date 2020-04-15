package com.dili.registry.consts;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Netty服务器常量
 * @author wangmi
 */
@Component
public class NettyConstant {
    /**
     * 开始帧
     */
    public static final Short HEAD_DATA = 20055;
    /**
     * 统一字符集
     */
    public static final String CHARSET = "ISO-8859-1";
    /**
     * key为Server端的host:port，value是连接数
     */
    public static final Map<String, AtomicInteger> SERVER_CACHE = new ConcurrentHashMap<>();

    /**
     * 保存终端端的ChannelId和地址
     */
    public static final ConcurrentHashMap<ChannelId, String> TERMINAL_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 保存Server端的ChannelId和地址
     */
    public static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> SERVER_CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 最大线程量
     */
    public static final int MAX_THREADS = 1024;
    /**
     * 数据包最大长度
     */
    public static final int MAX_FRAME_LENGTH = 65535;


}

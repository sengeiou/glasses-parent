package com.dili.registry.domain;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 保存客户端与 Channel 之间的关系
 * @author wangmi
 */
public class NettySocketHolder {
    private static final Map<Integer, NioSocketChannel> MAP = new ConcurrentHashMap<>(16);

    public static void put(Integer id, NioSocketChannel socketChannel) {
        MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(Integer id) {
        return MAP.get(id);
    }

    public static Map<Integer, NioSocketChannel> getMAP() {
        return MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> MAP.remove(entry.getKey()));
    }
}

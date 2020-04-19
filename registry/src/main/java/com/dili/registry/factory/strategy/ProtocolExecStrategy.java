package com.dili.registry.factory.strategy;

import com.dili.registry.domain.BaseProtocol;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Ren HongWei
 * @date 2019-09-02 19:25
 * @description 协议解析, 执行 策略
 **/
public interface ProtocolExecStrategy<T extends BaseProtocol> {

    /**
     * 协议解析执行
     *
     * @param context  信道上下文
     * @param protocol 基础协议
     */
    void exec(ChannelHandlerContext context, T protocol);

    /**
     * 获取协议的类型
     *
     * @return 协议类型
     */
    String getType();
}

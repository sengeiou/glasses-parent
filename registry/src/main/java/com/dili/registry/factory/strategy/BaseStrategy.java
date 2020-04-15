package com.dili.registry.factory.strategy;

import com.dili.registry.domain.request.AbstractRequestProtocol;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ren HongWei
 * @date 2019-09-03 09:41
 * @description
 **/
@Slf4j
public abstract class BaseStrategy<T extends AbstractRequestProtocol> implements ProtocolExecStrategy<T> {


    /**
     * 执行方法
     *
     * @param context  信道上下文
     * @param protocol 基础协议
     */
    @Override
    public abstract void exec(ChannelHandlerContext context, T protocol);

    /**
     * 获取请求协议类型
     *
     * @return 类型
     */
    @Override
    public abstract Class<T> getRequestProtocolClass();
}

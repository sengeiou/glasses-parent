package com.dili.glasses.factory.strategy.protocol;

import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.receive.AbstractReceiveProtocol;
import com.dili.glasses.utils.MessageDelaySender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ren HongWei
 * @date 2019-09-03 09:41
 * @description
 **/
@Slf4j
public abstract class BaseStrategy<T extends AbstractReceiveProtocol> implements ProtocolExecStrategy<T> {

    private MessageDelaySender messageDelaySender;

    public BaseStrategy(MessageDelaySender messageDelaySender) {
        this.messageDelaySender = messageDelaySender;
    }

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

    /**
     * 服务端给客户端发送消息
     *
     * @param msg       需要发送的消息内容
     * @param channelId 连接通道唯一id
     */
    void channelWrite(ChannelId channelId, byte[] msg) {
        Integer terminalId = NettyConstant.CHANNEL_ID_TERMINAL_MAP.get(channelId);
        if (terminalId == null) {
            log.info("通道【" + channelId + "】对应的设备id不存在");
            return;
        }
        messageDelaySender.sendMsg(terminalId, msg);
    }
}

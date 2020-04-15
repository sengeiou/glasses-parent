package com.dili.glasses.factory.strategy.protocol;

import com.dili.glasses.domain.receive.HeartBeatReceiveProtocol;
import com.dili.glasses.domain.send.SimpleSendProtocol;
import com.dili.glasses.utils.MessageDelaySender;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ren HongWei
 * @date 2019-09-03 10:21
 * @description 心跳处理策略, 这里只做简单的心跳响应，设备端连接超时的处理在事件监听中处理
 **/
@Slf4j
@Component
public class HeartBeatStrategy extends BaseStrategy<HeartBeatReceiveProtocol> {

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public HeartBeatStrategy(MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, HeartBeatReceiveProtocol protocol) {
        log.info("收到心跳包, channelId:{}, 设备编号:{}", context.channel().id(), protocol.getTerminalId());
        response(context, protocol);
    }

    /**
     * 响应客户端
     *
     * @param context         信道上下文
     * @param requestProtocol 请求内容
     */
    private void response(ChannelHandlerContext context, HeartBeatReceiveProtocol requestProtocol) {
        SimpleSendProtocol heartBeatResponse = new SimpleSendProtocol(requestProtocol.getTerminalId(), requestProtocol.getCmd(), true);
        channelWrite(context.channel().id(), heartBeatResponse.toByteArray());
    }

    @Override
    public Class<HeartBeatReceiveProtocol> getRequestProtocolClass() {
        return HeartBeatReceiveProtocol.class;
    }
}

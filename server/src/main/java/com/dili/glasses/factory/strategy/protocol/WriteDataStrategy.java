package com.dili.glasses.factory.strategy.protocol;

import com.alibaba.fastjson.JSON;
import com.dili.glasses.boot.RabbitConfiguration;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.dto.SocketWriteResponseDataDto;
import com.dili.glasses.domain.receive.WriteDataReceiveProtocol;
import com.dili.glasses.utils.MessageDelaySender;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ren HongWei
 * @date 2019-09-11 11:43
 * @description 写数据策略
 **/
@Component
@Slf4j
public class WriteDataStrategy extends BaseStrategy<WriteDataReceiveProtocol> {

    private final RabbitTemplate rabbitTemplate;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public WriteDataStrategy(RabbitTemplate rabbitTemplate, MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.rabbitTemplate = rabbitTemplate;
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, WriteDataReceiveProtocol protocol) {
        // 接收到数据写入到设备，通知业务服务器
        // 不为服务器记录号
        if (protocol.getRecordNo() != NettyConstant.SOCKET_READ_DATA_RECORD_NO) {
            SocketWriteResponseDataDto responseDataDto = new SocketWriteResponseDataDto();
            responseDataDto.setRecordNo(protocol.getRecordNo());
            responseDataDto.setSuccess(protocol.isSuccess());
            responseDataDto.setTerminalId(protocol.getTerminalId());
            rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE, RabbitConfiguration.REPORT_QUEUE, JSON.toJSONString(responseDataDto));
        }
    }

    @Override
    public Class<WriteDataReceiveProtocol> getRequestProtocolClass() {
        return WriteDataReceiveProtocol.class;
    }
}

package com.dili.glasses.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.glasses.consts.BusinessMessageTypeConstants;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.dto.ReadBusinessServerRequestDto;
import com.dili.glasses.domain.dto.UpdateSystemConfigRequestDto;
import com.dili.glasses.domain.dto.WriteBusinessServerRequestDto;
import com.dili.glasses.domain.entity.TerminalConfig;
import com.dili.glasses.domain.send.ReadDataSendProtocol;
import com.dili.glasses.domain.send.WriteDataSendProtocol;
import com.dili.glasses.service.SystemConfigService;
import com.dili.glasses.service.TerminalConfigService;
import com.dili.glasses.utils.PageExecUtil;
import com.dili.ss.util.ByteArrayUtils;
import com.rabbitmq.client.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 命令消费组件
 *
 * @author wangmi
 */
@Component
@Slf4j
public class CmdListener implements ChannelAwareMessageListener {

    private final TerminalConfigService terminalConfigService;

    private final SystemConfigService systemConfigService;

    @Autowired
    public CmdListener(TerminalConfigService terminalConfigService, SystemConfigService systemConfigService) {
        this.terminalConfigService = terminalConfigService;
        this.systemConfigService = systemConfigService;
    }

    @Override
    @RabbitListener(queues = "#{rabbitConfiguration.queueName()}")
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            log.info("收到消息: " + message);
            String data = new String(message.getBody(), StandardCharsets.UTF_8);
            //消息类型转换
            JSONObject jsonObject = JSON.parseObject(data);
            messageDeal(jsonObject.getByte("messageType"), data);
            // 采用手动应答模式, 手动确认应答更为安全稳定
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            log.error("{}", e);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    /**
     * 消息处理
     *
     * @param messageType 消息类型
     * @param json        请求json
     */
    private void messageDeal(Byte messageType, String json) {
        switch (messageType) {
            case BusinessMessageTypeConstants.READ_FROM_TERMINAL:
                //读取终端数据指令
                ReadBusinessServerRequestDto readDto = JSON.parseObject(json, ReadBusinessServerRequestDto.class);
                ReadDataSendProtocol readDataSendProtocol = new ReadDataSendProtocol(readDto.getTerminalId(),
                        BusinessMessageTypeConstants.READ_FROM_TERMINAL, readDto.getRecordNo(), readDto.getDataSign());
                sendMsgToTerminal(readDataSendProtocol.getTerminalId(), readDataSendProtocol.toByteArray());
                break;
            case BusinessMessageTypeConstants.WRITE_TO_TERMINAL:
                //写入终端数据指令
                WriteBusinessServerRequestDto writeDto = JSON.parseObject(json, WriteBusinessServerRequestDto.class);
                WriteDataSendProtocol writeDataSendProtocol = new WriteDataSendProtocol(writeDto.getTerminalId(),
                        BusinessMessageTypeConstants.READ_FROM_TERMINAL, writeDto.getRecordNo(), writeDto.getParams());
                sendMsgToTerminal(writeDataSendProtocol.getTerminalId(), writeDataSendProtocol.toByteArray());
                break;
            case BusinessMessageTypeConstants.UPDATE_CONFIG:
                //更新配置信息
                UpdateSystemConfigRequestDto updateSystemConfigRequestDto = JSON.parseObject(json, UpdateSystemConfigRequestDto.class);

                if (CollectionUtils.isEmpty(updateSystemConfigRequestDto.getTerminalIdSet())) {
                    //刷新配置的缓存
                    systemConfigService.refreshCache();
                    batchUpdateTerminalConfig();
                } else {
                    updateAssignTerminalConfig(updateSystemConfigRequestDto);
                }
                break;
            default:
                log.warn("业务服务器消息类型不正确:{}", messageType);
        }
    }

    /**
     * 向终端发送消息
     *
     * @param terminalId 终端id
     * @param dataArr    数据数组
     */
    private void sendMsgToTerminal(int terminalId, byte[] dataArr) {
        //获取终端号获取ChannelId
        if (!NettyConstant.TERMINAL_CHANNEL_ID_MAP.containsKey(terminalId)) {
            log.error("终端不在线，无法发送指令：终端号:{}", terminalId);
            return;
        }

        ChannelId channelId = NettyConstant.TERMINAL_CHANNEL_ID_MAP.get(terminalId);
        ChannelHandlerContext channelHandlerContext = NettyConstant.CHANNEL_MAP.get(channelId);
        channelHandlerContext.writeAndFlush(dataArr);
    }

    /**
     * 每次最多下发数量的大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 批量向终端下发指令
     */
    private void batchUpdateTerminalConfig() {
        //获取目前在服务器上注册的终端id
        Collection<Integer> values = NettyConstant.CHANNEL_ID_TERMINAL_MAP.values();
        batchSendUpdateSetting(values);
    }

    /**
     * 批量向设备发送设置指令
     *
     * @param values 终端id
     */
    private void batchSendUpdateSetting(Collection<Integer> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            //分页执行指令下发
            PageExecUtil.pageExec(BATCH_SIZE, values, (list) -> {
                List<TerminalConfig> terminalConfigs = terminalConfigService.findAllByteTerminalId(new HashSet<>(list));
                Map<Integer, List<TerminalConfig>> group = terminalConfigs.stream().collect(Collectors.groupingBy(TerminalConfig::getTerminalId));

                group.forEach((terminalId, configList) -> {
                    //批量下发指令
                    ChannelId channelId = NettyConstant.TERMINAL_CHANNEL_ID_MAP.get(terminalId);
                    if (channelId != null) {
                        Map<Byte, Byte[]> map = new HashMap<>(configList.size());
                        configList.forEach(ele -> map.put(ele.getType(), intToByteArr(ele.getContent(), ele.getSize())));
                        WriteDataSendProtocol writeDataSendProtocol = new WriteDataSendProtocol(terminalId, (byte) 0x05, 0, map);
                        ChannelHandlerContext channelHandlerContext = NettyConstant.CHANNEL_MAP.get(channelId);
                        if (channelHandlerContext != null) {
                            channelHandlerContext.writeAndFlush(writeDataSendProtocol.toByteArray());
                        }
                    }
                });
            });
        }
    }

    /**
     * 更新指定设备的配置
     *
     * @param updateSystemConfigRequestDto 请求dto
     */
    private void updateAssignTerminalConfig(UpdateSystemConfigRequestDto updateSystemConfigRequestDto) {

        Set<Integer> existsTerminalId = new HashSet<>(updateSystemConfigRequestDto.getTerminalIdSet().size());
        updateSystemConfigRequestDto.getTerminalIdSet().forEach(terminalId -> {
            if (NettyConstant.TERMINAL_CHANNEL_ID_MAP.containsKey(terminalId)) {
                existsTerminalId.add(terminalId);
            }
        });

        batchSendUpdateSetting(existsTerminalId);
    }

    /**
     * int类型转为byte数组
     *
     * @param content int值大小
     * @param size    实际大小
     * @return byte数组
     */
    private Byte[] intToByteArr(int content, int size) {
        if (size == 1) {
            return new Byte[]{(byte) content};
        } else if (size == 2) {
            return ByteArrayUtils.toObjects(ByteArrayUtils.short2bytes2((short) content));
        } else if (size == 4) {
            return ByteArrayUtils.toObjects(ByteArrayUtils.int2byte2(content));
        }
        return null;
    }

}

package com.dili.glasses.factory.strategy.protocol;

import com.alibaba.fastjson.JSON;
import com.dili.glasses.boot.RabbitConfiguration;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.domain.TerminalSettingData;
import com.dili.glasses.domain.dto.SocketReadResponseDataDto;
import com.dili.glasses.domain.entity.TerminalConfig;
import com.dili.glasses.domain.es.TerminalReportRecord;
import com.dili.glasses.domain.receive.ReadDataReceiveProtocol;
import com.dili.glasses.domain.send.WriteDataSendProtocol;
import com.dili.glasses.factory.DataParser;
import com.dili.glasses.service.ReportDataService;
import com.dili.glasses.service.TerminalConfigService;
import com.dili.glasses.service.TerminalService;
import com.dili.glasses.utils.MessageDelaySender;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.util.ByteUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-11 11:41
 * @description 读取数据策略
 **/
@Component
@Slf4j
public class ReadDataStrategy extends BaseStrategy<ReadDataReceiveProtocol> {

    private final RabbitTemplate rabbitTemplate;

    private final DataParser dataParser;

    private final TerminalConfigService terminalConfigService;

    private final TerminalService terminalService;

    private final ReportDataService reportDataService;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public ReadDataStrategy(RabbitTemplate rabbitTemplate, DataParser dataParser, TerminalConfigService terminalConfigService, TerminalService terminalService, ReportDataService reportDataService, MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.rabbitTemplate = rabbitTemplate;
        this.dataParser = dataParser;
        this.terminalConfigService = terminalConfigService;
        this.terminalService = terminalService;
        this.reportDataService = reportDataService;
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, ReadDataReceiveProtocol protocol) {
        //1. 解析数据
        TerminalSettingData terminalSettingData = dataParser.parseSettingData(protocol);

        //2. 存储一份数据到es中
//        TerminalReportRecord terminalReportRecord = new TerminalReportRecord();
//        BeanUtils.copyProperties(terminalSettingData, terminalReportRecord);
//        reportDataService.addData(terminalReportRecord);

        //3. 记录号为指定值，说明是socket服务器读取的
        if (protocol.getRecordNo().equals(NettyConstant.SOCKET_READ_DATA_RECORD_NO)) {
            systemReadData(context, protocol, terminalSettingData);
        } else {
            //4. 解析数据，发送至业务服务器
            SocketReadResponseDataDto socketReadResponseDataDto = new SocketReadResponseDataDto();
            BeanUtils.copyProperties(terminalSettingData, socketReadResponseDataDto);
            rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE, RabbitConfiguration.REPORT_ROUTING_KEY, JSON.toJSONString(socketReadResponseDataDto));
        }
    }


    /**
     * 系统读取配置数据的处理
     *
     * @param terminalSettingData 终端配置信息
     */
    private void systemReadData(ChannelHandlerContext context, ReadDataReceiveProtocol protocol, TerminalSettingData terminalSettingData) {
        //1. 获取设备的配置信息
        List<TerminalConfig> allTerminalConfig = terminalConfigService.findAllTerminalConfig(terminalSettingData.getTerminalId());

        //2. 对比设备上的配置和服务器的配置，以服务器的配置为准，更新设备上的配置
        Map<Byte, Byte[]> needUpdateConfig = new HashMap<>(1);
        allTerminalConfig.forEach(ele -> {
            if (!terminalSettingData.getConfig().containsKey(ele.getType())) {
                Byte[] bytes = intToByteArr(ele.getContent(), ele.getSize());
                if (bytes != null) {
                    needUpdateConfig.put(ele.getType(), bytes);
                }
            } else {
                int config = terminalSettingData.getConfig().get(ele.getType());
                if (config != ele.getContent()) {
                    Byte[] bytes = intToByteArr(ele.getContent(), ele.getSize());
                    if (bytes != null) {
                        needUpdateConfig.put(ele.getType(), bytes);
                    }
                }
            }
        });

        //3.更新设备版本信息
        terminalService.updateTerminalVersion(protocol.getTerminalId(), terminalSettingData.getHardwareVersion(), terminalSettingData.getSoftwareVersion());

        //4.更新设备配置
        if (needUpdateConfig.size() > 0) {
            Map<Byte, Byte[]> allConfig = new HashMap<>(allTerminalConfig.size());
            allTerminalConfig.forEach(config -> {
                Byte[] bytes = intToByteArr(config.getContent(), config.getSize());
                if (bytes != null) {
                    allConfig.put(config.getType(), bytes);
                }
            });

            WriteDataSendProtocol writeDataSendProtocol = new WriteDataSendProtocol(protocol.getTerminalId(), (byte) 0x05, NettyConstant.SOCKET_READ_DATA_RECORD_NO, allConfig);
            channelWrite(context.channel().id(), writeDataSendProtocol.toByteArray());
        }
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


    @Override
    public Class<ReadDataReceiveProtocol> getRequestProtocolClass() {
        return ReadDataReceiveProtocol.class;
    }

    public static void main(String[] args) {
        int value = 6000;
        short shortValue = (short) value;
        short changedValue = (short) (~shortValue + 1);
        System.out.println(Arrays.toString(short2byte(changedValue)));
    }

    public static byte[] short2byte(short s) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = 16 - (i + 1) * 8; //因为byte占4个字节，所以要计算偏移量
            b[i] = (byte) ((s >> offset) & 0xff); //把16位分为2个8位进行分别存储
        }
        return b;
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }
}

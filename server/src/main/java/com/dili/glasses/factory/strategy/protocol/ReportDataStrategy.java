package com.dili.glasses.factory.strategy.protocol;

import com.alibaba.fastjson.JSON;
import com.dili.glasses.boot.RabbitConfiguration;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.consts.ViolationType;
import com.dili.glasses.domain.ViolationDataReportDto;
import com.dili.glasses.domain.dto.ViolationDataDto;
import com.dili.glasses.domain.entity.TerminalConfig;
import com.dili.glasses.domain.es.TerminalReportRecord;
import com.dili.glasses.domain.receive.ReportDataReceiveProtocol;
import com.dili.glasses.domain.send.ReportDataSendProtocol;
import com.dili.glasses.domain.send.WriteDataSendProtocol;
import com.dili.glasses.factory.DataParser;
import com.dili.glasses.service.ReportDataService;
import com.dili.glasses.service.TerminalConfigService;
import com.dili.glasses.utils.MessageDelaySender;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-03 11:32
 * @description 设备主动上传违规数据
 **/
@Slf4j
@Component
public class ReportDataStrategy extends BaseStrategy<ReportDataReceiveProtocol> {

    private final RabbitTemplate rabbitTemplate;

    private final DataParser reportDataParser;

    private final ReportDataService reportDataService;

    private final TerminalConfigService terminalConfigService;

    private final MessageDelaySender messageDelaySender;

    @Autowired
    public ReportDataStrategy(RabbitTemplate rabbitTemplate, DataParser reportDataParser,
                              ReportDataService reportDataService, TerminalConfigService terminalConfigService, MessageDelaySender messageDelaySender) {
        super(messageDelaySender);
        this.rabbitTemplate = rabbitTemplate;
        this.reportDataParser = reportDataParser;
        this.reportDataService = reportDataService;
        this.terminalConfigService = terminalConfigService;
        this.messageDelaySender = messageDelaySender;
    }

    @Override
    public void exec(ChannelHandlerContext context, ReportDataReceiveProtocol protocol) {
        String msg = JSON.toJSONString(protocol.getViolationDataMap());
        log.info("收到上报数据：{}", msg);

        //数据解析
        TerminalReportRecord terminalReportRecord = reportDataParser.parseData(protocol);

        log.info("解析后的数据：{}", JSON.toJSONString(terminalReportRecord));

        //违规数据判断，如果存在违规数据，则放入队列通知
        List<ViolationDataReportDto> violationDataReportDtoList = dataViolation(terminalReportRecord);
        terminalReportRecord.setViolationDataList(violationDataReportDtoList);

        //存储数据
        reportDataService.addData(terminalReportRecord);

        if (CollectionUtils.isNotEmpty(violationDataReportDtoList)) {
            //构建违规数据实体并上报
            ViolationDataDto violationDataDto = new ViolationDataDto();
            violationDataDto.setViolationDataReportDtoList(violationDataReportDtoList);
            rabbitTemplate.convertAndSend(RabbitConfiguration.TOPIC_EXCHANGE, RabbitConfiguration.REPORT_QUEUE, JSON.toJSONString(violationDataDto));
        }

        response(context, protocol);

        //存储空间不足，清除配置
        if (terminalReportRecord.getStorageNoSpace() > 1) {
            //1. 获取设备的配置信息
            List<TerminalConfig> allTerminalConfig = terminalConfigService.findAllTerminalConfig(terminalReportRecord.getTerminalId());
            Map<Byte, Byte[]> allConfig = new HashMap<>(allTerminalConfig.size());
            allTerminalConfig.forEach(config -> {
                Byte[] bytes;
                //设置清除缓存的配置为1
                if (config.getType() == 0x0f) {
                    bytes = intToByteArr(1, config.getSize());
                } else {
                    bytes = intToByteArr(config.getContent(), config.getSize());
                }

                if (bytes != null) {
                    allConfig.put(config.getType(), bytes);
                }
            });

            WriteDataSendProtocol writeDataSendProtocol = new WriteDataSendProtocol(protocol.getTerminalId(), (byte) 0x05, NettyConstant.SOCKET_READ_DATA_RECORD_NO, allConfig);
            channelWrite(context.channel().id(), writeDataSendProtocol.toByteArray());
        }
    }


    /**
     * 数据违规检测
     *
     * @param terminalReportRecord 终端上报的数据
     */
    private List<ViolationDataReportDto> dataViolation(TerminalReportRecord terminalReportRecord) {
        int terminalId = terminalReportRecord.getTerminalId();
        List<ViolationDataReportDto> violationDataReportDtoList = new ArrayList<>();
        List<TerminalConfig> allTerminalConfig = terminalConfigService.findAllTerminalConfig(terminalId);
        if (CollectionUtils.isEmpty(allTerminalConfig)) {
            log.error("终端的配置信息不存在，无法验证上报数据是否合法");
        }
        //1. 验证光感
        Integer lightMin = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x04)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer lightMax = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x05)).map(TerminalConfig::getContent).findAny().orElse(null);
        if (lightMin != null && lightMax != null) {
            if (terminalReportRecord.getLightIntensity() < lightMin || terminalReportRecord.getLightIntensity() > lightMax) {
                //光感不合法
                ViolationDataReportDto violationDataReportDto = new ViolationDataReportDto();
                violationDataReportDto.setType(ViolationType.LIGHT_INTENSITY);
                violationDataReportDto.setCurrentValue(terminalReportRecord.getLightIntensity());
                violationDataReportDto.setMinValue(lightMin);
                violationDataReportDto.setMaxValue(lightMax);
                violationDataReportDtoList.add(violationDataReportDto);
            }
        }
        //2. 验证陀螺仪
        Integer gyroXMin = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x06)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer gyroXMax = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x07)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer gyroYMin = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x08)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer gyroYMax = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x09)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer gyroZMin = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x0a)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer gyroZMax = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x0b)).map(TerminalConfig::getContent).findAny().orElse(null);
        if (gyroXMin != null && gyroXMax != null) {
            if (terminalReportRecord.getGyroAngle().getX() < gyroXMin || terminalReportRecord.getGyroAngle().getX() > gyroXMax) {
                // 陀螺仪X不合法
                ViolationDataReportDto violationDataReportDto = new ViolationDataReportDto();
                violationDataReportDto.setType(ViolationType.GYRO_X);
                violationDataReportDto.setCurrentValue(terminalReportRecord.getGyroAngle().getX());
                violationDataReportDto.setMinValue(gyroXMin);
                violationDataReportDto.setMaxValue(gyroXMax);
                violationDataReportDtoList.add(violationDataReportDto);
            }
        }
        if (gyroYMin != null && gyroYMax != null) {
            if (terminalReportRecord.getGyroAngle().getY() < gyroYMin || terminalReportRecord.getGyroAngle().getY() > gyroYMax) {
                // 陀螺仪Y不合法
                ViolationDataReportDto violationDataReportDto = new ViolationDataReportDto();
                violationDataReportDto.setType(ViolationType.GYRO_Y);
                violationDataReportDto.setCurrentValue(terminalReportRecord.getGyroAngle().getY());
                violationDataReportDto.setMinValue(gyroYMin);
                violationDataReportDto.setMaxValue(gyroYMax);
                violationDataReportDtoList.add(violationDataReportDto);
            }
        }
        if (gyroZMin != null && gyroZMax != null) {
            if (terminalReportRecord.getGyroAngle().getZ() < gyroZMin || terminalReportRecord.getGyroAngle().getZ() > gyroZMax) {
                // 陀螺仪Z不合法
                ViolationDataReportDto violationDataReportDto = new ViolationDataReportDto();
                violationDataReportDto.setType(ViolationType.GYRO_Z);
                violationDataReportDto.setCurrentValue(terminalReportRecord.getGyroAngle().getZ());
                violationDataReportDto.setMinValue(gyroZMin);
                violationDataReportDto.setMaxValue(gyroZMax);
                violationDataReportDtoList.add(violationDataReportDto);
            }
        }
        //3. 验证距离
        Integer distanceMin = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x0c)).map(TerminalConfig::getContent).findAny().orElse(null);
        Integer distanceMax = allTerminalConfig.stream().filter(ele -> ele.getType().equals((byte) 0x0d)).map(TerminalConfig::getContent).findAny().orElse(null);
        if (distanceMin != null && distanceMax != null) {
            if (terminalReportRecord.getDistance() < distanceMin || terminalReportRecord.getDistance() > distanceMax) {
                // 距离不合法
                ViolationDataReportDto violationDataReportDto = new ViolationDataReportDto();
                violationDataReportDto.setType(ViolationType.DISTANCE);
                violationDataReportDto.setCurrentValue(terminalReportRecord.getDistance());
                violationDataReportDto.setMinValue(distanceMin);
                violationDataReportDto.setMaxValue(distanceMax);
                violationDataReportDtoList.add(violationDataReportDto);
            }
        }
        return violationDataReportDtoList;
    }


    /**
     * 响应客户端
     *
     * @param context         信道上下文
     * @param requestProtocol 请求内容
     */
    private void response(ChannelHandlerContext context, ReportDataReceiveProtocol requestProtocol) {
        ReportDataSendProtocol responseProtocol = new ReportDataSendProtocol(requestProtocol.getTerminalId(), requestProtocol.getCmd(), requestProtocol.getLength());
        channelWrite(context.channel().id(), responseProtocol.toByteArray());
    }

    @Override
    public Class<ReportDataReceiveProtocol> getRequestProtocolClass() {
        return ReportDataReceiveProtocol.class;
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

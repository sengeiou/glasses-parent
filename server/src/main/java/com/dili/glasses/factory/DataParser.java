package com.dili.glasses.factory;

import com.dili.glasses.domain.TerminalSettingData;
import com.dili.glasses.domain.es.TerminalReportRecord;
import com.dili.glasses.domain.receive.ReadDataReceiveProtocol;
import com.dili.glasses.domain.receive.ReportDataReceiveProtocol;
import com.dili.glasses.factory.strategy.data.ByteToIntStrategy;
import com.dili.glasses.factory.strategy.data.GPSStrategy;
import com.dili.glasses.factory.strategy.data.GyroAngleStrategy;
import com.dili.glasses.factory.strategy.data.TerminalTimeStrategy;
import com.dili.ss.util.ByteArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Ren HongWei
 * @date 2019-09-14 14:45
 * @description 上报数据解析
 **/
@Component
@Slf4j
public class DataParser {

    private final TerminalTimeStrategy terminalTimeStrategy;

    private final GyroAngleStrategy gyroAngleStrategy;

    private final GPSStrategy gpsStrategy;

    private final ByteToIntStrategy byteToIntStrategy;

    @Autowired
    public DataParser(TerminalTimeStrategy terminalTimeStrategy, GyroAngleStrategy gyroAngleStrategy, GPSStrategy gpsStrategy, ByteToIntStrategy byteToIntStrategy) {
        this.terminalTimeStrategy = terminalTimeStrategy;
        this.gyroAngleStrategy = gyroAngleStrategy;
        this.gpsStrategy = gpsStrategy;
        this.byteToIntStrategy = byteToIntStrategy;
    }


    /**
     * 解析上报的数据
     *
     * @param protocol 上报数据的协议
     * @return 终端上报的数据
     */
    public TerminalReportRecord parseData(ReportDataReceiveProtocol protocol) {
        TerminalReportRecord terminalReportRecord = new TerminalReportRecord();
        terminalReportRecord.setTerminalId(protocol.getTerminalId());
        protocol.getViolationDataMap().forEach((type, value) -> {
            byte[] primitivesBytes = ByteArrayUtils.toPrimitives(value);
            switch (type) {
                case 0x01:
                    terminalReportRecord.setTerminalTime(terminalTimeStrategy.parseData(primitivesBytes));
                    break;
                case 0x02:
                    terminalReportRecord.setDistance(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x03:
                    terminalReportRecord.setDuration(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x04:
                    terminalReportRecord.setLightIntensity(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x05:
                    terminalReportRecord.setGyroAngle(gyroAngleStrategy.parseData(primitivesBytes));
                    break;
                case 0x06:
                    terminalReportRecord.setGps(gpsStrategy.parseData(primitivesBytes));
                    break;
                case 0x07:
                    terminalReportRecord.setStorageNoSpace(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x08:
                    terminalReportRecord.setBatteryVoltage(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                default:
            }
        });
        return terminalReportRecord;
    }

    /**
     * 解析设备的配置数据
     *
     * @param protocol 协议
     * @return 设备的配置数据
     */
    public TerminalSettingData parseSettingData(ReadDataReceiveProtocol protocol) {
        TerminalSettingData terminalSettingData = new TerminalSettingData();
        terminalSettingData.setTerminalId(protocol.getTerminalId());
        terminalSettingData.setConfig(new HashMap<>(15));
        terminalSettingData.setRecordNo(protocol.getRecordNo());
        protocol.getDataMap().forEach((type, value) -> {

            byte[] primitivesBytes = ByteArrayUtils.toPrimitives(value);
            //0x01 到 0x0f 都是配置信息
            if (type <= 0x0f || type == 0x16) {
                terminalSettingData.getConfig().put(type, byteToIntStrategy.parseData(primitivesBytes));
            }

            //0x10 到 0x15是设备上的实时信息
            switch (type) {
                case 0x10:
                    terminalSettingData.setDistance(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x11:
                    terminalSettingData.setLightIntensity(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x13:
                    terminalSettingData.setGps(gpsStrategy.parseData(primitivesBytes));
                    break;
                case 0x14:
                    terminalSettingData.setHardwareVersion(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                case 0x15:
                    terminalSettingData.setSoftwareVersion(byteToIntStrategy.parseData(primitivesBytes));
                    break;
                default:
            }
        });
        return terminalSettingData;
    }
}

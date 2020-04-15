package com.dili.glasses.domain;

import com.dili.glasses.domain.es.helper.GPS;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Ren HongWei
 * @date 2019-09-19 15:55
 * @description
 **/
@Data
public class TerminalSettingReportData {

    /**
     * 终端id
     */
    private int terminalId;

    /**
     * 记录号
     */
    private int recordNo;

    /**
     * 距离，单位cm
     */
    @Field(type = FieldType.Integer)
    private int distance;

    /**
     * 光感强度
     */
    @Field(type = FieldType.Integer)
    private int lightIntensity;

    /**
     * gps
     */
    @Field(type = FieldType.Object)
    private GPS gps;


    /**
     * 电池电压，单位 1毫伏
     */
    @Field(type = FieldType.Integer)
    private int batteryVoltage;

    /**
     * 硬件版本
     */
    private int hardwareVersion;

    /**
     * 软件版本
     */
    private int softwareVersion;


}

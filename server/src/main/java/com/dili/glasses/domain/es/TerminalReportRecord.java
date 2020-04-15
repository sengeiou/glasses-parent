package com.dili.glasses.domain.es;

import com.dili.glasses.consts.ElasticSearchConstants;
import com.dili.glasses.domain.ViolationDataReportDto;
import com.dili.glasses.domain.es.helper.GPS;
import com.dili.glasses.domain.es.helper.GyroAngle;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author Ren HongWei
 * @date 2019-09-14 14:14
 * @description 终端上报数据记录
 **/
@Data
@Document(indexName = ElasticSearchConstants.TERMINAL_REPORT_INDEX, type = ElasticSearchConstants.TERMINAL_REPORT_TYPE)
public class TerminalReportRecord {

    @Id
    private String id;

    /**
     * 终端id
     */
    @Field(type = FieldType.Integer)
    private int terminalId;

    /**
     * 终端上的时间
     */
    @Field(type = FieldType.Date)
    private Date terminalTime;

    /**
     * 距离，单位cm
     */
    @Field(type = FieldType.Integer)
    private int distance;

    /**
     * 持续时间，单位s
     */
    @Field(type = FieldType.Integer)
    private int duration;

    /**
     * 光感强度
     */
    @Field(type = FieldType.Integer)
    private int lightIntensity;

    /**
     * 陀螺仪角度
     */
    @Field(type = FieldType.Object)
    private GyroAngle gyroAngle;

    /**
     * gps
     */
    @Field(type = FieldType.Object)
    private GPS gps;

    /**
     * 存储空间不足
     */
    @Field(type = FieldType.Integer)
    private int storageNoSpace;

    /**
     * 电池电压，单位 1毫伏
     */
    @Field(type = FieldType.Integer)
    private int batteryVoltage;

    /**
     * 违规数据
     */
    @Field(type = FieldType.Nested)
    private List<ViolationDataReportDto> violationDataList;

    /**
     * 上报时间
     */
    @Field(type = FieldType.Date)
    private Date reportTime;
}

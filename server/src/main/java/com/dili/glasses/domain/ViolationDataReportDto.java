package com.dili.glasses.domain;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Ren HongWei
 * @date 2019-09-19 18:27
 * @description 违规数据上报dto
 **/
@Data
public class ViolationDataReportDto {

    /**
     * 违规数据类型
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 当前值
     */
    @Field(type = FieldType.Double)
    private double currentValue;

    /**
     * 允许的最小值
     */
    @Field(type = FieldType.Double)
    private double minValue;

    /**
     * 允许的最大值
     */
    @Field(type = FieldType.Double)
    private double maxValue;
}

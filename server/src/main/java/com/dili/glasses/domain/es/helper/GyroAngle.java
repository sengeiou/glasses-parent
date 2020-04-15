package com.dili.glasses.domain.es.helper;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Ren HongWei
 * @date 2019-09-14 14:27
 * @description 陀螺仪角度
 **/
@Data
public class GyroAngle {

    @Field(type = FieldType.Double)
    private double x;

    @Field(type = FieldType.Double)
    private double y;

    @Field(type = FieldType.Double)
    private double z;
}

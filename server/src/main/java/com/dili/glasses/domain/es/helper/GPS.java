package com.dili.glasses.domain.es.helper;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Ren HongWei
 * @date 2019-09-14 15:17
 * @description
 **/
@Data
public class GPS {

    /**
     * 经度
     */
    @Field(type = FieldType.Keyword)
    private String longitude;

    /**
     * 纬度
     */
    @Field(type = FieldType.Keyword)
    private String latitude;
}

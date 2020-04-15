package com.dili.glasses.factory.strategy.data;

import com.dili.glasses.domain.es.helper.GPS;
import com.dili.ss.redis.service.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Ren HongWei
 * @date 2019-09-14 15:17
 * @description
 **/
@Component
@Slf4j
public class GPSStrategy implements DataStrategy<GPS> {

    /**
     * 数据字节长度
     */
    private static final int DATA_LENGTH = 10;

    @Override
    public GPS parseData(byte[] data) {

        if (data.length != DATA_LENGTH) {
            log.error("【GPS】解析错误：数据长度不正确");
            return null;
        }

        GPS gps = new GPS();

        byte[] first = Arrays.copyOfRange(data, 0, 5);
        byte[] second = Arrays.copyOfRange(data, 5, 10);


        gps.setLatitude(getNumString(first));
        gps.setLongitude(getNumString(second));
        return gps;
    }

    /**
     * 字节转为经纬度算法
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    private String getNumString(byte[] bytes) {
        double num;

        int dd = bytes[0] & 0xff;
        int mm = bytes[1] & 0xff;
        int ss = bytes[2] & 0xff;
        int xx = bytes[3] & 0xff;
        num = dd + ((mm + (ss + (xx / 255.0)) / 255.0) / 60.0);

        return String.valueOf(num);
    }
}

package com.dili.glasses.factory.strategy.data;

import com.dili.glasses.domain.es.helper.GyroAngle;
import com.dili.ss.util.ByteArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Ren HongWei
 * @date 2019-09-14 15:11
 * @description
 **/
@Component
@Slf4j
public class GyroAngleStrategy implements DataStrategy<GyroAngle> {

    /**
     * 数据字节长度
     */
    private static final int DATA_LENGTH = 6;

    @Override
    public GyroAngle parseData(byte[] data) {
        if (data.length != DATA_LENGTH) {
            log.error("【陀螺仪角度】解析错误：数据长度不正确");
            return null;
        }

        GyroAngle gyroAngle = new GyroAngle();
        gyroAngle.setX(ByteArrayUtils.bytes2short2(Arrays.copyOfRange(data, 0, 2))[0] / 100.0);
        gyroAngle.setY(ByteArrayUtils.bytes2short2(Arrays.copyOfRange(data, 2, 4))[0] / 100.0);
        gyroAngle.setZ(ByteArrayUtils.bytes2short2(Arrays.copyOfRange(data, 4, 6))[0] / 100.0);
        return gyroAngle;
    }


}

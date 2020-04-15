package com.dili.glasses.factory.strategy.data;

import com.dili.ss.util.ByteArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Ren HongWei
 * @date 2019-09-16 09:46
 * @description byte 转为 int
 **/
@Component
@Slf4j
public class ByteToIntStrategy implements DataStrategy<Integer> {


    @Override
    public Integer parseData(byte[] data) {
        if (data.length == 1) {
            return data[0] & 0xff;
        } else if (data.length == 2) {
            return (int) ByteArrayUtils.bytes2short2(data)[0];
        }
        return ByteArrayUtils.byte2int2(data);
    }
}

package com.dili.glasses.domain.receive;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-03 11:15
 * @description 违规数据上报协议
 **/
@Getter
@Setter
public class ReportDataReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 违规数据
     */
    private Map<Byte, Byte[]> violationDataMap;

    /**
     * 数据个数
     */
    private byte length;

    @Override
    public void parseInformation(Byte[] information) {
        if (information.length >= 1) {
            //要上传的数据个数
            int length = information[0] & 0xff;
            this.length = (byte) length;
            violationDataMap = new HashMap<>(length);
            int index = 1;
            //解析数据
            for (int i = 0; i < length; i++) {
                byte key = information[index];
                int valueLength = information[index + 1] & 0xff;
                Byte[] value = Arrays.copyOfRange(information, index + 2, index + 2 + valueLength);
                violationDataMap.put(key, value);
                index = index + 2 + valueLength;
            }
        }
    }
}

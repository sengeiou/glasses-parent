package com.dili.glasses.domain.receive;

import com.dili.ss.util.ByteArrayUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-10 17:22
 * @description 获取数据响应
 **/
@Getter
@Setter
public class ReadDataReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 记录号
     */
    private Integer recordNo;

    /**
     * 数据map
     */
    private Map<Byte, Byte[]> dataMap;

    /**
     * 数据个数
     */
    private byte length;


    @Override
    public void parseInformation(Byte[] information) {
        if (information.length >= 5) {
            //要上传的数据个数
            int length = information[0] & 0xff;
            this.length = (byte) length;
            dataMap = new HashMap<>(length);
            int index = 1;

            //解析数据
            for (int i = 0; i < length; i++) {
                byte key = information[index];
                int valueLength = information[index + 1] & 0xff;
                Byte[] value = Arrays.copyOfRange(information, index + 2, index + 2 + valueLength);
                dataMap.put(key, value);
                index = index + 2 + valueLength;
            }
            //记录号
            this.recordNo = ByteArrayUtils.byte2int2(ByteArrayUtils.toPrimitives(Arrays.copyOfRange(information, index, index + 4)));
        }
    }
}

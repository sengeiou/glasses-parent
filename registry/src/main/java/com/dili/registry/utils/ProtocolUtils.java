package com.dili.registry.utils;

import com.dili.registry.domain.BaseProtocol;
import com.dili.ss.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 协议处理工具
 * 主要处理长度和CRC16内容
 */
public class ProtocolUtils {

    /**
     * 获取协议长度
     * @param t
     * @param <T>
     * @return
     */
    public static <T extends BaseProtocol> void fillLengthAndCrc16(T t){
        List<Field> fields = ReflectionUtils.getAccessibleFields(t.getClass(), true, true);
        int length = 0;

        try {
            for (Field field : fields) {
                if(field.getName().equals("crc") || field.getName().equals("length")){
                    continue;
                }
                //属性长度+1(逗号)
                length += field.get(t).toString().length() + 1;

            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        //当前长度 + length4位 + , + crc6位 + []
        length += 13;
        t.setLength(String.valueOf(length));
        t.setCrc("");
    }

}

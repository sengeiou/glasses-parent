package com.dili.glasses.domain.receive;

import com.dili.ss.util.ByteArrayUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author Ren HongWei
 * @date 2019-09-23 11:49
 * @description 写数据终端响应数据
 **/
@Getter
@Setter
public class WriteDataReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 是否写入成功
     */
    private boolean success;

    /**
     * 记录号
     */
    private int recordNo;

    @Override
    public void parseInformation(Byte[] information) {
        if (information.length >= 5) {
            success = information[0] == 1;
            recordNo = ByteArrayUtils.byte2int2(ByteArrayUtils.toPrimitives(Arrays.copyOfRange(information, 1, 5)));
        }
    }
}

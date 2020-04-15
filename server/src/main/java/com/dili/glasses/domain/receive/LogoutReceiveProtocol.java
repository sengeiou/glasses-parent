package com.dili.glasses.domain.receive;

import com.dili.ss.util.ByteArrayUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author Ren HongWei
 * @date 2019-09-02 18:21
 * @description 登出请求实体
 **/
@Getter
@Setter
public class LogoutReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 终端时间 4个字节
     */
    private Integer terminalTime;


    @Override
    public void parseInformation(Byte[] information) {
        if (information.length == 4) {
            Byte[] bytes = Arrays.copyOfRange(information, 0, 4);
            setTerminalTime(ByteArrayUtils.byte2int2(ByteArrayUtils.toPrimitives(bytes)));
        }
    }
}

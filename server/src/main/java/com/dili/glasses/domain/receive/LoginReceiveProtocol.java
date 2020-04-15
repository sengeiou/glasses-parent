package com.dili.glasses.domain.receive;

import com.dili.ss.util.ByteArrayUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * @author Ren HongWei
 * @date 2019-09-02 17:59
 * @description 登录请求协议
 **/
@Getter
@Setter
public class LoginReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 终端时间 4个字节
     */
    private Integer terminalTime;

    /**
     * 桩类型
     */
    private Byte pileType;


    @Override
    public void parseInformation(Byte[] information) {
        if (information.length == 5) {
            Byte[] bytes = Arrays.copyOfRange(information, 0, 4);
            setTerminalTime(ByteArrayUtils.byte2int2(ByteArrayUtils.toPrimitives(bytes)));
            this.setPileType(information[4]);
        }
    }
}

package com.dili.glasses.domain.receive;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-03 10:16
 * @description 接收简略协议
 **/
@Getter
@Setter
public class SimpleReceiveProtocol extends AbstractReceiveProtocol {

    private boolean success;

    @Override
    public void parseInformation(Byte[] information) {
        if (information.length >= 1) {
            success = information[0] == 1;
        }
    }
}

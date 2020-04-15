package com.dili.glasses.domain.receive;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-02 17:59
 * @description 登录请求协议
 **/
@Getter
@Setter
public class HeartBeatReceiveProtocol extends AbstractReceiveProtocol {

    /**
     * 心跳时间间隔
     */
    private Byte heartBeatInterval;


    @Override
    public void parseInformation(Byte[] information) {
        if (information.length == 1) {
            this.setHeartBeatInterval(information[0]);
        }
    }
}

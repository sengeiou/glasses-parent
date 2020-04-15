package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.SocketMessageType;
import com.dili.glasses.domain.es.helper.GPS;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-17 15:38
 * @description 接收到终端的数据
 **/
@Getter
@Setter
public class SocketReadResponseDataDto extends BaseMessageFromSocket {

    public SocketReadResponseDataDto() {
        super(SocketMessageType.READ_DATA_RESPONSE);
    }

    /**
     * 终端id
     */
    private int terminalId;

    /**
     * 记录号
     */
    private int recordNo;

    /**
     * 距离，单位cm
     */
    private int distance;

    /**
     * 光感强度
     */
    private int lightIntensity;

    /**
     * gps
     */
    private GPS gps;


    /**
     * 电池电压，单位 1毫伏
     */
    private int batteryVoltage;

    /**
     * 硬件版本
     */
    private int hardwareVersion;

    /**
     * 软件版本
     */
    private int softwareVersion;
}

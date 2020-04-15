package com.dili.glasses.consts;

/**
 * @author Ren HongWei
 * @date 2019-09-20 11:08
 * @description socket服务器消息类型
 **/
public interface SocketMessageType {

    /**
     * 读请求响应数据
     */
    String READ_DATA_RESPONSE = "READ_DATA_RESPONSE";


    /**
     * 写请求响应数据
     */
    String WRITE_DATA_RESPONSE = "WRITE_DATA_RESPONSE";

    /**
     * 违规数据
     */
    String VIOLATION_DATA = "VIOLATION_DATA";
}

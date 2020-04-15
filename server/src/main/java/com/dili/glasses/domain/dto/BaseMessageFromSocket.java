package com.dili.glasses.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ren HongWei
 * @date 2019-09-20 11:10
 * @description socket服务器传递的消息 -- 基础类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessageFromSocket {

    /**
     * 消息类型
     */
    private String messageType;
}

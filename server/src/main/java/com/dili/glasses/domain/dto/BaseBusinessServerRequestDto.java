package com.dili.glasses.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ren HongWei
 * @date 2019-09-11 10:13
 * @description 业务系统请求dto
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseBusinessServerRequestDto {

    /**
     * 消息类型
     *
     * @see com.dili.glasses.consts.BusinessMessageTypeConstants
     */
    private byte messageType;

}

package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.BusinessMessageTypeConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-11 10:52
 * @description 从终端读取数据请求dto
 **/
@Getter
@Setter
public class ReadBusinessServerRequestDto extends BaseBusinessServerRequestDto {

    public ReadBusinessServerRequestDto() {
        super(BusinessMessageTypeConstants.READ_FROM_TERMINAL);
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
     * 数据标识
     */
    private Byte[] dataSign;
}

package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.BusinessMessageTypeConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-11 10:53
 * @description 向服务器写入数据请求dto
 **/
@Getter
@Setter
public class WriteBusinessServerRequestDto extends BaseBusinessServerRequestDto {

    public WriteBusinessServerRequestDto() {
        super(BusinessMessageTypeConstants.WRITE_TO_TERMINAL);
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
     * 参数
     */
    private Map<Byte, Byte[]> params;

}

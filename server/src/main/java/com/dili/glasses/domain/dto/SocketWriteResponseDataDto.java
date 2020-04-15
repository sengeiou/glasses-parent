package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.SocketMessageType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-23 11:55
 * @description
 **/
@Getter
@Setter
public class SocketWriteResponseDataDto extends BaseMessageFromSocket {

    public SocketWriteResponseDataDto() {
        super(SocketMessageType.WRITE_DATA_RESPONSE);
    }

    private int terminalId;

    private int recordNo;

    private boolean success;
}

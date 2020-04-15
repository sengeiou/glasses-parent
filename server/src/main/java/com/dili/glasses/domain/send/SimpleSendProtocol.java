package com.dili.glasses.domain.send;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-03 10:16
 * @description 简略协议
 **/
@Getter
@Setter
public class SimpleSendProtocol extends AbstractSendProtocol {

    private boolean success;

    @Override
    public byte[] contentToInformation() {
        ByteBuf buf = Unpooled.buffer(1);
        buf.writeByte(success ? (byte) 1 : (byte) 0);
        return buf.array();
    }

    public SimpleSendProtocol(Integer terminalId, Byte cmd, boolean success) {
        super(terminalId, cmd);
        this.success = success;
    }
}

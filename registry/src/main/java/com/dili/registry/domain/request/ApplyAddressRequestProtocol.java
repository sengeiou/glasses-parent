package com.dili.registry.domain.request;

import com.dili.registry.domain.AbstractProtocol;
import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * 申请Server地址传输协议
 *
 * @author wangmi
 */
@Getter
@Setter
public class ApplyAddressRequestProtocol extends AbstractRequestProtocol {

    /**
     * 终端时间 4个字节
     */
    private Integer terminalTime;

    /**
     * 终端时间长度
     */
    private static final Integer TERMINAL_TIME_SIZE = 4;

    @Override
    public void parseInformation(Byte[] information) {
        if (information.length == TERMINAL_TIME_SIZE) {
            Byte[] bytes = Arrays.copyOfRange(information, 0, 4);
            setTerminalTime(ByteArrayUtils.byte2int2(ByteArrayUtils.toPrimitives(bytes)));
        }
    }
}

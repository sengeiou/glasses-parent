package com.dili.registry.domain.response;

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
public class ApplyAddressResponseProtocol extends AbstractResponseProtocol {

    /**
     * 终端时间 4个字节
     */
    private Integer terminalTime;

    /**
     * 服务器端口信息  6个字节
     */
    private Byte[] hostPort;

    /**
     * 缓冲区大小
     */
    private static final Integer BUFF_SIZE = 10;

    public ApplyAddressResponseProtocol(Integer terminalId, Byte cmd, Integer terminalTime, Byte[] hostPort) {
        super(terminalId, cmd);
        this.terminalTime = terminalTime;
        this.hostPort = hostPort;
    }

    @Override
    public byte[] contentToInformation() {
        ByteBuf buf = Unpooled.buffer(BUFF_SIZE);
        buf.writeInt(this.getTerminalTime());
        buf.writeBytes(ByteArrayUtils.toPrimitives(this.getHostPort()));
        return buf.array();
    }
}

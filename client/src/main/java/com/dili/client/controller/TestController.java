package com.dili.client.controller;

import com.dili.client.consts.NettyCache;
import com.dili.client.consts.NettyConstant;
import com.dili.client.domain.Protocol;
import com.dili.ss.domain.BaseOutput;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ren HongWei
 * @date 2019-08-30 10:41
 * @description 测试消息发送
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/send")
    public BaseOutput<Object> exec(String cmd) {

        Protocol protocol = new Protocol();
        protocol.setStx(NettyConstant.HEAD_DATA);
        protocol.setLength((short) 128);
        protocol.setTerminalId(1);
        protocol.setCmd((byte) 0x04);
        protocol.setSource((byte) 2);
        protocol.setTransferType((byte) 0);
        protocol.setEndMark((byte) 0x68);
        protocol.setChecksum(16);
        protocol.setInformation(ArrayUtils.toObject(cmd.getBytes()));

        NettyCache.channel.writeAndFlush(protocol.toByteArray());
        return BaseOutput.success("命令发送完成");
    }
}

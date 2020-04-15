package com.dili.registry.domain;

import com.dili.ss.util.ByteArrayUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 登录类传输协议
 * @author wangmi
 */
public class LoginTypeAbstractProtocol extends AbstractProtocol {

    //终端类型:默认是0，主要是备用，如果有多种类型时用这个字节区分
    private Byte terminalType;


}

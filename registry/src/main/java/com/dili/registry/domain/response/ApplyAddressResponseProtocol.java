package com.dili.registry.domain.response;

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
public class ApplyAddressResponseProtocol extends AbstractProtocol {


    @Override
    public void parseDatas() {

    }
}

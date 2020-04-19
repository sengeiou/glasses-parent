package com.dili.registry.domain.response;

import com.dili.registry.domain.BaseProtocol;
import lombok.Getter;
import lombok.Setter;

/**
 * 申请Server地址传输协议
 *
 * @author wangmi
 */
@Getter
@Setter
public abstract class BaseResponseProtocol extends BaseProtocol {



    /**
     * 子类根据类型编码除type, length和crc外的所有字段
     * 用于ProtocolEncoder
     * @return
     */
    public abstract String encodeDatas();
}

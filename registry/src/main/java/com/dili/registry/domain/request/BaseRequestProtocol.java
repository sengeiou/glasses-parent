package com.dili.registry.domain.request;

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
public abstract class BaseRequestProtocol extends BaseProtocol {


    /**
     * 解析数据域
     */
    public abstract void parseDatas(BaseProtocol baseProtocol);

}

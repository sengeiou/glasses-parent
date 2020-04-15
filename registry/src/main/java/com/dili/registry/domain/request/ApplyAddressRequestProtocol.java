package com.dili.registry.domain.request;

import com.dili.registry.domain.AbstractProtocol;
import lombok.Getter;
import lombok.Setter;

/**
 * 申请Server地址传输协议
 *
 * @author wangmi
 */
@Getter
@Setter
public class ApplyAddressRequestProtocol extends AbstractProtocol {

    @Override
    public void parseDatas() {
        if (getDatas().length > 0) {
            //TODO
        }
    }
}

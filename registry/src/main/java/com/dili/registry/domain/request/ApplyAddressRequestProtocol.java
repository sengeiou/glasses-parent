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
public class ApplyAddressRequestProtocol extends BaseRequestProtocol {

    /**
     * 流水号
     */
    private String serialNumber;

    @Override
    public void parseDatas(BaseProtocol baseProtocol) {
        String[] datas = baseProtocol.getDatas();
        setLength(datas[0]);
        setSerialNumber(datas[1]);
    }

}

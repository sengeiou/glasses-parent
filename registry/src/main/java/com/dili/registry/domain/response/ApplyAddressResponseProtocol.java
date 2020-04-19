package com.dili.registry.domain.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 申请Server地址传输协议
 *
 * @author wangmi
 */
@Getter
@Setter
public class ApplyAddressResponseProtocol extends BaseResponseProtocol {

    /**
     * 端口或域名
     */
    private String host;

    /**
     * 端口
     */
    private String port;

    @Override
    public String encodeDatas() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(getType()).append(",")
                .append(getImei()).append(",")
                .append(getLength()).append(",")
                .append(getHost()).append(",")
                .append(getPort()).append(",")
                .append(getCrc()).append("]");
        return sb.toString();
    }
}

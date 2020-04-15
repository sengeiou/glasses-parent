package com.dili.registry.domain;

import lombok.Data;

/**
 * 眼镜传输协议
 *
 * @author wangmi
 */
@Data
public class GlassesProtocol extends AbstractProtocol {

    /**
     * 数据区, 类型不同数据区不同
     */
    private String data;

}

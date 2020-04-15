package com.dili.registry.domain;

/**
 * 交互类协议，有记录号
 */
public class InteractiveAbstractProtocol extends AbstractProtocol {
    /**
     * 记录号4字节 高1字节是随机码无意义(保留加密用),低3字节是记录序号
     */
    private Integer recordNumber;
}

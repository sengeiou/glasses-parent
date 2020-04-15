package com.dili.registry.domain.request;

import com.dili.registry.domain.AbstractProtocol;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ren HongWei
 * @date 2019-09-02 15:33
 * @description
 **/
@Getter
@Setter
public abstract class AbstractRequestProtocol extends AbstractProtocol {

    /**
     * 解析信息域
     *
     * @param information 信息域内容
     */
    public abstract void parseInformation(Byte[] information);
}

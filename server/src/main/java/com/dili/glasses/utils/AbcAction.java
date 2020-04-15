package com.dili.glasses.utils;

import java.util.Collection;

/**
 * @author Ren HongWei
 * @date 2019-08-02 16:39
 * @description
 **/
public interface AbcAction<P> {

    /**
     * 执行函数
     *
     * @param parameters 参数列表
     */
    void call(Collection<P> parameters);

}

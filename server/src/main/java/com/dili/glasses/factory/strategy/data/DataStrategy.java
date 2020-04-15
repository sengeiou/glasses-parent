package com.dili.glasses.factory.strategy.data;

/**
 * @author Ren HongWei
 * @date 2019-09-14 14:49
 * @description
 **/
public interface DataStrategy<T> {

    /**
     * 将字节数组解析为对应的实体
     *
     * @param data 字节数组
     * @return 实体
     */
    T parseData(byte[] data);
}

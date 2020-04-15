package com.dili.glasses.service;

import com.dili.glasses.domain.entity.SystemConfig;

import java.util.List;
import java.util.Set;

/**
 * @author Ren HongWei
 * @date 2019-09-19 14:04
 * @description
 **/
public interface SystemConfigService {

    /**
     * 获取所有系统配置
     *
     * @return 系统配置
     */
    List<SystemConfig> getAllSystemConfig();

    /**
     * 获取所有系统配置的类型
     *
     * @return 系统配置的类型
     */
    Set<Byte> getAllSystemConfigByte();

    /**
     * 刷新缓存
     */
    void refreshCache();
}

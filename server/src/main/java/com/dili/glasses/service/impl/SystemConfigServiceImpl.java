package com.dili.glasses.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.glasses.consts.NettyConstant;
import com.dili.glasses.dao.SystemConfigMapper;
import com.dili.glasses.domain.entity.SystemConfig;
import com.dili.glasses.service.SystemConfigService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.redis.service.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ren HongWei
 * @date 2019-09-19 14:04
 * @description 系统配置服务
 **/
@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Resource
    private SystemConfigMapper systemConfigMapper;

    private final RedisUtil redisUtil;

    @Autowired
    public SystemConfigServiceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    @Override
    public List<SystemConfig> getAllSystemConfig() {
        Object json = redisUtil.get(NettyConstant.SYSTEM_CONFIG_KEY);
        if (json == null) {
            return cacheSystemConfig();
        }
        return JSON.parseArray(json.toString(), SystemConfig.class);
    }

    @Override
    public Set<Byte> getAllSystemConfigByte() {
        List<SystemConfig> allSystemConfig = getAllSystemConfig();
        return allSystemConfig.stream().map(SystemConfig::getType).collect(Collectors.toSet());
    }

    @Override
    public void refreshCache() {
        redisUtil.remove(NettyConstant.SYSTEM_CONFIG_KEY);
        cacheSystemConfig();
    }

    /**
     * 获取并缓存系统配置
     *
     * @return 系统配置
     */
    private List<SystemConfig> cacheSystemConfig() {
        SystemConfig systemConfig = DTOUtils.newInstance(SystemConfig.class);
        systemConfig.setDeleteFlag((byte) 0);
        List<SystemConfig> systemConfigList = systemConfigMapper.select(systemConfig);
        redisUtil.set(NettyConstant.SYSTEM_CONFIG_KEY, JSON.toJSONString(systemConfigList));
        return systemConfigList;
    }
}

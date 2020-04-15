package com.dili.glasses.service;

import com.dili.glasses.domain.entity.TerminalConfig;

import java.util.List;
import java.util.Set;

/**
 * @author Ren HongWei
 * @date 2019-09-19 16:09
 * @description
 **/
public interface TerminalConfigService {

    /**
     * 获取终端配置
     *
     * @param terminalId 终端id
     * @return 终端配置列表
     */
    List<TerminalConfig> findAllTerminalConfig(int terminalId);

    /**
     * 根据终端id集合 和 系统配置id集合获取
     *
     * @param terminalIdSet     终端id集合
     * @param systemConfigIdSet 系统配置id集合
     * @return 系统配置信息
     */
    List<TerminalConfig> findAllByTerminalIdAndSystemConfigId(Set<Integer> terminalIdSet, Set<Long> systemConfigIdSet);

    /**
     * 根据终端id集合 获取配置
     *
     * @param terminalIdSet 终端id集合
     * @return 终端配置信息
     */
    List<TerminalConfig> findAllByteTerminalId(Set<Integer> terminalIdSet);

    /**
     * 批量插入终端配置
     *
     * @param terminalConfigList 终端配置列表
     */
    void batchInsert(List<TerminalConfig> terminalConfigList);
}

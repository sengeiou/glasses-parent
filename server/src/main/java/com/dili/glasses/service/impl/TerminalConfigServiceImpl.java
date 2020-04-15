package com.dili.glasses.service.impl;

import com.dili.glasses.dao.TerminalConfigMapper;
import com.dili.glasses.domain.entity.TerminalConfig;
import com.dili.glasses.service.TerminalConfigService;
import com.dili.ss.dto.DTOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.weekend.WeekendSqls;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @author Ren HongWei
 * @date 2019-09-19 16:09
 * @description 终端配置
 **/
@Service
@Slf4j
public class TerminalConfigServiceImpl implements TerminalConfigService {

    @Resource
    private TerminalConfigMapper terminalConfigMapper;

    @Override
    public List<TerminalConfig> findAllTerminalConfig(int terminalId) {
        TerminalConfig terminalConfig = DTOUtils.newInstance(TerminalConfig.class);
        terminalConfig.setTerminalId(terminalId);
        terminalConfig.setDeleteFlag((byte) 0);
        return terminalConfigMapper.select(terminalConfig);
    }

    @Override
    public List<TerminalConfig> findAllByTerminalIdAndSystemConfigId(Set<Integer> terminalIdSet, Set<Long> systemConfigIdSet) {
        WeekendSqls<TerminalConfig> sql = WeekendSqls.custom();
        sql.andEqualTo(TerminalConfig::getDeleteFlag, 0);
        if (CollectionUtils.isNotEmpty(terminalIdSet)) {
            sql.andIn(TerminalConfig::getTerminalId, terminalIdSet);
        }
        if (CollectionUtils.isNotEmpty(systemConfigIdSet)) {
            sql.andIn(TerminalConfig::getSystemConfigId, systemConfigIdSet);
        }

        return terminalConfigMapper.selectByExample(new Example.Builder(TerminalConfig.class).where(sql).build());
    }

    @Override
    public List<TerminalConfig> findAllByteTerminalId(Set<Integer> terminalIdSet) {
        WeekendSqls<TerminalConfig> sql = WeekendSqls.custom();
        if (CollectionUtils.isNotEmpty(terminalIdSet)) {
            sql.andIn(TerminalConfig::getTerminalId, terminalIdSet);
        }
        sql.andEqualTo(TerminalConfig::getDeleteFlag, 0);
        return terminalConfigMapper.selectByExample(new Example.Builder(TerminalConfig.class).where(sql).build());
    }

    @Override
    public void batchInsert(List<TerminalConfig> terminalConfigList) {
        if (CollectionUtils.isEmpty(terminalConfigList)) {
            return;
        }

        terminalConfigMapper.insertList(terminalConfigList);
    }
}

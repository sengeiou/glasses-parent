package com.dili.glasses.service.impl;

import com.dili.glasses.dao.TerminalMapper;
import com.dili.glasses.domain.entity.Terminal;
import com.dili.glasses.service.TerminalService;
import com.dili.ss.dto.DTOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Ren HongWei
 * @date 2019-09-19 17:20
 * @description 设备服务
 **/
@Service
@Slf4j
public class TerminalServiceImpl implements TerminalService {


    @Resource
    private TerminalMapper terminalMapper;

    @Override
    public Terminal findOne(int terminalId) {
        Terminal terminal = DTOUtils.newInstance(Terminal.class);
        terminal.setTerminalId(terminalId);
        terminal.setDeleteFlag((byte) 0);
        return terminalMapper.selectOne(terminal);
    }

    @Override
    public void createTerminal(String longNum, int terminalId, String batch) {
        Terminal terminal = DTOUtils.newInstance(Terminal.class);
        terminal.setTerminalId(terminalId);
        terminal.setDeleteFlag((byte) 0);
        terminal.setLongNum(longNum);
        terminal.setBatch(batch);
        terminal.setCreateTime(new Date());
        terminal.setModifyTime(new Date());
        terminalMapper.insert(terminal);
    }

    @Override
    public void updateTerminalVersion(int terminalId, int hardwareVersion, int softwareVersion) {
        Terminal terminal = DTOUtils.newInstance(Terminal.class);
        terminal.setTerminalId(terminalId);
        terminal.setDeleteFlag((byte) 0);
        Terminal result = terminalMapper.selectOne(terminal);
        if (result != null) {
            result.setHardwareVersion(hardwareVersion);
            result.setSoftwareVersion(softwareVersion);
            terminalMapper.updateByPrimaryKey(result);
        }
    }

    @Override
    public void updateOnlineStatus(int terminalId, boolean online) {
        Terminal terminal = DTOUtils.newInstance(Terminal.class);
        terminal.setTerminalId(terminalId);
        Terminal result = terminalMapper.selectOne(terminal);
        if (result != null) {
            byte status = online ? (byte) 1 : 0;
            if (online) {
                result.setLastLoginTime(new Date());
            } else {
                result.setLastOfflineTime(new Date());
            }
            result.setOnline(status);

            if (result.getActive() == null || result.getActive() == 0) {
                result.setActive((byte) 1);
                result.setActiveTime(new Date());
            }

            terminalMapper.updateByPrimaryKey(result);
        }
    }
}

package com.dili.glasses.service;

import com.dili.glasses.domain.entity.Terminal;

/**
 * @author Ren HongWei
 * @date 2019-09-19 17:20
 * @description 设备信息
 **/
public interface TerminalService {

    /**
     * 根据终端id获取数据
     *
     * @param terminalId 终端id
     * @return 终端信息
     */
    Terminal findOne(int terminalId);

    /**
     * 创建设备信息
     *
     * @param longNum    设备长编号
     * @param terminalId 设备端编号
     * @param batch      批次编号
     */
    void createTerminal(String longNum, int terminalId, String batch);

    /**
     * 更新终端版本号
     *
     * @param terminalId      终端id
     * @param hardwareVersion 硬件版本号
     * @param softwareVersion 软件版本号
     */
    void updateTerminalVersion(int terminalId, int hardwareVersion, int softwareVersion);

    /**
     * 更新设备的在线状态
     *
     * @param terminalId 终端id
     * @param online     是否在线
     */
    void updateOnlineStatus(int terminalId, boolean online);
}

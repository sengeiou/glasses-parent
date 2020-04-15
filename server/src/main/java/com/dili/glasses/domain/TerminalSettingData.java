package com.dili.glasses.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author Ren HongWei
 * @date 2019-09-19 15:00
 * @description 设备端配置数据
 **/
@Getter
@Setter
public class TerminalSettingData extends TerminalSettingReportData {

    /**
     * 配置项
     */
    private Map<Byte, Integer> config;
}

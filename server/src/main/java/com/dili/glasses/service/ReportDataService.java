package com.dili.glasses.service;

import com.dili.glasses.domain.es.TerminalReportRecord;

/**
 * @author Ren HongWei
 * @date 2019-09-16 10:51
 * @description
 **/
public interface ReportDataService {

    /**
     * 添加数据
     *
     * @param reportRecord 上报的数据
     */
    void addData(TerminalReportRecord reportRecord);

    /**
     * 批量插入es
     */
    void batchInsertToEs();
}

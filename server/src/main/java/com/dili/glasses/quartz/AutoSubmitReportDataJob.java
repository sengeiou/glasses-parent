package com.dili.glasses.quartz;

import com.dili.glasses.service.ReportDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Ren HongWei
 * @date 2019-09-17 09:28
 * @description 自动提交上报数据
 **/
@Component
@Slf4j
public class AutoSubmitReportDataJob {

    private final ReportDataService reportDataService;

    @Autowired
    public AutoSubmitReportDataJob(ReportDataService reportDataService) {
        this.reportDataService = reportDataService;
    }

    /**
     * 按一定时间间隔，自动提交数据
     * 每隔一分钟执行一次
     */
    @Scheduled(cron = "1 * *  * * ?")
    public void autoSubmit() {
        reportDataService.batchInsertToEs();
    }
}

package com.dili.glasses.service.impl;

import com.dili.glasses.consts.ElasticSearchConstants;
import com.dili.glasses.domain.es.TerminalReportRecord;
import com.dili.glasses.service.ReportDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Ren HongWei
 * @date 2019-09-16 10:51
 * @description 数据上报服务
 **/
@Service
@Slf4j
public class ReportDataServiceImpl implements ReportDataService {

    /**
     * 缓存上报的数据，统一批量插入
     */
    private static final ConcurrentLinkedQueue<TerminalReportRecord> CACHED_RECORD = new ConcurrentLinkedQueue<>();

    /**
     * 批量大小
     */
    private static final Integer BATCH_SIZE = 100;

    private final ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public ReportDataServiceImpl(ElasticsearchTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }


    @Override
    public void addData(TerminalReportRecord reportRecord) {
        reportRecord.setReportTime(new Date());
        reportRecord.setId(UUID.randomUUID().toString().replace("-", ""));
        CACHED_RECORD.offer(reportRecord);
        //超过筏值，进行一次批量数据插入
        if (CACHED_RECORD.size() >= BATCH_SIZE) {
            batchInsertToEs();
        }
    }

    @Override
    public void batchInsertToEs() {
        if (CACHED_RECORD.size() <= 0) {
            return;
        }

        List<IndexQuery> indexQueryList = new ArrayList<>();
        try {
            //批量处理数据
            while (true) {
                TerminalReportRecord record = CACHED_RECORD.poll();
                if (record == null) {
                    break;
                }
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId(record.getId());
                indexQuery.setObject(record);
                indexQuery.setIndexName(ElasticSearchConstants.TERMINAL_REPORT_INDEX);
                indexQuery.setType(ElasticSearchConstants.TERMINAL_REPORT_TYPE);
                indexQueryList.add(indexQuery);
            }
            elasticsearchTemplate.bulkIndex(indexQueryList);
            elasticsearchTemplate.refresh(ElasticSearchConstants.TERMINAL_REPORT_INDEX);
        } catch (Exception e) {
            log.error("设备上报数据批量写入es出错：{}", e);
        }
    }


}

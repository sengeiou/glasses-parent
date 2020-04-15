package com.dili.glasses.repository;

import com.dili.glasses.domain.es.TerminalReportRecord;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * @author Ren HongWei
 * @date 2019-09-16 10:40
 * @description
 **/
public interface TerminalReportRecordRepository extends ElasticsearchCrudRepository<TerminalReportRecord, String> {
}

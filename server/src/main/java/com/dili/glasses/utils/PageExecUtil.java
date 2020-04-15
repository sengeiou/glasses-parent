package com.dili.glasses.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Ren HongWei
 * @date 2019-08-02 15:30
 * @description 分页执行工具
 **/
public class PageExecUtil {

    public static <T> void pageExec(int pageSize, Collection<T> collection, AbcAction<T> func) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }

        if (pageSize <= 0) {
            return;
        }

        List<T> list = new ArrayList<>(collection);

        //计算需要分页大小
        int total = collection.size();
        int slice = total / pageSize;
        if (total % pageSize > 0) {
            slice++;
        }
        for (int i = 0; i < slice; i++) {
            int min = i * pageSize;
            int max = min + pageSize;

            //处理最后一段数据
            if (max > total) {
                max = total;
            }

            List<T> needToDealList = list.subList((i * pageSize), max);

            func.call(needToDealList);
        }
    }
}

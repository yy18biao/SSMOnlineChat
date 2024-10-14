package com.chat.core.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.chat.core.domain.TableData;


import java.util.List;

public class BaseController {
    // 将查询出来的分页数据需要的数据整体打包返回
    public TableData getTableData(List<?> list) {
        if (CollectionUtil.isEmpty(list)) {
            return TableData.empty();
        }

        //获取符合查询条件的数据的总数
        return TableData.success(list, new PageInfo<>(list).getTotal());
    }
}

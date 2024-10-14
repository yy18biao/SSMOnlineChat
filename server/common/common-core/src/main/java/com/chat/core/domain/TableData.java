package com.chat.core.domain;

import com.chat.core.enums.ResCode;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

// 表格数据对象
@Data
public class TableData {
    // 表格中分页的数据的总量
    private long total;

    // 数据列表
    private List<?> rows;

    // 状态码
    private int code;

    // 响应内容
    private String msg;

    // 当查询结果为空时调用该方法得到表格数据对象
    public static TableData empty() {
        TableData resp = new TableData();
        resp.setCode(ResCode.SUCCESS.getCode());
        resp.setRows(new ArrayList<>());
        resp.setMsg(ResCode.SUCCESS.getMsg());
        resp.setTotal(0);
        return resp;
    }

    // 当查询结果不为空时调用该方法得到表格数据对象
    public static TableData success(List<?> list, long total) {
        TableData resp = new TableData();
        resp.setCode(ResCode.SUCCESS.getCode());
        resp.setRows(list);
        resp.setMsg(ResCode.SUCCESS.getMsg());
        resp.setTotal(total);
        return resp;
    }
}

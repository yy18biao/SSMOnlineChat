package com.chat.core.domain;

import lombok.Data;

@Data
public class PageQueryDto {
    private Integer pageSize = 10;  // 每页的数据

    private Integer pageNum = 1; // 第几页
}

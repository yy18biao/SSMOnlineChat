create table chatSessionUser
(
    chatSessionId bigint unsigned NOT NULL COMMENT '聊天会话id',
    userId        bigint unsigned NOT NULL comment '用户id',
    createBy      bigint unsigned not null comment '创建人',
    createTime    datetime        not null comment '创建时间',
    updateBy      bigint unsigned comment '更新人',
    updateTime    datetime comment '更新时间'
)
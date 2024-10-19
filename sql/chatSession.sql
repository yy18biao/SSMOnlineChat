create table chatSession
(
    chatSessionId    bigint unsigned NOT NULL COMMENT '聊天会话id(主键)',
    chatSessionName  varchar(20) comment '聊天会话昵称',
    chatSessionPhoto varchar(100) comment '聊天会话头像',
    chatSessionType  tinyint         not null default 1 comment '1 单聊 2 群聊',
    chatSessionLastMessage  varchar(1000)  comment '会话最新一条消息',
    createBy         bigint unsigned     not null comment '创建人',
    createTime       datetime        not null comment '创建时间',
    updateBy         bigint unsigned comment '更新人',
    updateTime       datetime comment '更新时间',
    primary key (`chatSessionId`)
)
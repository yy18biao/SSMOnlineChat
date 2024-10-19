create table message
(
    messageId       bigint unsigned NOT NULL COMMENT '消息id(主键)',
    chatSessionId   bigint unsigned NOT NULL COMMENT '聊天会话id',
    messageNickname varchar(20) comment '消息发送者的昵称',
    messagePhoto    varchar(100) comment '消息发送者的头像',
    messageType     tinyint         not null default 1 comment '1 文本 2 文件',
    content         text comment '消息正文',
    createBy        bigint unsigned not null comment '创建人',
    createTime      datetime        not null comment '创建时间',
    primary key (`messageId`)
)
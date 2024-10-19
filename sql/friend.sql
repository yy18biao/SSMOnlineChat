create table friend
(
    friendId    bigint unsigned NOT NULL COMMENT '朋友id',
    userId  bigint unsigned comment '用户id',
    friendName    varchar(20)  COMMENT '朋友备注',
    friendPhoto    varchar(100)  COMMENT '朋友头像',
    createBy         bigint unsigned     not null comment '创建人',
    createTime       datetime        not null comment '创建时间',
    updateBy         bigint unsigned comment '更新人',
    updateTime       datetime comment '更新时间'
)
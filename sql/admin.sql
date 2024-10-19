create table admin
(
    id         bigint unsigned not null comment '用户id（主键）',
    userId     varchar(20) not null comment '账号',
    nickname   varchar(20) comment '昵称',
    password   char(60)    not null comment '密码',
    createBy   bigint unsigned not null  comment '创建人',
    createTime datetime    not null comment '创建时间',
    updateBy   bigint unsigned  comment '更新人',
    updateTime datetime comment '更新时间',
    primary key (`id`),
    unique key `userId` (`userId`)
);
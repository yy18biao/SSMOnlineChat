create table user
(
    userId     bigint unsigned NOT NULL COMMENT '用户账号(主键)',
    password   char(60)        not null comment '密码',
    nickname   varchar(20) comment '用户昵称',
    photo      varchar(100) comment '用户头像',
    phone      char(11)        not null comment '手机号',
    code       char(6) comment '验证码',
    email      varchar(20) comment '邮箱',
    introduce  varchar(1000) comment '个人介绍',
    status     tinyint         not null default 1 comment '0 申请冻结 1 正常 2 冻结',
    createBy   bigint unsigned comment '创建人',
    createTime datetime comment '创建时间',
    updateBy   bigint unsigned comment '更新人',
    updateTime datetime comment '更新时间',
    primary key (`userId`),
    unique key `phone` (`phone`),
    unique key `email` (`email`)
)
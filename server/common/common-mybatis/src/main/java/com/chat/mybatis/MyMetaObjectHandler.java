package com.chat.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.redis.service.RedisService;
import jakarta.annotation.Resource;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class,
                ThreadLocalUtil.get("curUserId",  Long.class));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class,
                ThreadLocalUtil.get("curUserId",  Long.class));
    }
}

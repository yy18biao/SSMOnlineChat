package com.chat.admin.domain.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import lombok.Data;

@Data
@TableName("admin")
public class Admin extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String userId;
    private String password;
    private String nickname;
}

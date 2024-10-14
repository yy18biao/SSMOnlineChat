package com.chat.admin.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chat.core.domain.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("user")
public class User extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "userID", type = IdType.ASSIGN_ID)
    private Long userId;

    private String nickName;

    private String photo;

    private String phone;

    private String email;

    private String introduce;

    private Integer status;
}

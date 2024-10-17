package com.chat.user.domain.dto;

import com.chat.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserUpdateDto extends BaseEntity {
    private String nickname;
    private String photo;
    private String email;
    private String introduce;
    private String phone;
    private String code;
}

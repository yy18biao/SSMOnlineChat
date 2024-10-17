package com.chat.user.domain.dto;

import com.chat.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PasswordUpdateDto extends BaseEntity {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String code;
}

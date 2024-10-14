package com.chat.admin.domain.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminDto {
    @NotNull(message = "账号不能为null")
    private String userId;
    @NotNull(message = "密码不能为null")
    private String password;
}

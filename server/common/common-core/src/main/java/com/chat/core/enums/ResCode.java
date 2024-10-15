package com.chat.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResCode {
    SUCCESS(200, "操作成功"),
    FAILED(300, "操作失败"),
    ERROR(2000, "服务器繁忙"),
    USER_EXISTS(2100, "用户已存在"),
    USER_NOT_EXISTS(2101, "用户不存在"),
    FAILED_LOGIN(2102, "账号/密码错误"),

    FAILED_UNAUTHORIZED(3000, "未授权"),
    FAILED_PARAMS_VALIDATE(3001, "参数校验失败"),

    FAILED_PHONE(3100, "手机号码格式错误"),
    PHONE_EXISTS(3101, "手机号已被注册"),
    PASSWORD_NO_PASSWORD(3102, "两次密码不一致"),
    FAILED_CODE(3103, "验证码错误");

    private int code;
    private String msg;
}
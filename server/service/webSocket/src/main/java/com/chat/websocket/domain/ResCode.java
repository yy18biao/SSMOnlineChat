package com.chat.websocket.domain;

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
    FAILED_CODE(3103, "验证码错误"),

    FRIEND_YES(3200, "该用户已经是您的好友"),
    FRIEND_NO_ME(3201, "不可添加自己"),
    APPLYED(3202, "已向对方发起申请，请等待对方处理"),
    FRIEND_APPLYED(3303, "对方已向您发起申请，请处理"),

    OLD_PASSWORD_NO_PASSWORD(3400, "当前密码错误"),

    FRIEND_WEBSOCKETREQ(3500, "反序列化消息请求失败"),
    FRIEND_MESSAGE_MYSQL(3501, "消息存储数据库失败"),
    FRIEND_UPDATE_CHATSESSION(3502, "修改会话失败"),
    FRIEND_SEND_MESSAGE(3504, "转发消息失败");

    private int code;
    private String msg;
}

package com.chat.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResCode {
    SUCCESS(200, "操作成功"),
    FAILED(300, "操作失败"),
    ERROR(500, "服务器繁忙"),

    USER_EXISTS(2100, "用户已存在"),
    USER_NOT_EXISTS(2101, "用户不存在"),
    FAILED_LOGIN(2102, "账号/密码错误"),
    FREEZE_APPLY(2103, "当前用户申请冻结中，请联系管理员"),
    CANCEL_APPLY(2104, "当前用户申请注销中，请联系管理员"),
    FREEZE_NOW(2105, "当前用户已冻结，请联系管理员"),

    FAILED_UNAUTHORIZED(3000, "未授权"),
    FAILED_PARAMS_VALIDATE(3001, "参数校验失败"),

    FAILED_PHONE(3100, "手机号码格式错误"),
    PHONE_EXISTS(3101, "手机号已被注册"),
    FAILED_CODE(3103, "验证码错误"),

    FRIEND_YES(3200, "该用户已经是您的好友"),
    FRIEND_NO_ME(3201, "不可添加自己"),
    APPLYED(3202, "已向对方发起申请，请等待对方处理"),
    FRIEND_APPLYED(3303, "对方已向您发起申请，请处理"),

    OLD_PASSWORD_NO_PASSWORD(3400, "当前密码错误"),

    FRIEND_WEBSOCKETREQ(3500, "反序列化websocket消息请求失败"),
    FRIEND_MESSAGE_MYSQL(3501, "消息存储数据库失败"),
    FRIEND_UPDATE_CHATSESSION(3502, "修改会话失败"),
    FRIEND_SEND_MESSAGE(3504, "转发消息失败"),

    FAILED_RABBIT_PRODUCE(3600, "rabbit发布消息失败"),

    FAILED_FILE_UPLOAD(3700, "上传文件失败");

    private int code;
    private String msg;
}

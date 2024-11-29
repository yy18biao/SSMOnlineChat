package com.chat.core.entity;

import com.chat.core.enums.ResCode;
import lombok.Data;

@Data
public class Resp<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Resp<T> ok() {
        return assembleResult(null, ResCode.SUCCESS);
    }

    public static <T> Resp<T> ok(T data) {
        return assembleResult(data, ResCode.SUCCESS);
    }

    public static <T> Resp<T> fail() {
        return assembleResult(null, ResCode.FAILED);
    }

    public static <T> Resp<T> fail(int code, String msg) {
        return assembleResult(code, msg, null);
    }

    public static <T> Resp<T> fail(ResCode resCode) {
        return assembleResult(null, resCode);
    }

    private static <T> Resp<T> assembleResult(T data, ResCode resCode) {
        Resp<T> r = new Resp<>();
        r.setCode(resCode.getCode());
        r.setData(data);
        r.setMsg(resCode.getMsg());
        return r;
    }

    private static <T> Resp<T> assembleResult(int code, String msg, T data) {
        Resp<T> r = new Resp<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}

package com.chat.security.exception;

import com.chat.core.enums.ResCode;

// 业务处理服务层发现的异常

public class ServiceException extends RuntimeException {
    private ResCode resCode;

    public ServiceException(ResCode resCode) {
        this.resCode = resCode;
    }

    public ResCode getResCode() {
        return resCode;
    }
}

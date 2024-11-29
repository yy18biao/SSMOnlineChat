package com.chat.security.exception;

import com.chat.core.enums.ResCode;
import lombok.Getter;

// 业务处理服务层发现的异常
@Getter
public class ServiceException extends RuntimeException {
    private ResCode resCode;

    public ServiceException(ResCode resCode) {
        this.resCode = resCode;
    }

}

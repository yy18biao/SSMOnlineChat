package com.chat.security.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.core.domain.Resp;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

// 全局异常处理器
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{
    // 请求方法不支持
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Resp<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return Resp.fail(ResCode.ERROR);
    }

    // 运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Resp<?> handleRuntimeException(RuntimeException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生运行时异常", requestURI, e);
        return Resp.fail(ResCode.ERROR);
    }

    // 业务异常
    @ExceptionHandler(ServiceException.class)
    public Resp<?> handleServiceException(ServiceException e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("'{}',发生业务处理异常 : {}", requestURI, e.getResCode().getMsg());
        return Resp.fail(e.getResCode());
    }

    // 参数校验异常
    @ExceptionHandler(BindException.class)
    public Resp<Void> handleBindException(BindException e) {
        log.error(e.getMessage());
        String message = join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage);
        return Resp.fail(ResCode.FAILED_PARAMS_VALIDATE.getCode(), message);
    }
    // 拼接异常
    private <E> String join(Collection<E> collection, Function<E, String> function) {
        if (CollUtil.isEmpty(collection)) {
            return StrUtil.EMPTY;
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.joining(", "));
    }

    // 系统的异常
    @ExceptionHandler(Exception.class)
    public Resp<?> handleException(Exception e, HttpServletRequest request){
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生异常.", requestURI, e);
        return Resp.fail(ResCode.ERROR);
    }
}

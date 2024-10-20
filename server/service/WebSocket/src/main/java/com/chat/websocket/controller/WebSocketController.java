package com.chat.websocket.controller;

import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.enums.ResCode;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.websocket.service.OnlineWebsocketService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/websocket")
public class WebSocketController {
    @Resource
    private TokenService tokenService;

    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    @Value("${jwt.secret}")
    private String secret;

    @DeleteMapping("/deleteWebSocket")
    public Resp<Void> deleteWebSocket(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if (loginUserData == null) {
            throw new ServiceException(ResCode.FAILED_UNAUTHORIZED);
        }
        onlineWebsocketService.offline(loginUserData.getUserId());
        return Resp.ok();
    }
}

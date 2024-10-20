package com.chat.websocket.config;

import com.chat.websocket.api.WebSocketApi;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Resource
    private WebSocketApi webSocketApi;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 绑定websocket请求类和连接uri
        registry.addHandler(webSocketApi, "/onlineChat")
                // 该拦截器将http协议中保存的会话添加到websocket协议中
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}

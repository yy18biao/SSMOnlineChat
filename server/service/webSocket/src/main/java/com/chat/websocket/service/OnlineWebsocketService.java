package com.chat.websocket.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

// 保存关联用户和用户websocket连接的类
@Component
public class OnlineWebsocketService {
    private ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 用户上线
    public void online(Long userId, WebSocketSession session) {
        if (sessions.get(userId) != null) {
            return;
        }
        sessions.put(userId, session);
    }

    // 用户下线, 针对这个哈希表进行删除元素
    public void offline(Long userId, WebSocketSession session) {
        WebSocketSession existSession = sessions.get(userId);
        if (existSession == session) {
            sessions.remove((userId));
        }
    }

    // 根据 userId 获取到 WebSocketSession
    public WebSocketSession getSession(Long userId) {
        return sessions.get(userId);
    }

}

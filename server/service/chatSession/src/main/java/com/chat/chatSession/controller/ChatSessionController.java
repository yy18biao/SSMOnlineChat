package com.chat.chatSession.controller;

import com.chat.chatSession.domain.Dto.UpdateMessageDto;
import com.chat.chatSession.domain.vo.ChatSessionVo;
import com.chat.chatSession.mapper.ChatSessionMapper;
import com.chat.chatSession.service.ChatSessionService;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.Resp;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatSession")
public class ChatSessionController {
    @Resource
    private ChatSessionService chatSessionService;
    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @GetMapping("/getChatSessionList")
    public Resp<List<ChatSessionVo>> getChatSessionList(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(chatSessionService.getChatSessionList(token));
    }

    // 新增一个会话
    @GetMapping("/addChatSession")
    public Resp<String> addChatSession(@RequestParam Long friendId,
                                     @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(chatSessionService.addChatSession(friendId, token));
    }

    // 搜索一个会话的基本信息
    @GetMapping("/searchChatSession")
    public Resp<ChatSessionVo> searchChatSession(@RequestParam String chatSessionId,
                                                 @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(chatSessionService.searchChatSession(Long.parseLong(chatSessionId), token));
    }

    // 获取会话中的所有用户id
    @GetMapping("/selectAllUserId")
    public Resp<List<Long>> selectAllUserId(Long chatSessionId) {
        return Resp.ok(chatSessionService.selectAllUserId(chatSessionId));
    }
}

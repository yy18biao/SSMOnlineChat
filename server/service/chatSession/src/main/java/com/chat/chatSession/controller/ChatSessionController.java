package com.chat.chatSession.controller;

import com.chat.chatSession.domain.vo.ChatSessionVo;
import com.chat.chatSession.service.ChatSessionService;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.Resp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chatSession")
public class ChatSessionController {
    @Resource
    private ChatSessionService chatSessionService;

    @GetMapping("/getChatSessionList")
    public Resp<List<ChatSessionVo>> getChatSessionList(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(chatSessionService.getChatSessionList(token));
    }
}

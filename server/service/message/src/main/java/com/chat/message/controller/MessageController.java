package com.chat.message.controller;

import com.chat.core.domain.Resp;
import com.chat.message.domain.vo.MessageVo;
import com.chat.message.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private MessageService messageService;

    @GetMapping("/all")
    public Resp<List<MessageVo>> getAll(@RequestParam Long chatSessionId) {
        return Resp.ok(messageService.getAll(chatSessionId));
    }
}

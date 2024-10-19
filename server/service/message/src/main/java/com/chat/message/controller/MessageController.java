package com.chat.message.controller;

import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.enums.ResCode;
import com.chat.message.domain.Message;
import com.chat.message.domain.dto.MessageDto;
import com.chat.message.domain.vo.MessageVo;
import com.chat.message.service.MessageService;
import com.chat.message.service.OnlineWebsocketService;
import com.chat.security.exception.ServiceException;
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

    @PostMapping("/addMessage")
    public Resp<Void> addMessage(@RequestBody MessageDto messageDto) {
        return messageService.addMessage(messageDto, messageDto.getToken()) > 0 ? Resp.ok() : Resp.fail();
    }

    @DeleteMapping("/deleteWebSocket")
    public Resp<Void> deleteWebSocket(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return messageService.deleteWebSocket(token);
    }
}

package com.chat.message.rabbit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.message.domain.Message;
import com.chat.message.mapper.ChatSessionUserMapper;
import com.chat.message.mapper.MessageMapper;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ConsumeService {
    @Resource
    private MessageMapper messageMapper;

    @Resource
    private ChatSessionUserMapper chatSessionUserMapper;

    @Resource
    private TokenService tokenService;

    @Resource
    private ProductService productService;

    @Value("${jwt.secret}")
    private String secret;

    private LoginUserData getLoginUserData(String token) {
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if (loginUserData == null) {
            throw new ServiceException(ResCode.FAILED_UNAUTHORIZED);
        }

        return loginUserData;
    }

    @RabbitListener(queues = "message")
    public void consume(RabbitMessageDto rabbitMessageDto) {
        LoginUserData loginUserData = getLoginUserData(rabbitMessageDto.getToken());
        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());

        if (rabbitMessageDto.getDtoType().equals("addTextMessage")) {
            // 新增消息
            Message message = BeanUtil.copyProperties(rabbitMessageDto, Message.class);
            if (messageMapper.insert(message) < 1) {
                throw new ServiceException(ResCode.FRIEND_MESSAGE_MYSQL);
            } else {
                // 新增成功 发布到rabbit让websocket模块转发
                RabbitMessageVo rabbitMessageVo = new RabbitMessageVo();
                rabbitMessageVo.setUserId(loginUserData.getUserId());
                rabbitMessageVo.setRespType(rabbitMessageDto.getDtoType());
                rabbitMessageVo.setChatSessionUserIds(chatSessionUserMapper.selectAllUserId(rabbitMessageDto.getChatSessionId()));
                rabbitMessageVo.setMessageContent(rabbitMessageDto.getContent());
                rabbitMessageVo.setChatSessionId(rabbitMessageDto.getChatSessionId());
                rabbitMessageVo.setMessageNickname(loginUserData.getNickname());
                rabbitMessageVo.setMessagePhoto(loginUserData.getPhoto());
                rabbitMessageVo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
                productService.send(rabbitMessageVo);
            }
        }


    }
}

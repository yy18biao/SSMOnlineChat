package com.chat.websocket.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.dto.RabbitChatSessionDto;
import com.chat.core.domain.dto.RabbitFriendDto;
import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.domain.dto.WebSocketDto;
import com.chat.core.domain.vo.RabbitFriendApplyVo;
import com.chat.core.enums.ResCode;
import com.chat.redis.service.RedisService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.websocket.rabbit.producer.RabbitChatSessionProducer;
import com.chat.websocket.rabbit.producer.RabbitFriendProducer;
import com.chat.websocket.rabbit.producer.RabbitMessageProducer;
import com.chat.websocket.service.OnlineWebsocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// websocket协议交互业务处理类
@Component
public class WebSocketApi extends TextWebSocketHandler {
    @Resource
    private OnlineWebsocketService onlineWebsocketService;

    @Resource
    private RabbitMessageProducer rabbitMessageProducer;

    @Resource
    private RabbitChatSessionProducer rabbitChatSessionProducer;

    @Resource
    private RabbitFriendProducer rabbitFriendProducer;

    @Resource
    private TokenService tokenService;

    @Resource
    private RedisService redisService;


    @Value("${jwt.secret}")
    private String secret;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // 收到消息回调
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // 反序列化收到的消息请求
        WebSocketDto webSocketDto = objectMapper.readValue(message.getPayload(), WebSocketDto.class);
        System.out.println(webSocketDto);

        // 判断消息类型
        if (webSocketDto.getDtoType().equals("userOnline")) {
            // 用户上线新增关联信息

            // 获取当前用户登录信息
            LoginUserData loginUserData = getLoginUserData(webSocketDto.getToken());
            // 用户上线
            onlineWebsocketService.online(loginUserData.getUserId(), session);
        } else if (webSocketDto.getDtoType().equals("addTextMessage")) {
            // 新增消息类型

            // 发布rabbit通知消息模块新增文本消息数据
            RabbitMessageDto rabbitMessageDto = BeanUtil.copyProperties(webSocketDto, RabbitMessageDto.class);
            rabbitMessageDto.setContent(webSocketDto.getMessageContent());
            rabbitMessageProducer.send(rabbitMessageDto);
            // 发布rabbit通知会话模块修改会话最近消息数据
            RabbitChatSessionDto rabbitChatSessionDto = BeanUtil.copyProperties(webSocketDto, RabbitChatSessionDto.class);
            rabbitChatSessionDto.setLastMessageContent(webSocketDto.getMessageContent());
            rabbitChatSessionDto.setDtoType("updateLastMessageContent");
            rabbitChatSessionProducer.send(rabbitChatSessionDto);
        } else if(webSocketDto.getDtoType().equals("addFriendApply")){
            // 新增好友申请

            // 获取当前用户登录信息
            LoginUserData loginUserData = getLoginUserData(webSocketDto.getToken());

            // 判断是否已经存在两个人的申请
            if(redisService.get("addFriendApply" + loginUserData.getUserId().toString(), Long.class) != null &&
                    redisService.get("addFriendApply" + loginUserData.getUserId().toString(), Long.class).equals(webSocketDto.getFriendId())) {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        new RabbitFriendApplyVo("已向该用户发起申请 等待对方处理", "addFriendApplyError"))));
                return;
            } else if(redisService.get("addFriendApply" + webSocketDto.getFriendId().toString(), Long.class) != null &&
                    redisService.get("addFriendApply" + webSocketDto.getFriendId().toString(), Long.class).equals(loginUserData.getUserId())){
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                        new RabbitFriendApplyVo("该用户已向您发起申请 请处理", "addFriendApplyError"))));
                return;
            }
            // 添加关联
            redisService.set("addFriendApply" + loginUserData.getUserId().toString(), webSocketDto.getFriendId());

            // 发布rabbit通知好友模块新增好友申请
            RabbitFriendDto friendDto = BeanUtil.copyProperties(webSocketDto, RabbitFriendDto.class);
            friendDto.setFriendName(loginUserData.getNickname());
            friendDto.setFriendId(webSocketDto.getFriendId());
            friendDto.setFriendPhoto(loginUserData.getPhoto());
            rabbitFriendProducer.send(friendDto);
        }
    }

    // 连接建立回调
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    // 连接断开回调
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

    }

    // 连接异常回调
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

    }
}

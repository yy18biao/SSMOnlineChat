package com.chat.user.rabbit;

import cn.hutool.core.util.StrUtil;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.dto.RabbitUserDto;
import com.chat.core.domain.vo.RabbitUserVo;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.user.domain.User;
import com.chat.user.mapper.UserMapper;
import com.chat.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumeService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private TokenService tokenService;

    @Resource
    private ProducerService productService;

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

    @RabbitListener(queues = "user")
    public void consume(RabbitUserDto rabbitUserDto) {
        LoginUserData loginUserData = getLoginUserData(rabbitUserDto.getToken());
        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());

        if (rabbitUserDto.getDtoType().equals("updateHead")) {
            User user = userMapper.selectById(loginUserData.getUserId());
            user.setPhoto("https://biao22.oss-cn-guangzhou.aliyuncs.com/" + rabbitUserDto.getPhoto());
        if (userMapper.updateById(user) > 0) {
                // 发布消息让websocket模块通知用户
                RabbitUserVo rabbitUserVo = new RabbitUserVo();
                rabbitUserVo.setUserId(loginUserData.getUserId());
                rabbitUserVo.setRespType(rabbitUserDto.getDtoType());
                rabbitUserVo.setPhoto("https://biao22.oss-cn-guangzhou.aliyuncs.com/" + rabbitUserDto.getPhoto());
                productService.send(rabbitUserVo);
            }
        }
    }
}

package com.chat.friend.rabbit;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.dto.RabbitFriendDto;
import com.chat.core.domain.dto.RabbitMessageDto;
import com.chat.core.domain.vo.RabbitFriendApplyVo;
import com.chat.core.domain.vo.RabbitMessageVo;
import com.chat.core.enums.ResCode;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.friend.domain.Friend;
import com.chat.friend.domain.FriendApply;
import com.chat.friend.mapper.FriendApplyMapper;
import com.chat.friend.mapper.FriendMapper;
import com.chat.redis.service.RedisService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class ConsumeService {
    @Resource
    private FriendMapper friendMapper;

    @Resource
    private FriendApplyMapper friendApplyMapper;

    @Resource
    private TokenService tokenService;

    @Resource
    private ProductService productService;

    @Value("${jwt.secret}")
    private String secret;
    @Autowired
    private RedisService redisService;


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

    // TODO 好友同意/拒绝
    @RabbitListener(queues = "friend")
    public void consume(RabbitFriendDto rabbitFriendDto) {
        LoginUserData loginUserData = getLoginUserData(rabbitFriendDto.getToken());
        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());

        if (rabbitFriendDto.getDtoType().equals("addFriendApply")) {
            RabbitFriendApplyVo rabbitFriendApplyVo = new RabbitFriendApplyVo();
            rabbitFriendApplyVo.setFriendId(rabbitFriendDto.getFriendId());
            if (friendMapper.selectCount(new LambdaQueryWrapper<Friend>()
                    .eq(Friend::getFriendId, rabbitFriendDto.getFriendId())
                    .eq(Friend::getUserId, loginUserData.getUserId())) != 0) {
                // 对方已是好友
                rabbitFriendApplyVo.setMsg(ResCode.FRIEND_YES.getMsg());
                rabbitFriendApplyVo.setRespType("addFriendApplyError");
            } else {
                FriendApply friendApply = new FriendApply();
                friendApply.setUserId(loginUserData.getUserId());
                friendApply.setFriendId(rabbitFriendDto.getFriendId());
                friendApply.setPhoto(rabbitFriendDto.getFriendPhoto());
                friendApply.setNickname(rabbitFriendDto.getFriendName());

                if (friendApplyMapper.insert(friendApply) < 1) {
                    rabbitFriendApplyVo.setMsg(ResCode.ERROR.getMsg());
                    rabbitFriendApplyVo.setRespType("addFriendApplyError");
                } else {
                    // 插入成功
                    rabbitFriendApplyVo.setMsg(rabbitFriendDto.getFriendName() + " 向您发起了好友申请");
                    rabbitFriendApplyVo.setRespType("addFriendApply");
                }
            }

            productService.send(rabbitFriendApplyVo);
        }
    }
}

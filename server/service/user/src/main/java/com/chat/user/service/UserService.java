package com.chat.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.constants.RedisConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.domain.vo.LoginUserVO;
import com.chat.core.enums.ResCode;
import com.chat.core.enums.UserIdentity;
import com.chat.core.utils.BCryptUtils;
import com.chat.redis.service.RedisService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.sms.SMSService;
import com.chat.user.domain.User;
import com.chat.user.domain.dto.UserAddDto;
import com.chat.user.domain.dto.UserDto;
import com.chat.user.domain.vo.UserVo;
import com.chat.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private SMSService smsService;

    @Resource
    private RedisService redisService;

    @Resource
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    // 判断手机合理性
    public static boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    // 发送注册验证码
    public boolean sendRegCode(UserDto userDto) {
        if (!checkPhone(userDto.getPhone())) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        String code = RandomUtil.randomNumbers(6);
        String phone = userDto.getPhone();

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.REG_PHONE_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(userDto.getPhone(), code);
    }

    // 发送登录验证码
    public boolean sendLoginCode(UserDto userDto) {
        if (!checkPhone(userDto.getPhone())) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        String code = RandomUtil.randomNumbers(6);
        String phone = userDto.getPhone();

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.LOGIN_PHONE_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(userDto.getPhone(), code);
    }

    // 注册
    public int reg(UserAddDto userAddDto) {
        // 判断手机号码是否合理
        if (!checkPhone(userAddDto.getPhone())) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        // 判断用户是否已经存在
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, userAddDto.getPhone())) > 0) {
            throw new ServiceException(ResCode.PHONE_EXISTS);
        }

        // 判断验证码是否正确
        String code = redisService.get(RedisConstants.REG_PHONE_CODE_KEY + userAddDto.getPhone(), String.class);
        if(!code.equals(userAddDto.getCode())){
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        User user = BeanUtil.copyProperties(userAddDto, User.class);
        user.setPassword(BCryptUtils.encrypt(user.getPassword()));

        redisService.delete(RedisConstants.REG_PHONE_CODE_KEY + userAddDto.getPhone());
        return userMapper.insert(user);
    }

    // 密码登录
    public String passLogin(String phone, String password) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if(user == null){
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        if(!BCryptUtils.matches(password, user.getPassword())){
            throw new ServiceException(ResCode.FAILED_LOGIN);
        }

        return tokenService.createToken(user.getUserId(), secret,
                                        UserIdentity.ORDINARY.getValue(), user.getNickname(),
                                        user.getPhoto());
    }

    // 验证码登录
    public String codeLogin(String phone, String code) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if(user == null){
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        if(!code.equals(redisService.get(RedisConstants.LOGIN_PHONE_CODE_KEY + phone, String.class))){
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        redisService.delete(RedisConstants.LOGIN_PHONE_CODE_KEY + phone);

        return tokenService.createToken(user.getUserId(),
                                        secret, UserIdentity.ORDINARY.getValue(),
                                        user.getNickname(), user.getPhoto());
    }

    public boolean logout(String token){
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        return tokenService.deleteLoginUser(token, secret);
    }

    public Resp<UserVo> getUser(String token){
        // 判断token合理性和去除token前缀
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        // 获取用户信息
        LoginUserData loginUserData = tokenService.getLoginUser(token, secret);
        if(loginUserData == null){
            return Resp.fail();
        }

        User user = userMapper.selectById(loginUserData.getUserId());
        if(user == null){
            return null;
        }
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);

        return Resp.ok(userVo);
    }
}

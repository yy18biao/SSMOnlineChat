package com.chat.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.constants.RedisConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.enums.ResCode;
import com.chat.core.enums.UserIdentity;
import com.chat.core.utils.BCryptUtils;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.redis.service.RedisService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.sms.SMSService;
import com.chat.user.domain.User;
import com.chat.user.domain.UserEs;
import com.chat.user.domain.dto.PasswordUpdateDto;
import com.chat.user.domain.dto.UserAddDto;
import com.chat.user.domain.dto.UserDto;
import com.chat.user.domain.dto.UserUpdateDto;
import com.chat.user.domain.vo.UserVo;
import com.chat.user.es.UserRepository;
import com.chat.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private UserRepository userRepository;

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

    // 发送注册验证码
    public boolean sendRegCode(String phone) {
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        String code = RandomUtil.randomNumbers(6);

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.REG_PHONE_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(phone, code);
    }

    // 发送登录验证码
    public boolean sendLoginCode(String phone) {
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        String code = RandomUtil.randomNumbers(6);

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.LOGIN_PHONE_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(phone, code);
    }

    // 发送修改手机号验证码
    public boolean sendUpdatePhoneCode(String phone) {
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        // 判断用户是否已经存在
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getPhone, phone)) > 0) {
            throw new ServiceException(ResCode.PHONE_EXISTS);
        }

        String code = RandomUtil.randomNumbers(6);

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.UPDATE_PHONE_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(phone, code);
    }

    // 发送修改密码验证码
    public boolean sendUpdatePasswordCode(String phone) {
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }
        String code = RandomUtil.randomNumbers(6);

        // 存储到redis中 有效时间5分钟
        redisService.set(RedisConstants.UPDATE_PASSWORD_CODE_KEY + phone, code, 5L, TimeUnit.MINUTES);

        return smsService.sendPhoneCode(phone, code);
    }

    // 注册
    public int reg(UserAddDto userAddDto) {
        // 判断手机号码是否合理
        if (!checkPhone(userAddDto.getPhone())) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        // 判断用户是否已经存在
        if (userRepository.findUserEsByPhone(userAddDto.getPhone()) != null) {
            throw new ServiceException(ResCode.PHONE_EXISTS);
        }

        // 判断验证码是否正确
        String code = redisService.get(RedisConstants.REG_PHONE_CODE_KEY + userAddDto.getPhone(), String.class);
        if (!code.equals(userAddDto.getCode())) {
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        User user = BeanUtil.copyProperties(userAddDto, User.class);
        user.setPassword(BCryptUtils.encrypt(user.getPassword()));
        user.setNickname(user.getPhone());
        if (userMapper.insert(user) < 1) return -1;


        // 添加到es
        user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userAddDto.getPhone()));
        UserEs userEs = BeanUtil.copyProperties(user, UserEs.class);
        userRepository.save(userEs);

        redisService.delete(RedisConstants.REG_PHONE_CODE_KEY + userAddDto.getPhone());
        return 1;
    }

    // 密码登录
    public List<String> passLogin(String phone, String password, HttpServletRequest request) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        if (userRepository.findUserEsByPhone(phone) == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (!BCryptUtils.matches(password, user.getPassword())) {
            throw new ServiceException(ResCode.FAILED_LOGIN);
        }

        List<String> list = new ArrayList<>();
        list.add(tokenService.createToken(user.getUserId(), secret,
                UserIdentity.ORDINARY.getValue(), user.getNickname(),
                user.getPhoto()));
        list.add(user.getUserId().toString());

        HttpSession session = request.getSession(true);
        session.setAttribute("curUserId", user.getUserId());

        return list;
    }

    // 验证码登录
    public List<String> codeLogin(String phone, String code, HttpServletRequest request) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        if (userRepository.findUserEsByPhone(phone) == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (!code.equals(redisService.get(RedisConstants.LOGIN_PHONE_CODE_KEY + phone, String.class))) {
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        redisService.delete(RedisConstants.LOGIN_PHONE_CODE_KEY + phone);

        List<String> list = new ArrayList<>();
        list.add(tokenService.createToken(user.getUserId(), secret,
                UserIdentity.ORDINARY.getValue(), user.getNickname(),
                user.getPhoto()));
        list.add(user.getUserId().toString());

        HttpSession session = request.getSession(true);
        session.setAttribute("curUserId", user.getUserId());

        return list;
    }

    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }
        return tokenService.deleteLoginUser(token, secret);
    }

    public Resp<UserVo> getUser(String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        UserEs useres = userRepository.findUserEsByUserId(loginUserData.getUserId());
        if (useres == null) {
            return null;
        }
        UserVo userVo = BeanUtil.copyProperties(useres, UserVo.class);

        return Resp.ok(userVo);
    }

    public int updateUser(UserUpdateDto userUpdateDto, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        UserEs useres = userRepository.findUserEsByUserId(loginUserData.getUserId());
        if (useres == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        if (userUpdateDto.getPhone() != null) {
            // 获取验证码
            String code = redisService.get(RedisConstants.UPDATE_PHONE_CODE_KEY + userUpdateDto.getPhone(), String.class);
            // 判断验证码
            if (code != null && !userUpdateDto.getCode().equals(code)) {
                throw new ServiceException(ResCode.FAILED_CODE);
            }

            redisService.delete(RedisConstants.UPDATE_PHONE_CODE_KEY + userUpdateDto.getPhone());
            useres.setPhone(userUpdateDto.getPhone());
        }

        if (userUpdateDto.getNickname() != null) {
            useres.setNickname(userUpdateDto.getNickname());
        }
        if (userUpdateDto.getEmail() != null) {
            useres.setEmail(userUpdateDto.getEmail());
        }
        if (userUpdateDto.getIntroduce() != null) {
            useres.setIntroduce(userUpdateDto.getIntroduce());
        }

        userRepository.save(useres);

        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());
        User user = BeanUtil.copyProperties(useres, User.class);
        int i = userMapper.updateById(user);
        ThreadLocalUtil.remove();
        return i;
    }

    public int updatePassword(PasswordUpdateDto passwordUpdateDto, String token) {
        // 获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);

        User user = userMapper.selectById(loginUserData.getUserId());
        if (user == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        if (!BCryptUtils.matches(passwordUpdateDto.getOldPassword(), user.getPassword())) {
            throw new ServiceException(ResCode.OLD_PASSWORD_NO_PASSWORD);
        }

        if (!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
            throw new ServiceException(ResCode.PASSWORD_NO_PASSWORD);
        }

        // 获取验证码
        String code = redisService.get(RedisConstants.UPDATE_PASSWORD_CODE_KEY + user.getPhone(), String.class);
        // 判断验证码
        if (!passwordUpdateDto.getCode().equals(code)) {
            throw new ServiceException(ResCode.FAILED_CODE);
        }
        redisService.delete(RedisConstants.UPDATE_PASSWORD_CODE_KEY + user.getPhone());

        user.setPassword(BCryptUtils.encrypt(passwordUpdateDto.getNewPassword()));

        ThreadLocalUtil.set("curUserId", loginUserData.getUserId());
        int i = userMapper.updateById(user);
        ThreadLocalUtil.remove();

        return i;
    }
}

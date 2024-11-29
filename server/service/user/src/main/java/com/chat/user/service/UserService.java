package com.chat.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chat.core.constants.HttpConstants;
import com.chat.core.constants.RedisConstants;
import com.chat.core.entity.LoginUserData;
import com.chat.core.enums.ResCode;
import com.chat.core.enums.UserIdentity;
import com.chat.core.utils.BCryptUtils;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.oss.config.OSSProperties;
import com.chat.oss.domain.OSSResp;
import com.chat.redis.service.RedisService;
import com.chat.security.exception.ServiceException;
import com.chat.security.service.TokenService;
import com.chat.sms.SMSService;
import com.chat.user.entity.User;
import com.chat.user.entity.UserEs;
import com.chat.user.entity.dto.SendCodeDto;
import com.chat.user.entity.dto.UserAddDto;
import com.chat.user.entity.dto.UserUpdateDto;
import com.chat.user.entity.vo.UserVo;
import com.chat.user.es.UserRepository;
import com.chat.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SMSService smsService;

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Resource
    private OSSProperties properties;

    @Resource
    private OSSClient ossClient;

    // 发送手机验证码
    public boolean sendRegCode(SendCodeDto sendCodeDto) {
        if (!checkPhone(sendCodeDto.getPhone())) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        String code = RandomUtil.randomNumbers(6);

        // 存储到redis中 有效时间5分钟
        if (sendCodeDto.getStatus().equals(1)) {
            // 注册
            redisService.set(RedisConstants.REG_PHONE_CODE_KEY + sendCodeDto.getPhone(), code, 5L, TimeUnit.MINUTES);
        } else if (sendCodeDto.getStatus().equals(2)) {
            // 登录
            redisService.set(RedisConstants.LOGIN_PHONE_CODE_KEY + sendCodeDto.getPhone(), code, 5L, TimeUnit.MINUTES);
        } else if (sendCodeDto.getStatus().equals(3)) {
            // 修改手机号码需要判断该手机号码是否已经存在
            if (userRepository.findUserEsByPhone(sendCodeDto.getPhone()) != null)
                throw new ServiceException(ResCode.PHONE_EXISTS);

            redisService.set(RedisConstants.UPDATE_PHONE_CODE_KEY + sendCodeDto.getPhone(), code, 5L, TimeUnit.MINUTES);
        } else if (sendCodeDto.getStatus().equals(4)) {
            // 修改密码
            redisService.set(RedisConstants.UPDATE_PASSWORD_CODE_KEY + sendCodeDto.getPhone(), code, 5L, TimeUnit.MINUTES);
        }

        return smsService.sendPhoneCode(sendCodeDto.getPhone(), code);
    }

    // 注册
    public boolean reg(UserAddDto userAddDto) {
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

        // 添加数据库
        User user = BeanUtil.copyProperties(userAddDto, User.class);
        user.setPassword(BCryptUtils.encrypt(user.getPassword()));
        if (userMapper.insert(user) < 1) {
            return false;
        }

        // 添加到 es
        user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, userAddDto.getPhone()));
        UserEs userEs = BeanUtil.copyProperties(user, UserEs.class);
        userRepository.save(userEs);

        // 删除 redis 中的验证码
        redisService.delete(RedisConstants.REG_PHONE_CODE_KEY + userAddDto.getPhone());

        return true;
    }

    public String passLogin(String phone, String password) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        // 判断用户是否存在
        if (userRepository.findUserEsByPhone(phone) == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        // 判断密码是否正确
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (!BCryptUtils.matches(password, user.getPassword())) {
            throw new ServiceException(ResCode.FAILED_LOGIN);
        }

        switch (user.getStatus()) {
            case 1:
                break;
            case 2:
                throw new ServiceException(ResCode.FREEZE_APPLY);
            case 3:
                throw new ServiceException(ResCode.CANCEL_APPLY);
            case 4:
                throw new ServiceException(ResCode.FREEZE_NOW);
        }

        return tokenService.createToken(user.getUserId(), secret, UserIdentity.ORDINARY.getValue(),
                user.getNickname(), user.getPhoto());
    }

    public String codeLogin(String phone, String code) {
        // 判断手机号码是否合理
        if (!checkPhone(phone)) {
            throw new ServiceException(ResCode.FAILED_PHONE);
        }

        // 判断用户是否存在
        UserEs userEs = userRepository.findUserEsByPhone(phone);
        if (userEs == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        // 判断验证码是否正确
        if (!code.equals(redisService.get(RedisConstants.LOGIN_PHONE_CODE_KEY + phone, String.class))) {
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        // 删除缓存中的验证码
        redisService.delete(RedisConstants.LOGIN_PHONE_CODE_KEY + phone);

        switch (userEs.getStatus()) {
            case 1:
                break;
            case 2:
                throw new ServiceException(ResCode.FREEZE_APPLY);
            case 3:
                throw new ServiceException(ResCode.CANCEL_APPLY);
            case 4:
                throw new ServiceException(ResCode.FREEZE_NOW);
        }

        return tokenService.createToken(userEs.getUserId(), secret, UserIdentity.ORDINARY.getValue(),
                userEs.getNickname(), userEs.getPhoto());
    }

    public boolean logout(String token) {
        if (StrUtil.isNotEmpty(token) && token.startsWith(HttpConstants.PREFIX)) {
            token = token.replaceFirst(HttpConstants.PREFIX, StrUtil.EMPTY);
        }

        return tokenService.deleteLoginUser(token, secret);
    }

    public UserVo getUser(String token) {
        // 从es获取用户信息
        LoginUserData loginUserData = getLoginUserData(token);
        return BeanUtil.copyProperties(userRepository.findUserEsByUserId(loginUserData.getUserId()), UserVo.class);
    }

    public boolean updateUser(String token, UserUpdateDto userUpdateDto) {
        // 从es获取用户数据
        UserEs userEs = userRepository.findUserEsByUserId(userUpdateDto.getUserId());
        if (userEs == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        // 手机号码不为空时，判断验证码
        if (userUpdateDto.getPhone() != null) {
            // 获取验证码
            String code = redisService.get(RedisConstants.UPDATE_PHONE_CODE_KEY + userUpdateDto.getPhone(), String.class);
            // 判断验证码
            if (code != null && !userUpdateDto.getPhoneCode().equals(code)) {
                throw new ServiceException(ResCode.FAILED_CODE);
            }

            redisService.delete(RedisConstants.UPDATE_PHONE_CODE_KEY + userUpdateDto.getPhone());
            userEs.setPhone(userUpdateDto.getPhone());
        }

        // 邮箱不为空时，判断验证码
        if (userUpdateDto.getEmail() != null) {
            // 获取验证码
            String code = redisService.get(RedisConstants.UPDATE_EMAIL_CODE_KEY + userUpdateDto.getEmail(), String.class);
            // 判断验证码
            if (code != null && !userUpdateDto.getEmailCode().equals(code)) {
                throw new ServiceException(ResCode.FAILED_CODE);
            }

            redisService.delete(RedisConstants.UPDATE_EMAIL_CODE_KEY + userUpdateDto.getEmail());
            userEs.setEmail(userUpdateDto.getEmail());
        }

        // 昵称和个性签名
        if (userUpdateDto.getNickname() != null) {
            userEs.setNickname(userUpdateDto.getNickname());

            // 修改redis中存储的已登录的token信息
            String userKey = tokenService.getUserKey(token, secret);
            LoginUserData loginUserData = getLoginUserData(token);
            loginUserData.setNickname(userUpdateDto.getNickname());
            redisService.set(RedisConstants.LOGIN_TOKEN_KEY + userKey, loginUserData, RedisConstants.EXP, TimeUnit.MINUTES);
        }
        if (userUpdateDto.getIntroduce() != null) {
            userEs.setIntroduce(userUpdateDto.getIntroduce());
        }

        // 更新 es
        userRepository.save(userEs);

        // 更新数据库
        ThreadLocalUtil.set("curUserId", userUpdateDto.getUserId());
        User user = BeanUtil.copyProperties(userEs, User.class);
        int i = userMapper.updateById(user);
        ThreadLocalUtil.remove();

        return i > 0;
    }

    public boolean updatePassword(UserUpdateDto userUpdateDto) {
        User user = userMapper.selectById(userUpdateDto.getUserId());
        if (user == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }

        // 判断输入的旧密码是否和当前密码匹配
        if (!BCryptUtils.matches(userUpdateDto.getOldPassword(), user.getPassword())) {
            throw new ServiceException(ResCode.OLD_PASSWORD_NO_PASSWORD);
        }

        // 获取验证码
        String code = redisService.get(RedisConstants.UPDATE_PASSWORD_CODE_KEY + user.getPhone(), String.class);
        // 判断验证码
        if (!userUpdateDto.getPasswordCode().equals(code)) {
            throw new ServiceException(ResCode.FAILED_CODE);
        }

        redisService.delete(RedisConstants.UPDATE_PASSWORD_CODE_KEY + user.getPhone());

        // 更新数据库
        ThreadLocalUtil.set("curUserId", userUpdateDto.getUserId());
        user.setPassword(BCryptUtils.encrypt(userUpdateDto.getPassword()));
        int i = userMapper.updateById(user);
        ThreadLocalUtil.remove();

        return i > 0;
    }

    public boolean uploadPhoto(String token, MultipartFile file) throws IOException {
        InputStream inputStream = null;
        try {
            String fileName;
            if (file.getOriginalFilename() != null) {
                fileName = file.getOriginalFilename().toLowerCase();
            } else {
                fileName = "1.png";
            }
            String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
            inputStream = file.getInputStream();

            // 上传
            fileName = upload(extName, inputStream);
            if (fileName.isEmpty()) {
                return false;
            }

            // 修改redis中存储的已登录的token信息
            String userKey = tokenService.getUserKey(token, secret);
            LoginUserData loginUserData = getLoginUserData(token);
            loginUserData.setPhoto(fileName);
            redisService.set(RedisConstants.LOGIN_TOKEN_KEY + userKey, loginUserData, RedisConstants.EXP, TimeUnit.MINUTES);

            return true;
        } catch (Exception e) {
            log.error("OSS upload file error", e);
            throw new ServiceException(ResCode.FAILED_FILE_UPLOAD);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public boolean updateStatus(String userId, Integer status) {
        UserEs userEs = userRepository.findUserEsByUserId(userId);
        if (userEs == null) {
            throw new ServiceException(ResCode.USER_NOT_EXISTS);
        }
        userEs.setStatus(status);
        userRepository.save(userEs);

        User user = BeanUtil.copyProperties(userEs, User.class);
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    // 判断手机合理性
    private boolean checkPhone(String phone) {
        Pattern regex = Pattern.compile("^1[345789]\\d{9}$");
        Matcher m = regex.matcher(phone);
        return m.matches();
    }

    // 根据token获取缓存中保存的用户基本信息
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

    private String upload(String fileType, InputStream inputStream) {
        String key = "head-" + ObjectId.next() + "." + fileType;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectAcl(CannedAccessControlList.PublicRead);
        PutObjectRequest request = new PutObjectRequest(properties.getBucket(), key, inputStream, objectMetadata);
        PutObjectResult putObjectResult;
        try {
            putObjectResult = ossClient.putObject(request);
        } catch (Exception e) {
            log.error("OSS put object error: {}", ExceptionUtil.stacktraceToOneLineString(e, 500));
            throw new ServiceException(ResCode.FAILED_FILE_UPLOAD);
        }

        OSSResp ossResult = new OSSResp();
        if (putObjectResult == null || StrUtil.isBlank(putObjectResult.getRequestId())) {
            return "";
        }

        return FileUtil.getName(key);
    }
}
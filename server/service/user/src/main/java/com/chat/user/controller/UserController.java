package com.chat.user.controller;

import com.chat.core.constants.HttpConstants;
import com.chat.core.entity.Resp;
import com.chat.core.enums.ResCode;
import com.chat.user.entity.dto.SendCodeDto;
import com.chat.user.entity.dto.UserAddDto;
import com.chat.user.entity.dto.UserLoginDto;
import com.chat.user.entity.dto.UserUpdateDto;
import com.chat.user.entity.vo.UserVo;
import com.chat.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@Tag(name = "用户模块")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendCode")
    @Operation(description = "发送手机验证码")
    public Resp<Void> sendCode(@RequestBody SendCodeDto sendCodeDto) {
        return userService.sendRegCode(sendCodeDto) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/reg")
    @Operation(description = "用户注册")
    public Resp<Void> reg(@RequestBody UserAddDto userAddDto) {
        return userService.reg(userAddDto) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/passLogin")
    @Operation(description = "密码登录")
    public Resp<String> passLogin(@RequestBody UserLoginDto userLoginDto) {
        return Resp.ok(userService.passLogin(userLoginDto.getPhone(), userLoginDto.getPassword()));
    }

    @PostMapping("/codeLogin")
    @Operation(description = "验证码登录")
    public Resp<String> codeLogin(@RequestBody UserLoginDto userLoginDto) {
        return Resp.ok(userService.codeLogin(userLoginDto.getPhone(), userLoginDto.getCode()));
    }

    @DeleteMapping("/logout")
    @Operation(description = "退出登录")
    public Resp<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.logout(token) ? Resp.ok() : Resp.fail();
    }

    @GetMapping("/getUser")
    @Operation(description = "获取当前用户基本信息")
    public Resp<UserVo> getLoginUser(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        UserVo userVo = userService.getUser(token);
        return userVo == null ? Resp.fail(ResCode.FAILED_UNAUTHORIZED) : Resp.ok(userVo);
    }

    @PutMapping("/updateUser")
    @Operation(description = "修改用户基本信息")
    public Resp<Void> updateUser(@RequestHeader(HttpConstants.AUTHENTICATION) String token,
                                 @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(token, userUpdateDto) ? Resp.ok() : Resp.fail();
    }

    @PutMapping("/updatePassword")
    @Operation(description = "修改用户密码")
    public Resp<Void> updatePassword(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.updatePassword(userUpdateDto) ? Resp.ok() : Resp.fail();
    }

    @PutMapping("/updatePhoto")
    @Operation(description = "修改用户头像")
    public Resp<Void> uploadPhoto(@RequestHeader(HttpConstants.AUTHENTICATION) String token, MultipartFile photo) throws IOException {
        return userService.uploadPhoto(token, photo) ? Resp.ok() : Resp.fail();
    }

    @PutMapping("/freezeApply")
    @Operation(description = "申请冻结")
    public Resp<Void> freezeApply(String userId) {
        return userService.updateStatus(userId, 2) ? Resp.ok() : Resp.fail();
    }

    @PutMapping("/cancelApply")
    @Operation(description = "申请注销")
    public Resp<Void> cancelApply(String userId) {
        return userService.updateStatus(userId, 3) ? Resp.ok() : Resp.fail();
    }

//    @PostMapping("sendEmailCode")
//    @Operation(description = "发送邮箱验证码")
//    public Resp<Void> sendEmailCode(String email) {
//        return userService.sendEmailCode(email) ? Resp.ok() : Resp.fail();
//    }
}

package com.chat.user.controller;

import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.domain.vo.LoginUserVO;
import com.chat.user.domain.dto.PasswordUpdateDto;
import com.chat.user.domain.dto.UserAddDto;
import com.chat.user.domain.dto.UserDto;
import com.chat.user.domain.dto.UserUpdateDto;
import com.chat.user.domain.vo.UserVo;
import com.chat.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/sendRegCode")
    public Resp<Void> sendRegCode(@RequestBody UserDto userDto) {
        return userService.sendRegCode(userDto) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/sendLoginCode")
    public Resp<Void> sendLoginCode(@RequestBody UserDto userDto) {
        return userService.sendLoginCode(userDto) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/sendUpdatePhoneCode")
    public Resp<Void> sendUpdatePhoneCode(@RequestParam String phone) {
        return userService.sendUpdatePhoneCode(phone) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/sendUpdatePasswordCode")
    public Resp<Void> sendUpdatePasswordCode(@RequestParam String phone) {
        return userService.sendUpdatePasswordCode(phone) ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/reg")
    public Resp<Void> reg(@RequestBody UserAddDto userAddDto) {
        return userService.reg(userAddDto) > 0 ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/passLogin")
    public Resp<List<String>> passLogin(@RequestBody UserDto userDto, HttpServletRequest request) {
        return Resp.ok(userService.passLogin(userDto.getPhone(), userDto.getPassword(), request));
    }

    @PostMapping("/codeLogin")
    public Resp<List<String>> codeLogin(@RequestBody UserDto userDto, HttpServletRequest request) {
        return Resp.ok(userService.codeLogin(userDto.getPhone(), userDto.getCode() ,request));
    }

    @DeleteMapping("/logout")
    public Resp<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.logout(token) ? Resp.ok() : Resp.fail();
    }

    @GetMapping("/getUser")
    public Resp<UserVo> getUser(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.getUser(token);
    }

    @PutMapping("/updateUser")
    public Resp<Void> updateUser(@RequestBody UserUpdateDto userUpdateDto,
                                 @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.updateUser(userUpdateDto, token) > 0 ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/updatePassword")
    public Resp<Void> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto,
                                    @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.updatePassword(passwordUpdateDto, token) > 0 ? Resp.ok() : Resp.fail();
    }


}

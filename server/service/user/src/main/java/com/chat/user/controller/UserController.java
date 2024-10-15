package com.chat.user.controller;

import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.LoginUserData;
import com.chat.core.domain.Resp;
import com.chat.core.domain.vo.LoginUserVO;
import com.chat.user.domain.dto.UserAddDto;
import com.chat.user.domain.dto.UserDto;
import com.chat.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/reg")
    public Resp<Void> reg(@RequestBody UserAddDto userAddDto) {
        return userService.reg(userAddDto) > 0 ? Resp.ok() : Resp.fail();
    }

    @PostMapping("/passLogin")
    public Resp<String> passLogin(@RequestBody UserDto userDto) {
        return Resp.ok(userService.passLogin(userDto.getPhone(), userDto.getPassword()));
    }

    @PostMapping("/codeLogin")
    public Resp<String> codeLogin(@RequestBody UserDto userDto) {
        return Resp.ok(userService.codeLogin(userDto.getPhone(), userDto.getCode()));
    }

    @DeleteMapping("/logout")
    public Resp<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.logout(token) ? Resp.ok() : Resp.fail();
    }

    @GetMapping("/getUser")
    public Resp<LoginUserVO> getUser(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return userService.getUser(token);
    }
}

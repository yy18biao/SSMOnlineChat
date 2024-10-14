package com.chat.admin.controller;

import com.chat.admin.domain.admin.AdminAddDto;
import com.chat.admin.domain.admin.AdminDto;
import com.chat.admin.service.AdminService;
import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.Resp;
import com.chat.core.domain.vo.LoginUserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;

    @PostMapping("/login")
    public Resp<String> login(@Validated @RequestBody AdminDto adminDto) {
        return adminService.login(adminDto.getUserId(), adminDto.getPassword());
    }

    @PostMapping("/add")
    public Resp<Void> add(@RequestBody AdminAddDto adminAddDto) {
        return adminService.add(adminAddDto) > 0 ? Resp.ok() : Resp.fail();
    }

    @GetMapping("/getAdmin")
    public Resp<LoginUserVO> getUser(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return adminService.getAdmin(token);
    }

    @DeleteMapping("/logout")
    public Resp<Void> logout(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return adminService.logout(token) ? Resp.ok() : Resp.fail();
    }
}

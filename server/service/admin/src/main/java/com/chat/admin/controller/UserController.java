package com.chat.admin.controller;

import com.chat.admin.domain.user.UserDto;
import com.chat.admin.domain.user.UserSearchDto;
import com.chat.admin.service.UserService;
import com.chat.core.controller.BaseController;
import com.chat.core.domain.Resp;
import com.chat.core.domain.TableData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin")
public class UserController extends BaseController {
    @Resource
    private UserService userService;

    @GetMapping("/list")
    public TableData list(UserSearchDto userSearchDto) {
        return getTableData(userService.list(userSearchDto));
    }

    @GetMapping("/applyList")
    public TableData applyList(UserSearchDto userSearchDto) {
        return getTableData(userService.applyList(userSearchDto));
    }

    @PutMapping("/updateStatus")
    public Resp<Void> updateStatus(@RequestBody UserDto userDto) {
        return userService.updateStatus(userDto) > 0 ? Resp.ok() : Resp.fail();
    }
}

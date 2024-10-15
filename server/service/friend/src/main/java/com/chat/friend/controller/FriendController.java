package com.chat.friend.controller;

import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.Resp;
import com.chat.friend.domain.dto.FriendSearchDto;
import com.chat.friend.domain.dto.FriendUpdateDto;
import com.chat.friend.domain.vo.FriendDataVo;
import com.chat.friend.domain.vo.FriendSearchVo;
import com.chat.friend.service.FriendService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    private FriendService friendService;

    @GetMapping("/list")
    public Resp<List<FriendDataVo>> list(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(friendService.list(token));
    }

    @GetMapping("/search")
    public Resp<FriendSearchVo> search(@RequestParam Long friendId, @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(friendService.search(friendId, token));
    }

    // 修改好友备注
    @PutMapping("/updateRemark")
    public Resp<Void> updateRemark(@RequestBody FriendUpdateDto friendUpdateDto, @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return friendService.updateRemark(friendUpdateDto.getFriendId(), friendUpdateDto.getFriendName(), token) > 0 ? Resp.ok() : Resp.fail();
    }
}

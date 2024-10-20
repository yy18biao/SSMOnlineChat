package com.chat.friend.controller;

import com.chat.core.constants.HttpConstants;
import com.chat.core.domain.Resp;
import com.chat.core.utils.ThreadLocalUtil;
import com.chat.friend.domain.FriendApply;
import com.chat.friend.domain.dto.FriendSearchDto;
import com.chat.friend.domain.dto.FriendUpdateDto;
import com.chat.friend.domain.vo.FriendDataVo;
import com.chat.friend.domain.vo.FriendSearchVo;
import com.chat.friend.domain.vo.SearchUserVo;
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

    // 修改好友备注
    @DeleteMapping("/deleteFriend")
    public Resp<Void> deleteFriend(@RequestParam Long friendId, @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return friendService.deleteFriend(friendId, token) ? Resp.ok() : Resp.fail();
    }

    // 查询单个用户
    @GetMapping("/searchUser")
    public Resp<SearchUserVo> searchUser(String searchName) {
        return Resp.ok(friendService.searchUser(searchName));
    }

    // 查看好友申请列表
    @GetMapping("/friendApplyList")
    public Resp<List<FriendApply>> friendApplyList(@RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return Resp.ok(friendService.friendApplyList(token));
    }

    // 同意好友申请
    @PostMapping("/agreeFriendApply")
    public Resp<Void> agreeFriendApply(@RequestParam Long friendId,
                                       @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return friendService.agreeFriendApply(friendId, token) ? Resp.ok() : Resp.fail();
    }

    // 拒绝好友申请
    @DeleteMapping("/refuseFriendApply")
    public Resp<Void> refuseFriendApply(Long friendId, @RequestHeader(HttpConstants.AUTHENTICATION) String token) {
        return friendService.refuseFriendApply(friendId, token) > 0 ? Resp.ok() : Resp.fail();
    }
}

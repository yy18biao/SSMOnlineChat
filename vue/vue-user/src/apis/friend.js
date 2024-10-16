import service from "@/utils/request";

export function getFriendListService() {
    return service({
        url: "/friend/list",
        method: "get",
    });
}

export function searchFriendService(friendId) {
    return service({
        url: "/friend/search",
        method: "get",
        params: {
            friendId: friendId,
        },
    });
}

export function updateRemarkService(params = {}) {
    return service({
        url: "/friend/updateRemark",
        method: "PUT",
        data: params,
    });
}

export function deleteFriendService(friendId) {
    return service({
        url: "/friend/deleteFriend",
        method: "delete",
        params: {
            friendId: friendId,
        },
    });
}

export function searchUserService(searchName) {
    return service({
        url: "/friend/searchUser",
        method: "get",
        params: {
            searchName: searchName,
        },
    });
}

export function addFriendService(friendId) {
    return service({
        url: "/friend/addFriend",
        method: "post",
        params: {
            friendId: friendId,
        },
    });
}

export function friendApplyListService() {
    return service({
        url: "/friend/friendApplyList",
        method: "get",
    });
}

export function agreeFriendApplyService(friendId) {
    return service({
        url: "/friend/agreeFriendApply",
        method: "post",
        params: {
            friendId: friendId,
        }
    });
}

export function refuseFriendApplyService(friendId) {
    return service({
        url: "/friend/refuseFriendApply",
        method: "delete",
        params: {
            friendId: friendId,
        }
    });
}

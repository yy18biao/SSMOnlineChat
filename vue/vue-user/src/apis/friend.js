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
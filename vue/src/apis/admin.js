import request from "@/utils/request";

export function loginService(userId, password) {
    return request({
        url: "/admin/login",
        headers: {
            isToken: false,
        },
        method: "post",
        data: { userId, password },
    });
}

export function getAdminService() {
    return request({
        url: "/admin/getAdmin",
        method: "get",
    });
}

export function logoutService() {
    return request({
        url: "/admin/logout",
        method: "delete",
    });
}

export function getUserListService(params) {
    return service({
        url: "/admin/list",
        method: "get",
        params,
    });
}

export function getApplyListService(params) {
    return service({
        url: "/admin/applyList",
        method: "get",
        params,
    });
}

export function updateStatusService(params = {}) {
    return service({
        url: "/admin/updateStatus",
        method: "put",
        data: params,
    });
}
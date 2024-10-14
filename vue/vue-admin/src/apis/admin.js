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
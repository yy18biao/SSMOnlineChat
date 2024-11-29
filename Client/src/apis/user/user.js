import service from "@/utils/request.js";

// 退出登录
export function logoutService() {
    return service({
        url: "/user/logout",
        method: "delete",
    });
}
import service from "@/utils/request";

export function getUserService() {
    return service({
        url: "/user/getUser",
        method: "get",
    });
}

export function logoutService() {
    return service({
        url: "/user/logout",
        method: "delete",
    });
}
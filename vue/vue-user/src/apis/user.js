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

export function updateUserService(params = {}) {
    return service({
        url: "/user/updateUser",
        method: "put",
        data: params,
    });
}

export function updatePhoneCodeService(phone) {
    return service({
        url: "/user/sendUpdatePhoneCode",
        method: "post",
        params: {
            phone: phone
        },
    });
}

export function updatePasswordService(params = {}) {
    return service({
        url: "/user/updatePassword",
        method: "post",
        data: params,
    });
}

export function updatePasswordCodeService(phone) {
    return service({
        url: "/user/sendUpdatePasswordCode",
        method: "post",
        params: {
            phone: phone
        },
    });
}

export function deleteWebSocketService() {
    return service({
        url: "/websocket/deleteWebSocket",
        method: "delete",
    });
}
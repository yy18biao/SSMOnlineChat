import service from "@/utils/request.js";

export function sendLoginCodeService(params = {}) {
    return service({
        url: "/user/sendCode",
        method: "post",
        data: params,
    });
}

export function passLoginService(params = {}) {
    return service({
        url: "/user/passLogin",
        method: "post",
        data: params,
    });
}

export function codeLoginService(params = {}) {
    return service({
        url: "/user/codeLogin",
        method: "post",
        data: params,
    });
}
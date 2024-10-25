import service from "@/utils/request";

export function sendLoginCode(params = {}) {
    return service({
        url: "/user/sendLoginCode",
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

export function sendRegCodeService(params = {}) {
    return service({
        url: "/user/sendRegCode",
        method: "post",
        data: params,
    });
}

export function regService(params = {}) {
    return service({
        url: "/user/reg",
        method: "post",
        data: params,
    });
}
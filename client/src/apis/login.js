import service from "@/utils/request";

export function sendLoginCodeService(phone) {
    return service({
        url: "/user/sendLoginCode",
        method: "post",
        params: {
            phone: phone
        },
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

export function sendRegCodeService(phone) {
    return service({
        url: "/user/sendRegCode",
        method: "post",
        params: {
            phone: phone
        },
    });
}

export function regService(params = {}) {
    return service({
        url: "/user/reg",
        method: "post",
        data: params,
    });
}
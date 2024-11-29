import service from "@/utils/request";

export function sendRegCodeService(phone, status) {
    return service({
        url: "/user/sendCode",
        method: "post",
        data: {
            phone: phone,
            status: status
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
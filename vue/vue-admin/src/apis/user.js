import service from "@/utils/request";

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
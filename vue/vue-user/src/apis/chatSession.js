import service from "@/utils/request";

export function getChatSessionListService() {
    return service({
        url: "/chatSession/getChatSessionList",
        method: "get",
    });
}

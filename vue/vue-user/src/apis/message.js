import service from "@/utils/request";

export function getMessageAllService(chatSessionId) {
    return service({
        url: "/message/all",
        method: "get",
        params: {
            chatSessionId: chatSessionId
        }
    });
}
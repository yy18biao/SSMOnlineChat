import service from "@/utils/request";

export function getChatSessionListService() {
    return service({
        url: "/chatSession/getChatSessionList",
        method: "get",
    });
}

export function addChatSessionListService(friendId) {
    return service({
        url: "/chatSession/addChatSession",
        method: "get",
        params: {
            friendId: friendId,
        }
    });
}

export function searchChatSessionService(chatSessionId) {
    return service({
        url: "/chatSession/searchChatSession",
        method: "get",
        params: {
            chatSessionId: chatSessionId,
        }
    });
}

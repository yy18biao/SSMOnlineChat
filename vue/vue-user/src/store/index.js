import {createStore} from 'vuex';

export default createStore({
    state() {
        return {
            friendData: {
                userId: '',
                nickname: '',
                photo: '',
                introduce: '',
                email: '',
                friendName: ''
            }

        };
    },
    mutations: {
        setFriendData(state, newValue) {
            state.friendData = newValue;
        },
        setFriendDataFriendName(state, newValue) {
            state.friendName = newValue;
        },
        setChatSessionId(state, newValue) {
            state.chatSessionId = newValue;
        }
    },
    actions: {
        updateFriendId({commit}, newValue) {
            commit('setFriendId', newValue);
        },
        updateFriendName({commit}, newValue) {
            commit('setFriendName', newValue);
        },
        updateChatSessionId({commit}, newValue) {
            commit('setChatSessionId', newValue);
        }
    },
    getters: {
        friendId: state => state.friendId,
        friendName: state => state.friendName,
        chatSessionId: state => state.chatSessionId,
    }
});
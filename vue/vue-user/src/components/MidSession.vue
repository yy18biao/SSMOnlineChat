<script setup>

import {ref, reactive, defineEmits } from "vue";
import {Search} from "@element-plus/icons-vue";
import {getChatSessionListService} from "@/apis/chatSession.js";
import router from "@/router/index.js";

const searchName = ref('')
const sessionData = reactive([])

// 点击会话信号
const emit = defineEmits(['openChatSessionRight']);

// 触发点击会话信号处理
function openChatSessionRight(chatSessionId) {
  emit('openChatSessionRight', chatSessionId);
}

// 获取会话列表
async function getChatSessionList() {
  try {
    const chatSessions = await getChatSessionListService();

    for (let i = 0; i < chatSessions.data.length; i++) {
      let chatSessionId = chatSessions.data[i].chatSessionId
      let chatSessionName = chatSessions.data[i].chatSessionName
      let chatSessionLastMessage = chatSessions.data[i].chatSessionLastMessage.substring(0, 9) + '...'
      let chatSessionPhoto = chatSessions.data[i].chatSessionPhoto

      sessionData.push({chatSessionId, chatSessionName, chatSessionLastMessage, chatSessionPhoto})
    }
  }catch (e){
    console.error(e)
    await router.push("/user/login")
  }

  return sessionData
}
getChatSessionList()


</script>

<template>
  <el-container>
    <el-header class="el-header">
      <el-input v-model=searchName style="width: 200px;margin-top: 20px" placeholder="请输入会话名"
                :prefix-icon="Search"/>
    </el-header>

    <el-main style="width: 100%; display: flex; flex-direction: column; justify-content: center; align-items: center;">
      <el-menu class="el-menu">
        <el-menu-item class="el-menu-item" v-for="(index) in sessionData"
                      @click="openChatSessionRight(index.chatSessionId)">
          <div class="photo">
            <el-avatar :src=index.chatSessionPhoto size="large"/>
          </div>
          <div class="session-main">
            <span class="name">{{ index.chatSessionName }}</span>
            <span class="message">{{ index.chatSessionLastMessage }}</span>
          </div>
        </el-menu-item>
      </el-menu>
    </el-main>
  </el-container>
</template>

<style scoped lang="scss">
html, body {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.el-menu {
  height: 100%;
  width: 100%;

  .el-menu-item {
    height: 100px;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;

    .photo {
      width: 80px;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .session-main {
      height: 100%;
      width: 160px;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
      align-items: flex-start;
      margin-left: 10px;

      .name {
        height: 30px;
        width: 150px;
        text-align: left;
      }

      .message {
        height: 30px;
        width: 150px;
        text-align: left;
        margin-top: 5px;
      }
    }
  }
}
</style>
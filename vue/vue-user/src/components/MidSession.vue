<script setup>

import {ref, reactive, defineEmits, watch} from "vue";
import {Search} from "@element-plus/icons-vue";
import {getChatSessionListService, searchChatSessionService} from "@/apis/chatSession.js";

const searchName = ref('')

const props = defineProps({
  curSessionId: {
    required: true,
    type: Number,
  },
  sessionDataList: {
    required: true
  },
  rightFlag: {
    required: true,
  }
})

// 点击会话信号
const emit = defineEmits(['openChatSessionRight']);

// 触发点击会话信号处理
function openChatSessionRight(index) {
  // 如果点击的会话不是当前会话则通知父组件
  if (props.rightFlag !== 1 || props.sessionDataList[0].chatSessionId !== index.chatSessionId) {
    emit('openChatSessionRight', index.chatSessionId, index.chatSessionName);
  }
}

</script>

<template>
  <el-container>
    <el-header style="height: 70px">
      <el-input v-model=searchName style="width: 200px; margin-top: 20px" placeholder="请输入会话名"
                :prefix-icon="Search"/>
    </el-header>

    <el-main style="width: 100%; height: 630px; overflow: scroll; scrollbar-width: none;
                    display: flex; flex-direction: column; justify-content: center; align-items: center;">
      <el-menu class="el-menu">
        <el-menu-item class="el-menu-item" v-for="(index) in sessionDataList" @click="openChatSessionRight(index)">
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
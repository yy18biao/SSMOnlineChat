<script setup>

import {Search} from "@element-plus/icons-vue";
import {reactive} from 'vue'
import {getChatSessionListService} from "@/apis/chatSession.js";
import router from "@/router/index.js";

const searchName = ''

let sessionData = reactive([])

async function getChatSessionList() {
  try {
    const chatSessions = await getChatSessionListService();

    for (let i = 0; i < chatSessions.data.length; i++) {
      let index = '/user/messageSession/chatSession?chatSessionId=' + chatSessions.data[i].chatSessionId
          + '&chatSessionName=' + chatSessions.data[i].chatSessionName
      let chatSessionName = chatSessions.data[i].chatSessionName
      let chatSessionLastMessage = chatSessions.data[i].chatSessionLastMessage.substring(0, 9) + '...'
      let chatSessionPhoto = chatSessions.data[i].chatSessionPhoto

      sessionData.push({index, chatSessionName, chatSessionLastMessage, chatSessionPhoto})
    }
  }catch (e){
    console.error(e)
    router("/user/login")
  }

  return sessionData
}

getChatSessionList()

</script>

<template>
  <div class="main">
    <el-container direction="horizontal" style="position: relative;">
      <el-aside width="240px" class="mid">
        <el-container direction="vertical">
          <el-header class="el-header">
            <el-input v-model=searchName style="width: 200px;margin-top: 20px" placeholder="请输入会话名"
                      :prefix-icon="Search"/>
          </el-header>

          <el-main class="mid-box" direction="vertical">
            <el-menu class="el-menu" router>
              <el-menu-item class="el-menu-item" v-for="(index) in sessionData" :index="index.index">
                <el-container class="session" direction="horizontal">
                  <el-aside class="session-left">
                    <el-avatar :src=index.chatSessionPhoto size="large"/>
                  </el-aside>
                  <el-main class="session-main">
                    <span class="name">{{ index.chatSessionName }}</span>
                    <span class="message">{{ index.chatSessionLastMessage }}</span>
                  </el-main>
                </el-container>
              </el-menu-item>
            </el-menu>
          </el-main>
        </el-container>
      </el-aside>

      <el-main>
        <div class="right">
          <RouterView/>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped lang="scss">
.mid {
  height: 760px;
  display: flex;
  flex-direction: column;

  .mid-box {
    width: 240px;
    position: relative;
    flex-grow: 1;
    background: #f7f7f7;

    .el-menu {
      height: 100px;
      width: 240px;
      position: absolute; // 子组件设置为绝对定位
      left: 0;
      background: #f7f7f7;

      .el-menu-item {
        height: 100px;
        width: 240px;
        position: relative;
      }

      .session {
        height: 100px;
        width: 240px;
        position: absolute; // 子组件设置为绝对定位
        left: 0;

        .session-left {
          height: 100px;
          width: 80px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .session-main {
          height: 100%;
          width: 180px;
          position: relative;
          display: flex;
          flex-direction: column;

          .name {
            height: 30px;
            width: 150px;
            text-align: left;
            position: absolute; // 子组件设置为绝对定位
            left: 0;
            top: 0;
          }

          .message {
            height: 30px;
            width: 150px;
            text-align: left;
            position: absolute; // 子组件设置为绝对定位
            left: 0;
            margin-top: 20px;
          }
        }
      }
    }
  }
}

.right {
  width: 800px;
  height: 760px;
  background: #f5f5f5;
  position: absolute; // 子组件设置为绝对定位
  left: 240px;
  top: 0;
}
</style>
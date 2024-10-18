<script setup>
import {ChatLineRound, Folder, More} from "@element-plus/icons-vue";
import {reactive, ref, defineProps, watch} from 'vue'
import {getUserId} from "@/utils/cookie.js";

const props = defineProps({
  chatSessionId: {
    required: true,
    default: null
  },
  chatSessionName: {
    required: true,
    default: null
  },
  messageData:{
    required: true,
  }
})

// 当前用户id
const curUserId = getUserId()

const inputMessage = ref('')

</script>

<template>
  <el-container style="position: relative; height: 100%; width: 100%;">
    <el-header class="head">
      <el-button key="plain" class="head-name">{{ chatSessionName }}</el-button>
      <el-button class="head-more">
        <el-icon size="20px">
          <More/>
        </el-icon>
      </el-button>
    </el-header>

    <el-main style="height: 50%; border-bottom: 1px solid #e7e7e7; overflow: scroll; scrollbar-width: none;"
             ref="mainScrollRef">
      <div class="message-main" v-for="(index) in messageData">
        <div class="message-left" style="position: relative;" v-if="index.userId !== curUserId">
          <el-avatar :src=index.photo size="default"/>
          <div class="user-message">
            <div style="display: flex;">
              <div class="user-message-name">{{ index.nickname }}</div>
              <div class="user-message-time" style="margin-left: 10px;">{{index.createTime}}</div>
            </div>
            <div class="user-message-message">{{ index.content }}</div>
          </div>
        </div>

        <div class="message-right" style="position: relative;" v-else>
          <div class="user-message">
            <div style="display: flex;">
              <div class="user-message-time" style="margin-right: 10px;">{{index.createTime}}</div>
              <div class="user-message-name">{{ index.nickname }}</div>
            </div>
            <div class="user-message-message">{{ index.content }}</div>
          </div>
          <el-avatar :src=index.photo size="default"/>
        </div>
      </div>
    </el-main>

    <el-footer style="display: flex; flex-direction: column; height: 200px">
      <div>
        <el-button style="width: 20px; background: #f2f2f2; border: none">
          <el-icon size="20px">
            <Folder/>
          </el-icon>
        </el-button>
        <el-button style="width: 20px; background: #f2f2f2; border: none">
          <el-icon size="20px">
            <ChatLineRound/>
          </el-icon>
        </el-button>
      </div>

      <el-input type="textarea" :rows="5" resize="none" v-model="inputMessage"
                input-style="background-color: #f2f2f2; width: 750px; margin-top: 5px; box-shadow: none; resize: none;"/>

      <div style="display:flex; justify-content: flex-end">
        <el-button style="width: 100px; background: #d3e3fd; border: none;">发送</el-button>
      </div>

    </el-footer>
  </el-container>
</template>

<style scoped lang="scss">
.head {
  width: 100%;
  height: 40px;
  font-size: 20px;
  display: flex;
  border-bottom: 1px solid #ffffff;

  .head-name {
    background: #f2f2f2;
    border: none;
    font-size: 20px;
    color: black;
    margin-top: 3px;
  }

  .head-more {
    margin-left: auto;
    width: 20px;
    background: #f5f5f5;
    border: none
  }
}

.user-message-time {
  font-size: 10px;
  color: black;
}

.message-left {
  display: flex;
  overflow: auto;
  margin-top: 10px;

  .user-message {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: flex-start;
    margin-left: 5px;

    .user-message-message {
      word-wrap: break-word;
      max-width: 400px;
      background: #ffffff;
      border-radius: 10px; // 圆角
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // 阴影效果
      display: inline-block;
      position: relative; // 用于定位尾巴
      padding: 10px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20px; // 尾巴的位置，根据聊天框的方向调整
        width: 0;
        height: 0;
        border: 10px solid transparent; // 透明的边框
        border-top-color: #fff; // 尾巴的颜色与聊天框背景色相同
        border-bottom: 0;
        margin-left: -10px; // 根据需要调整尾巴的位置
      }
    }
  }
}

.message-right {
  display: flex;
  overflow: auto;
  margin-top: 10px;
  justify-content: flex-end;

  .user-message {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    justify-content: flex-end;
    margin-left: 5px;

    .user-message-message {
      word-wrap: break-word;
      max-width: 400px;
      background: #95ec69;
      border-radius: 10px; // 圆角
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); // 阴影效果
      display: inline-block;
      position: relative; // 用于定位尾巴
      padding: 10px;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20px; // 尾巴的位置，根据聊天框的方向调整
        width: 0;
        height: 0;
        border: 10px solid transparent; // 透明的边框
        border-top-color: #95ec69; // 尾巴的颜色与聊天框背景色相同
        border-bottom: 0;
        margin-left: -10px; // 根据需要调整尾巴的位置
      }
    }
  }
}

</style>
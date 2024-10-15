<script setup>
import {reactive, ref} from 'vue'
import {useRoute} from 'vue-router';
import {ChatLineRound, Folder, More} from "@element-plus/icons-vue";

const chatChatName = useRoute().query.chatSessionName
const chatChatId = useRoute().query.chatChatId

let messageData = reactive([
  {chatSessionPhoto: 'https://biao22.oss-cn-guangzhou.aliyuncs.com/6d881ab8-02af-4a5f-aa98-705a898d603c_head.jpg'}
])

let message = ref('')

</script>

<template>
  <el-container style="position: relative;">
    <el-header class="head">
      <el-button key="plain" class="head-name">{{ chatChatName }}</el-button>
      <el-button class="head-more">
        <el-icon size="20px">
          <More/>
        </el-icon>
      </el-button>

    </el-header>

    <el-main class="message-main" direction="horizontal">
      <div class="message-left" style="position: relative;">
        <el-avatar :src=messageData[0].chatSessionPhoto size="default"/>
        <div class="user-message">
          <div style="display: flex;">
            <div class="user-message-name">zhangsan</div>
            <div class="user-message-time" style="margin-left: 10px;">2022</div>
          </div>
          <div class="user-message-message">hello</div>
        </div>
      </div>

      <div class="message-right" style="position: relative;">
        <div class="user-message">
          <div style="display: flex;">
            <div class="user-message-time" style="margin-right: 10px;">2022</div>
            <div class="user-message-name">zhangsan</div>
          </div>
          <div class="user-message-message">hello</div>
        </div>
        <el-avatar :src=messageData[0].chatSessionPhoto size="default"/>
      </div>
    </el-main>

    <el-footer class="message-footer" style="display: flex; flex-direction: column">
      <div>
        <el-button style="width: 20px; background: #f5f5f5; border: none">
          <el-icon size="20px">
            <Folder/>
          </el-icon>
        </el-button>
        <el-button style="width: 20px; background: #f5f5f5; border: none">
          <el-icon size="20px">
            <ChatLineRound/>
          </el-icon>
        </el-button>
      </div>

      <el-input type="textarea" :rows="6" resize="none" v-model="message"
                input-style="background-color: #f5f5f5; width: 750px; margin-top: 5px; box-shadow: none; resize: none;"/>

      <div style="display:flex; justify-content: flex-end">
        <el-button style="width: 100px; background: #e9e9e9; border: none;">发送</el-button>
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
  border-bottom: 1px solid #e7e7e7;

  .head-name {
    background: #f5f5f5;
    border: none;
    font-size: 20px;
    color: black;
  }

  .head-more {
    margin-left: auto;
    width: 20px;
    background: #f5f5f5;
    border: none
  }
}

.message-main {
  border-bottom: 1px solid #e7e7e7;
  height: 500px;

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
}
</style>
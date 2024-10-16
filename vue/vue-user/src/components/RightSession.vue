<script setup>
import {ChatLineRound, Folder, More} from "@element-plus/icons-vue";
import {reactive, ref, defineProps} from 'vue'

const props = defineProps({
  chatSessionId:{
    type: String,
    required: true
  }
})

const inputMessage = ref('')

const chatSessionData = ref(
  {
    chatSessionName: '213123',
    messageData: reactive([
      {
        userId: '',
        photo: '',
        nickname: '12',
        content: '12312312'
      }
    ])
  }
)

console.log(1)
</script>

<template>
  <el-container style="position: relative;">
    <el-header class="head">
      <el-button key="plain" class="head-name">{{ chatSessionData.chatSessionName }}</el-button>
      <el-button class="head-more">
        <el-icon size="20px">
          <More/>
        </el-icon>
      </el-button>
    </el-header>

    <el-main class="message-main" direction="horizontal">
      <div class="message-left" style="position: relative;">
        <el-avatar :src=chatSessionData.messageData[0].chatSessionPhoto size="default"/>
        <div class="user-message">
          <div style="display: flex;">
            <div class="user-message-name">{{ chatSessionData.messageData[0].nickname }}</div>
            <div class="user-message-time" style="margin-left: 10px;">2022</div>
          </div>
          <div class="user-message-message">{{ chatSessionData.messageData[0].content }}</div>
        </div>
      </div>

      <div class="message-right" style="position: relative;">
        <div class="user-message">
          <div style="display: flex;">
            <div class="user-message-time" style="margin-right: 10px;">2022</div>
            <div class="user-message-name">{{ chatSessionData.messageData[0].nickname }}</div>
          </div>
          <div class="user-message-message">{{ chatSessionData.messageData[0].content }}</div>
        </div>
        <el-avatar :src=chatSessionData.messageData[0].chatSessionPhoto size="default"/>
      </div>
    </el-main>

    <el-footer class="message-footer" style="display: flex; flex-direction: column">
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
  border-bottom: 1px solid #f2f2f2;

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

.message-main {
  border-bottom: 1px solid #e7e7e7;
  height: 465px;

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
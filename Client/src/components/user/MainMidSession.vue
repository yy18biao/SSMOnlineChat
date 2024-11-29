<script setup>
import {Search} from "@element-plus/icons-vue";
import {defineProps} from "vue"

const props = defineProps({
  sessionDataList: {
    required: true
  },
})
</script>

<template>
  <div style="height: 100%; width: 100%">
    <el-header style="height: 10%; display: flex; justify-content: center; align-items: center;">
      <el-input v-model=searchName style="width: 80%;" placeholder="请输入会话名"/>
      <el-button type="text" style="width: 20%"><el-icon size="20px"><Search></Search></el-icon></el-button>
    </el-header>

    <div style="width: 100%; height: 90%; overflow: scroll; scrollbar-width: none;
                    display: flex; flex-direction: column; justify-content: center;
                    align-items: center">
      <el-menu class="el-menu" >
        <el-menu-item style="background-color: #e5e4e4" class="el-menu-item" v-for="(index) in sessionDataList" @click="openChatSessionRight(index)">
          <div class="photo">
            <el-avatar :src=index.chatSessionPhoto size="large"/>
          </div>
          <div class="session-main">
            <span class="name">{{ index.chatSessionName }}</span>
            <span class="message">{{ index.chatSessionLastMessage }}</span>
          </div>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<style scoped lang="scss">
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
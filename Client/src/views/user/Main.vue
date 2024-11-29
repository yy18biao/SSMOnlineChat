<template>
  <el-container class='common-layout'>
    <el-header class="el-header">
      <el-dropdown>
      <span class="el-dropdown__box">
              <strong>当前用户：</strong>
            <span style="width: 20px; color: black" id="loginUserNickname"></span>{{ loginUser.nickname }}
          <el-icon><ArrowDownBold/></el-icon>
      </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-main style="display: flex; justify-content: center; align-items: center;">
      <div class="main-main">
        <div class="left">
          <main-left :photo="loginUser.photo"></main-left>
        </div>

        <div class="mid">
          <main-mid-session v-if="midFlag === 1" :session-data-list="sessionDataList"></main-mid-session>
        </div>

        <div class="right">

        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>
import {reactive, ref} from "vue";
import {SwitchButton} from "@element-plus/icons-vue";
import {logoutService} from "@/apis/user/user.js";
import {removeToken} from "@/utils/cookie.js";
import router from "@/router/index.js";
import MainLeft from "@/components/user/MainLeft.vue";
import MainMidSession from "@/components/user/MainMidSession.vue";

// 用户个人信息
const loginUser = reactive({
  userId: "",
  nickname: '',
  photo: '',
  email: '',
  introduce: '',
  phone: '',
})

// 中间框的标记
const midFlag = ref(1)

// 所有聊天会话列表
const sessionDataList = reactive([])

// 退出登录
async function logout() {
  // 通知后端移除 websocket记录
  // await deleteWebSocketService()

  await logoutService()
  removeToken() // 移除 token
  await router.push("/user/login")
}

</script>

<style scoped lang="scss">
.common-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;

  .el-header {
    background-color: #d3e3fd;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 4%;

    .el-dropdown__box {
      display: flex;
      align-items: center;

      .el-icon {
        color: #4c4141;
        margin-left: 20px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .main-main {
    width: 50%;
    height: 90%;
    display: flex;

    .left{
      height: 100%;
      width: 7%;
      background-color: #2e2e2e;
    }

    .mid{
      height: 100%;
      width: 23%;
      background-color: #f7f7f7;
    }

    .right{
      height: 100%;
      width: 70%;
      background-color: #f5f5f5;
    }
  }
}
</style>
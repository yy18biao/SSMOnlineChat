<script setup>
import {
  Tools,
  ChatSquare,
  User,
  SwitchButton,
  ArrowDownBold
} from '@element-plus/icons-vue'
import {reactive} from 'vue'
import router from '@/router'
import {getToken, removeToken} from '@/utils/cookie';
import {ElMessageBox} from "element-plus";
import {getUserService, logoutService} from "@/apis/user.js";

let loginUser = reactive({
  nickname: '',
  photo: ''
})

async function getUser() {
  const user = await getUserService();
  loginUser.nickname = user.data.nickname;
  loginUser.photo = user.data.photo;
}

getUser()

async function logout() {
  // 消息提示弹窗
  await ElMessageBox.confirm(
      '确认退出',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '退出',
        type: 'warning',
      }
  )

  // 调用退出登录服务
  await logoutService()
  // 删除已有token
  removeToken()
  // 跳转回登录页面
  router.push("/user/login")
}
</script>

<template>
  <div class="common-layout">
    <el-container direction="vertical">
      <el-header class="el-header">
        <el-dropdown>
      <span class="el-dropdown__box">
          <div>
              <strong>当前用户：</strong>
            <span style="width: 20px"></span>{{ loginUser.nickname }}
          </div>
          <el-icon><ArrowDownBold/></el-icon>
      </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <el-container class="layout-container">
          <el-main class="layout-bottom-box">
            <div class="left">
              <el-aside class="el-aside">
                <el-menu class="el-menu" router>
                  <el-menu-item>
                    <el-avatar :src="loginUser.photo" size="100px"/>
                  </el-menu-item>
                  <el-menu-item index="/user/messageSession">
                    <el-icon size="60px">
                      <ChatSquare/>
                    </el-icon>
                  </el-menu-item>
                  <el-menu-item index="/user/friend">
                    <el-icon size="60px">
                      <User/>
                    </el-icon>
                  </el-menu-item>
                  <el-menu-item index="/user/setting">
                    <el-icon size="60px">
                      <Tools/>
                    </el-icon>
                  </el-menu-item>
                </el-menu>
              </el-aside>
            </div>
            <div class="right">
              <RouterView/>
            </div>
          </el-main>
        </el-container>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped lang="scss">
.common-layout {
  .el-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 40px;

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

  .main {
    display: flex;
    justify-content: center;
    align-items: center;


    .layout-container {
      height: 800px;
      width: 100px;
      background-image: url("../assets/images/background.png");

      .layout-bottom-box {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;
        width: 100%;
        overflow: hidden;

        .left {
          width: 80px;
          height: 100%;
          background: #c7dff6;

          .el-aside {
            height: 100%;
            width: 100%;
            background: #c7dff6;

            .el-menu {
              height: 100%;
              width: 100%;
              display: flex;
              flex-direction: column;
              background: #c7dff6;

              .el-menu-item {
                height: 60px;
                width: 100%;
                margin-top: 20px;
                display: flex;
                align-items: center;
                justify-content: center;
              }
            }
          }
        }
      }

      .right {
        width: 1000px;
        height: 760px;
        background: #e1dfde;
      }
    }
  }
}
</style>
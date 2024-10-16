<script setup>

import {ArrowDownBold, ChatSquare, SwitchButton, User, Tools, Plus} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import MidSession from "@/components/MidSession.vue";
import RightSession from "@/components/RightSession.vue";
import MidFriend from "@/components/MidFriend.vue";
import RightFriend from "@/components/RightFriend.vue";
import {getUserService, logoutService} from "@/apis/user.js";
import {removeToken} from "@/utils/cookie.js";
import router from "@/router/index.js";

// 中间框的启动标志 1 聊天会话 2 好友列表
const midFlag = ref(1)
// 右侧框的启动标志 1 聊天会话详情 2 好友详情
const rightFlag = ref(0)
// 当前聊天会话的id
const curChatSessionId = ref('')
// 当前选中好友的数据
let curFriendData = reactive({})
// 中间好友列表子组件实例
const midFriendRef = ref(null)

// 开启中间聊天会话
async function openMidSession() {
  midFlag.value = 1
  rightFlag.value = 0
}

// 开启中间好友列表
async function openMidFriend() {
  midFlag.value = 2
  rightFlag.value = 0
}

// 开启中间聊天会话
async function openRightSession() {
  rightFlag.value = 1
}

// 个人设置开启标志
const drawer = ref(false)

// 开启个人设置
async function openSet() {
  drawer.value = true
}

// 修改密码开启标志
const updateDialogFlag = ref(false)

// 用户个人信息
const loginUser = reactive({
  nickname: '',
  photo: '',
  email: '',
  introduce: '',
  phone: '',
  code: ''
})

let updateUser = reactive({
  nickname: '',
  photo: '',
  email: '',
  introduce: '',
  phone: '',
  code: ''
})

// 获取个人信息
async function getUser() {
  const user = await getUserService();
  loginUser.nickname = user.data.nickname;
  loginUser.photo = user.data.photo;
  loginUser.email = user.data.email;
  loginUser.introduce = user.data.introduce;
  loginUser.phone = user.data.phone;
  updateUser = loginUser
}

getUser()

// 退出登录
async function logout() {
  await logoutService()
  removeToken()
  await router.push("/user/login")
}

// 点击中间会话处理回调
async function handleOpenChatRight(chatMessageId) {
  curChatSessionId.value = chatMessageId;
  rightFlag.value = 1
}

// 点击中间好友处理回调
async function handleOpenFriendRight(friendData) {
  rightFlag.value = 0
  curFriendData = friendData;
  rightFlag.value = 2
}

// 修改了备注之后的回调
function handleUpdateRemark(friendId, friendName) {
  midFriendRef.value.updateFriendData(friendId, friendName)
}

// 删除好友信号回调
function handleDeleteRemark(friendData) {
  midFriendRef.value.deleteList(friendData)
  rightFlag.value = 0
}
</script>

<template>
  <el-container class='common-layout'>
    <el-header class="el-header">
      <el-dropdown>
      <span class="el-dropdown__box">
              <strong>当前用户：</strong>
            <span style="width: 20px; color: black"></span>{{ loginUser.nickname }}
          <el-icon><ArrowDownBold/></el-icon>
      </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-main style="flex: 1; display: flex; justify-content: center; align-items: center;">
      <div class="left">
        <el-menu class="el-menu">
          <el-menu-item>
            <el-upload
                action=""
                :show-file-list="false"
            >
              <img class="photo" v-if="loginUser.photo" :src="loginUser.photo"/>
              <el-icon v-else>
                <Plus/>
              </el-icon>
            </el-upload>
          </el-menu-item>
          <el-menu-item @click="openMidSession">
            <el-icon size="80px">
              <ChatSquare/>
            </el-icon>
          </el-menu-item>
          <el-menu-item @click="openMidFriend">
            <el-icon size="60px">
              <User/>
            </el-icon>
          </el-menu-item>
          <el-menu-item @click="openSet">
            <el-icon size="60px">
              <Tools/>
            </el-icon>
          </el-menu-item>
        </el-menu>
      </div>
      <div class="mid">
        <mid-session v-if="midFlag === 1" @openChatSessionRight="handleOpenChatRight"></mid-session>
        <mid-friend v-if="midFlag === 2" @openFriendRight="handleOpenFriendRight" ref="midFriendRef"></mid-friend>
      </div>
      <div class="right">
        <right-session v-if="rightFlag === 1" :chatSessionId="curChatSessionId"></right-session>
        <right-friend v-if="rightFlag === 2" :friendData="curFriendData"
                      @updateRemark="handleUpdateRemark" @deleteFriend="handleDeleteRemark"></right-friend>
      </div>
    </el-main>
  </el-container>

  <el-drawer v-model="drawer" :destroy-on-close="true" :with-header="false" size="50%">
    <div>昵称：
      <el-input v-model="updateUser.nickname" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>邮箱：
      <el-input v-model="updateUser.email" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>签名：
      <el-input v-model="updateUser.introduce" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>电话：</div>
    <div style="display: flex">
      <el-input v-model="updateUser.phone" size="default" input-style="width: 100px"></el-input>
      <el-button :type="'primary'">获取验证码</el-button>
    </div>
    <div>验证码：
      <el-input v-model="updateUser.code" size="default"
                input-style="width: 100px"></el-input>
    </div>
    <div style="margin-top: 100px">
      <el-button :type="'primary'">确认修改</el-button>
    </div>
    <div style="margin-top: 10px">
      <el-button :type="'warning'" @click="updateDialogFlag = true">修改密码</el-button>
    </div>
  </el-drawer>

  <!--  修改密码弹窗-->
  <el-dialog v-model="updateDialogFlag" title="修改密码" width="500" align-center>
    <el-input placeholder="输入当前密码"></el-input>
    <div style="display: flex">
      <el-input placeholder="输入验证码"></el-input>
      <el-button :type="'primary'">获取验证码</el-button>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button>确认修改</el-button>
        <el-button @click="updateDialogFlag = false">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

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

  .left {
    background-color: #e9e9e9;
    width: 80px;
    height: 700px;

    .el-menu {
      height: 100%;
      width: 100%;
      display: flex;
      flex-direction: column;
      background-color: #e9e9e9;

      .el-upload {
        display: flex;
        justify-content: center;
        align-content: center;
        background-color: #FFFFFF;

        .photo {
          height: 100%;
          width: 100%;
        }
      }

      .el-menu-item {
        margin-top: 20px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }

  .mid {
    height: 700px;
    width: 240px;
    background-color: #FFFFFF;
  }

  .right {
    background-color: #f2f2f2;
    height: 700px;
    width: 800px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .el-drawer {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }
}
</style>
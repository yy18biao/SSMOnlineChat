<script setup>

import {ArrowDownBold, ChatSquare, SwitchButton, User, Tools, Plus} from "@element-plus/icons-vue";
import {reactive, ref, onMounted, onUnmounted} from "vue";
import MidSession from "@/components/MidSession.vue";
import RightSession from "@/components/RightSession.vue";
import MidFriend from "@/components/MidFriend.vue";
import RightFriend from "@/components/RightFriend.vue";
import {
  getUserService,
  logoutService, updatePasswordCodeService,
  updatePasswordService,
  updatePhoneCodeService,
  updateUserService,
  deleteWebSocketService
} from "@/apis/user.js";
import {getToken, removeToken} from "@/utils/cookie.js";
import router from "@/router/index.js";
import {addChatSessionListService, getChatSessionListService, searchChatSessionService} from "@/apis/chatSession.js";
import {getMessageAllService} from "@/apis/message.js";
import {ElMessage} from "element-plus";

let txt = ref('获取验证码')
let timer = null

// 挂载关闭窗口时告诉后端需要移除对websocket的记录
onMounted(() => {
  window.addEventListener('beforeunload', deleteWebSocketService);
});

// 退出登录后移除此监听
onUnmounted(() => {
  window.removeEventListener('beforeunload', deleteWebSocketService);
});

// 中间框的启动标志 1 聊天会话 2 好友列表
const midFlag = ref(1)
// 右侧框的启动标志 1 聊天会话详情 2 好友详情
const rightFlag = ref(0)
// 个人设置开启标志
const drawer = ref(false)
// 当前聊天会话的id
const curChatSessionId = ref('')
// 当前选中好友的数据
let curFriendData = reactive({})
// 所有的会话列表
const sessionDataList = reactive([])
// 当前选中会话名
let curSessionName = ref('')
// 存放当前会话的消息
const messageData = reactive([])
// 中间好友列表子组件实例
const midFriendRef = ref(null)
// 右侧滑动条是否沉底标志
const isScrollable = ref(true)

// 开启中间聊天会话
async function openMidSession() {
  midFlag.value = 1
  rightFlag.value = 0
  sessionDataList.splice(0, sessionDataList.length)
  await getChatSessionList()
}

// 开启中间好友列表
async function openMidFriend() {
  midFlag.value = 2
  rightFlag.value = 0
}

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

// 用于修改个人信息
const updateUserData = reactive({
  nickname: '',
  email: '',
  introduce: '',
  phone: '',
  code: ''
})

// 用于修改密码
let updatePasswordData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  code: ''
})

// 赋值
function aToB(A, B) {

}

// 获取个人信息
async function getUser() {
  const user = await getUserService();
  loginUser.nickname = user.data.nickname;
  loginUser.photo = user.data.photo;
  loginUser.email = user.data.email;
  loginUser.introduce = user.data.introduce;
  loginUser.phone = user.data.phone;
  updateUserData.nickname = loginUser.nickname;
  updateUserData.email = loginUser.email;
  updateUserData.introduce = loginUser.introduce;
  updateUserData.phone = loginUser.phone;
}

getUser()

// 获取会话列表
async function getChatSessionList() {
  const chatSessions = await getChatSessionListService();

  if (chatSessions.data !== null) {
    for (let i = 0; i < chatSessions.data.length; i++) {
      let chatSessionId = chatSessions.data[i].chatSessionId
      let chatSessionName = chatSessions.data[i].chatSessionName
      if (chatSessionName !== null) {
        chatSessionName = chatSessionName.length > 15 ? chatSessionName.substring(0, 15) + '...' : chatSessionName
      }
      let chatSessionLastMessage = chatSessions.data[i].chatSessionLastMessage
      if (chatSessionLastMessage !== null) {
        chatSessionLastMessage = chatSessionLastMessage.length > 9 ? chatSessionLastMessage.substring(0, 9) + '...' : chatSessionLastMessage
      }
      let chatSessionPhoto = chatSessions.data[i].chatSessionPhoto

      sessionDataList.push({chatSessionId, chatSessionName, chatSessionLastMessage, chatSessionPhoto})
    }
  }

  // 触发一次右侧会话消息显示列表第一个
  if (sessionDataList.length > 0)
    await handleOpenChatRight(sessionDataList[0].chatSessionId, sessionDataList[0].chatSessionName)
}

if (sessionDataList.length <= 0)
  getChatSessionList()

// 退出登录
async function logout() {
  // 通知后端移除websocket记录
  await deleteWebSocketService()

  await logoutService()
  removeToken()
  await router.push("/user/login")
}

// 修改个人信息
async function updateUser() {
  await updateUserService(updateUserData)
  loginUser.nickname = updateUserData.nickname;
  loginUser.email = updateUserData.email;
  loginUser.introduce = updateUserData.introduce;
  loginUser.phone = updateUserData.phone;
  ElMessage.success("修改成功")
  updateUserData.code = ''
}

// 获取修改手机验证码
async function updatePhoneCode() {
  if (updateUserData.phone === loginUser.phone) {
    return ElMessage.error("请输入新的手机号码")
  }
  await updatePhoneCodeService(updateUserData.phone)
  txt.value = '59s'
  let num = 59
  timer = setInterval(() => {
    num--
    if (num < 1) {
      txt.value = '重新获取验证码'
      clearInterval(timer)
    } else {
      txt.value = num + 's'
    }
  }, 1000)
}

// 修改密码
async function updatePassword() {
  if (updatePasswordData.oldPassword === '') {
    return ElMessage.error("请输入当前密码")
  }
  if (updatePasswordData.newPassword === '') {
    return ElMessage.error("请输入修改密码")
  }
  if (updatePasswordData.confirmPassword === '') {
    return ElMessage.error("请再次确认密码")
  }
  if (updatePasswordData.newPassword !== updatePasswordData.confirmPassword) {
    return ElMessage.error("两次密码不一致")
  }
  if (updatePasswordData.code === '') {
    return ElMessage.error("请输入验证码")
  }
  await updatePasswordService(updatePasswordData)
  ElMessage.success("修改成功")
  updatePasswordData = {}
}

// 获取修改密码验证码
async function sendUpdatePassword() {
  await updatePasswordCodeService(loginUser.phone)
  txt.value = '59s'
  let num = 59
  timer = setInterval(() => {
    num--
    if (num < 1) {
      txt.value = '重新获取验证码'
      clearInterval(timer)
    } else {
      txt.value = num + 's'
    }
  }, 1000)
}

// 点击中间会话处理回调
async function handleOpenChatRight(chatSessionId, chatSessionName) {
  if (chatSessionId !== curChatSessionId.value) {
    curChatSessionId.value = chatSessionId
    curSessionName.value = chatSessionName
    const messageAll = await getMessageAllService(chatSessionId)
    messageData.splice(0, messageData.length)
    for (let i = 0; i < messageAll.data.length; i++) {
      messageData.push(messageAll.data[i])
    }
    // 将该对话置顶
    for (let i = 0; i < sessionDataList.length; i++) {
      if (sessionDataList[i].chatSessionId === curChatSessionId.value) {
        const [targetItem] = sessionDataList.splice(i, 1);
        curSessionName.value = targetItem.chatSessionName
        sessionDataList.unshift(targetItem);
        break;
      }
    }
  }
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

// 点击开启聊天信号回调
async function handleAddChatSession(friendId) {
  const id = await addChatSessionListService(friendId)
  curChatSessionId.value = id.data

  // 先在当前的列表中找看有没有
  let index = 0
  for (let i = 0; i < sessionDataList.length; i++) {
    if (sessionDataList[i].chatSessionId === curChatSessionId.value) {
      index = i
      break;
    }
  }
  // 找到了就将这一项移到数组开头
  if (index !== 0) {
    const [targetItem] = sessionDataList.splice(index, 1);
    curSessionName.value = targetItem.chatSessionName
    sessionDataList.unshift(targetItem);
  }
  // 没有找到就访问后端获取会话基本数据
  else {
    const newData = await searchChatSessionService(curChatSessionId.value)
    curSessionName.value = newData.data.chatSessionName
    // 插入到数组的开头
    sessionDataList.unshift(newData.data);
  }
  midFlag.value = 1
  rightFlag.value = 1
}

// websocket连接
const webSocket = new WebSocket("ws://127.0.0.1:8006/onlineChat")

// 连接建立成功
webSocket.onopen = function () {
  const req = {
    token: getToken(),
    dtoType: 'userOnline'
  }
  webSocket.send(JSON.stringify(req));
  console.log("连接成功")
}

webSocket.onmessage = function (message) {
  const resp = JSON.parse(message.data)
  console.log(resp)

  // 判断消息类型
  if (resp.respType === 'addTextMessage') {
    // 新增文本类型
    if (resp.chatSessionId === curChatSessionId.value) {
      messageData.push({
        photo: resp.messagePhoto,
        content: resp.messageContent,
        nickname: resp.messageNickname,
        userId: resp.userId,
        createTime: resp.createTime,
      })

      console.log(messageData);

      // 改变滑动条标志使滑动条沉底
      isScrollable.value = !isScrollable.value
    }

    // 找到中间会话列表中当前的会话
    // 将其置顶并且修改最近消息
    let index = -1
    for (let i = 0; i < sessionDataList.length; i++) {
      if (sessionDataList[i].chatSessionId === resp.chatSessionId) {
        index = i
        break;
      }
    }
    if (index !== -1) {
      const [targetItem] = sessionDataList.splice(index, 1);
      targetItem.chatSessionLastMessage = resp.messageContent.length > 9 ? resp.messageContent.substring(0, 9) + '...' : resp.messageContent
      sessionDataList.unshift(targetItem);
    } else {
      sessionDataList[0].chatSessionLastMessage = resp.messageContent.length > 9 ? resp.messageContent.substring(0, 9) + '...' : resp.messageContent
    }
  } else if (resp.respType === 'addFriendApply') {
    // 新增好友申请
    ElMessage({
      showClose: true,
      message: resp.msg,
      type: 'success',
    })
  } else if (resp.respType === 'addFriendApplyError') {
    // 新增好友申请错误
    ElMessage({
      showClose: true,
      message: resp.msg,
      type: 'error',
    })
  } else if (resp.respType === 'updateHead') {
    // 修改头像
    if (resp.photo !== null || resp.photo !== '') {
      loginUser.photo = resp.photo
    }
    console.log(loginUser);
  }
}

// 发送websocket请求
async function handleSendWebSocket(req) {
  webSocket.send(req)
}

/* 文件 */
const headers = ref({
  Authorization: "Bearer " + getToken(),
})

// 上传文件成功
const handleUploadSuccess = async (resp, file, fileList) => {
  const req = {
    fileName: resp.fileName,
    dtoType: 'updateHead',
    token: getToken()
  }
  // 通知后端修改用户数据
  webSocket.send(JSON.stringify(req))
}

const handleUploadError = () => {
  ElMessage({message: '图片上传失败', type: "error"});
}

</script>

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

    <el-main style="flex: 1; display: flex; justify-content: center; align-items: center;">
      <div class="left">
        <el-menu class="el-menu">
          <el-menu-item>
            <!--            TODO 头像上传-->
            <el-upload action="/api-dev/file/uploadFile" :show-file-list="false"
                       :headers="headers" :on-success="handleUploadSuccess" :on-error="handleUploadError">
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
        <mid-session v-if="midFlag === 1" @openChatSessionRight="handleOpenChatRight"
                     :chat-session-id="curChatSessionId" :session-data-list="sessionDataList"
                     :right-flag="rightFlag"/>
        <mid-friend v-if="midFlag === 2" @openFriendRight="handleOpenFriendRight" ref="midFriendRef"
                    @addFriend="handleSendWebSocket"/>
      </div>
      <div class="right">
        <right-session v-if="rightFlag === 1" :chat-session-id="curChatSessionId" :is-scrollable="isScrollable"
                       :chat-session-name="curSessionName" :message-data="messageData"
                       :login-user="loginUser" @newTextMessage="handleSendWebSocket"/>
        <right-friend v-if="rightFlag === 2" :friend-data="curFriendData"
                      @updateRemark="handleUpdateRemark" @deleteFriend="handleDeleteRemark"
                      @addChatSession="handleAddChatSession"/>
      </div>
    </el-main>
  </el-container>

  <el-drawer v-model="drawer" :destroy-on-close="true" :with-header="false" size="50%">
    <div>昵称：
      <el-input v-model="updateUserData.nickname" id="update-nickname" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>邮箱：
      <el-input v-model="updateUserData.email" id="update-email" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>签名：
      <el-input v-model="updateUserData.introduce" id="update-introduce" size="default" input-style="width: 100px"></el-input>
    </div>
    <div>电话：</div>
    <div style="display: flex">
      <el-input v-model="updateUserData.phone" id="update-phone" size="default" input-style="width: 100px"></el-input>
      <el-button :type="'primary'" @click="updatePhoneCode" id="code-button">{{ txt }}</el-button>
    </div>
    <div>验证码：
      <el-input v-model="updateUserData.code" size="default" id="update-code"
                input-style="width: 100px"></el-input>
    </div>
    <div style="margin-top: 100px">
      <el-button :type="'primary'" @click="updateUser" id="update-button">确认修改</el-button>
    </div>
    <div style="margin-top: 10px">
      <el-button :type="'warning'" @click="updateDialogFlag = true" id="switch-update-password">修改密码</el-button>
    </div>
  </el-drawer>

  <!--  修改密码弹窗-->
  <el-dialog v-model="updateDialogFlag" title="修改密码" width="500" align-center>
    <el-input placeholder="输入当前密码" v-model="updatePasswordData.oldPassword"></el-input>
    <el-input placeholder="输入修改密码" v-model="updatePasswordData.newPassword"></el-input>
    <el-input placeholder="确认修改密码" v-model="updatePasswordData.confirmPassword"></el-input>
    <div style="display: flex">
      <el-input placeholder="输入验证码" v-model="updatePasswordData.code"></el-input>
      <el-button :type="'primary'" @click="sendUpdatePassword">{{ txt }}</el-button>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="updatePassword">确认修改</el-button>
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
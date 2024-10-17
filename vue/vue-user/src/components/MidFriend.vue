<script setup>
import {reactive, ref, defineEmits, defineExpose} from "vue";
import {Plus, Search} from "@element-plus/icons-vue";
import {
  addFriendService,
  getFriendListService,
  searchUserService,
  friendApplyListService,
  agreeFriendApplyService, refuseFriendApplyService
} from "@/apis/friend.js";

// 是否开启好友搜索弹窗标志
const friendDialogFlag = ref(false);
// 是否开启好友申请列表弹窗标志
const applyDialogFlag = ref(false);

// 点击会话信号
const emit = defineEmits(['openFriendRight'])

// 触发点击会话信号处理
async function openFriendRight(friendData) {
  emit('openFriendRight', friendData);
}

const searchName = ref('')
const searchUserName = ref('')
const friendData = reactive([])
const addData = reactive({
  userId: '',
  nickname: '',
  photo: ''
})
const applyData = reactive([])

// 获取好友列表
async function getFriendList() {
  friendData.splice(0, friendData.length)
  const friends = await getFriendListService();

  for (let i = 0; i < friends.data.length; i++) {
    let friendId = friends.data[i].friendId

    let friendName = ''
    if (friends.data[i].friendName !== null && friends.data[i].friendName !== '')
      friendName = friends.data[i].friendName
    else
      friendName = friends.data[i].nickname
    friendName = friendName.length > 10 ? friendName.substring(0, 10) + '...' : friendName

    let photo = friends.data[i].photo
    let email = friends.data[i].email
    let phone = friends.data[i].phone
    let introduce = friends.data[i].introduce
    let nickname = friends.data[i].nickname
    friendData.push({friendId, friendName, email, phone, introduce, nickname, photo})
  }
}

// 修改指定好友项的数据
async function updateFriendData(friendId, friendName) {
  friendData.forEach(item => {
    if (item.friendId === friendId) {
      item.friendName = friendName;
    }
  });
}

// 搜索指定用户
async function searchUser(searchName) {
  const user = await searchUserService(searchName);
  addData.userId = user.data.userId;
  addData.photo = user.data.photo;
  addData.nickname = user.data.nickname;
}

// 添加好友
async function addFriend(friendId) {
  await addFriendService(friendId);
  alert("申请已发送")
}

// 查看好友申请列表
async function friendApplyList() {
  const friendApply = await friendApplyListService()
  for (let i = 0; i < friendApply.data.length; i++) {
    let userId = friendApply.data[i].userId
    let nickname = friendApply.data[i].nickname
    let photo = friendApply.data[i].photo

    applyData.push({userId, nickname, photo})
  }
}

friendApplyList()

// 同意好友申请
async function agreeFriendApply(index) {
  await agreeFriendApplyService(index.userId)
  applyData.splice(index, 1)
  let friendId = index.userId
  let friendName = index.nickname
  if (friendName.length > 10)
    friendName = friendName.substring(0, 10) + '...'
  let friendPhoto = index.photo
  friendData.push({friendId, friendName, friendPhoto})
}

// 拒绝好友申请
async function refuseFriendApply(index) {
  await refuseFriendApplyService(index.userId)
  applyData.splice(index, 1)
}

// 删除列表中的某一项
async function deleteList(index) {
  friendData.splice(index, 1)
}

// 将方法暴露给父组件
defineExpose({
  updateFriendData,
  deleteList
})

getFriendList()
</script>

<template>
  <el-container>
    <el-header style="height: 40px; display: flex; justify-content: center; align-items: center;">
      <el-input v-model=searchName style="width: 200px;margin-top: 20px" placeholder="请输入好友昵称"
                :prefix-icon="Search"/>
      <el-button type="primary" style="margin-top: 20px; width: 20px; margin-left: 10px"
                 @click="friendDialogFlag =  true">
        <el-icon>
          <Plus/>
        </el-icon>
      </el-button>
      <el-dialog v-model="friendDialogFlag" title="添加好友" width="500" align-center>
        <div style="display: flex">
          <el-input placeholder="请输入用户手机号码或用户账号" v-model="searchUserName"></el-input>
          <el-button type="primary" @click="searchUser(searchUserName)">
            <el-icon>
              <Search/>
            </el-icon>
          </el-button>
        </div>

        <el-menu style="height: 300px">
          <el-menu-item v-if="addData.userId !== ''" style="display: flex; justify-content: flex-start;">
            <div class="photo">
              <el-avatar :src=addData.photo size="large"/>
            </div>
            <div class="session-main">
              <div>{{ addData.nickname }}</div>
            </div>
            <el-button type="primary" @click="addFriend(addData.userId)">添加</el-button>
          </el-menu-item>
        </el-menu>
      </el-dialog>
    </el-header>

    <el-main style="height: 660px; overflow: scroll; scrollbar-width: none;">
      <el-button type="text" style="width: 100%; height: 20px" @click="applyDialogFlag = true">好友申请列表</el-button>
      <el-dialog v-model="applyDialogFlag" title="好友申请列表" width="500" align-center>
        <el-menu style="height: 300px">
          <el-menu-item class="el-menu-item" v-for="(index) in applyData">
            <div class="photo">
              <el-avatar :src=index.photo size="large"/>
            </div>
            <div class="session-main">
              <span class="name">{{ index.nickname }}</span>
            </div>
            <el-button type="primary" @click="agreeFriendApply(index)">同意</el-button>
            <el-button type="danger" @click="refuseFriendApply(index)">拒绝</el-button>
          </el-menu-item>
        </el-menu>
      </el-dialog>

      <el-menu class="el-menu" router>
        <el-menu-item class="el-menu-item" v-for="(index) in friendData" @click="openFriendRight(index)">
          <div class="photo">
            <el-avatar :src=index.photo size="large"/>
          </div>
          <div class="session-main">
            <span class="name">{{ index.friendName }}</span>
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
      justify-content: center;
      align-items: center;
      margin-left: 10px;

      .name {
        height: 100%;
        width: 150px;
        text-align: left;
        display: flex;
        justify-content: flex-start;
        align-items: center;
        font-size: 20px;
      }
    }
  }
}
</style>
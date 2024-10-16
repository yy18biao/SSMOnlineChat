<script setup>

import {ChatDotSquare, Delete, Edit} from "@element-plus/icons-vue";
import {defineProps, reactive, ref, defineEmits, defineExpose, watch} from "vue";
import {deleteFriendService, searchFriendService, updateRemarkService} from "@/apis/friend.js";

// 输入框的禁用标志
const isDisabled = ref(true)

// 信号
const emit = defineEmits(['updateRemark', "deleteFriend"]);

const props = defineProps({
  friendData:{
    required: true
  }
})

async function deleteFriend(friendData) {
  if(confirm("确定删除？")) {
    await deleteFriendService(friendData.friendId);
    emit("deleteFriend", friendData)
  }
}

function updateRemark(friendName, friendId) {
  updateRemarkService({friendName, friendId})
  isDisabled.value = true
  // 发送信号给父组件
  emit('updateRemark', friendId, friendName);
}
</script>

<template>
  <div class="friend-data">
    <el-avatar :src=friendData.photo shape="square" size="large"></el-avatar>
    <div class="friend-right">
      <div class="remark">
        <el-input size="default" v-model="friendData.friendName" :disabled="isDisabled"
                  input-style="background-color: #f2f2f2; font-size:15px; border: none; width: 180px;"
                  @change="updateRemark(friendData.friendName, friendData.userId)"/>
        <el-button style="background-color: #f2f2f2; border: none" @click="isDisabled = false">
          <el-icon size="20px">
            <Edit/>
          </el-icon>
        </el-button>
      </div>
      <div style="display: flex">昵称：<div>{{ friendData.nickname }}</div></div>
      <div style="display: flex">账号：<div>{{ friendData.friendId }}</div></div>
      <div style="display: flex">电话：<div>{{ friendData.phone }}</div></div>
      <div style="display: flex">邮箱：<div>{{ friendData.email }}</div></div>
      <div style="display: flex">签名：<div style="max-width: 100px;">{{ friendData.introduce }}</div></div>
      <div class="button">
        <el-button type="primary" style="width: 40px; height: 40px; border: none; margin-right: auto">
          <el-icon size="40px">
            <ChatDotSquare/>
          </el-icon>
        </el-button>
        <el-button type="danger" style="width: 40px; height: 40px; border: none; margin-left: auto" @click="deleteFriend(friendData)">
          <el-icon size="40px">
            <Delete/>
          </el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.friend-data {
  display: flex;

  .el-avatar {
    height: 60px;
    width: 60px;
  }

  .friend-right {
    margin-left: 10px;
    display: flex;
    flex-direction: column;

    .remark {
      display: flex;
    }
  }
}

.button{
  margin-top: 40px;
  display: flex;
}
</style>
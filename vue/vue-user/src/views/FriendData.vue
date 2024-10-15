<script setup>
import {ChatDotSquare, Delete, Edit} from "@element-plus/icons-vue";
import {updateRemarkService} from "@/apis/friend.js";
import {useStore} from "vuex";

const store = useStore()

function updateRemark(friendName, friendId) {
  updateRemarkService({friendName, friendId})
}

</script>

<template>
  <router-view :key="key" />
  <el-container>
    <el-header></el-header>
    <el-main class="main">
      <el-container class="data">
        <el-aside class="photo">
          <el-avatar :src=store.state.friendData.photo shape="square" size="large"></el-avatar>
        </el-aside>
        <el-main class="data-main">
          <el-input class="remark" size="default" v-model="store.state.friendData.friendName" :suffix-icon="Edit"
                    input-style="background-color: #f5f5f5; font-size:20px; border: none; outline: none;"
                    @change="updateRemark(store.state.friendData.friendName, store.state.friendData.friendId)"/>
          <div class="other">昵称：{{ store.state.friendData.nickname }}</div>
          <div class="other">账号：{{ store.state.friendData.userId }}</div>
          <div class="other">邮箱：{{ store.state.friendData.email }}</div>
        </el-main>
      </el-container>

      <div class="desc">
        <el-text size="large">个性签名</el-text>
        <el-text size="default" style="max-width: 260px; margin-left: 5px">{{ store.state.friendData.introduce }}</el-text>
      </div>

      <div class="button">
        <el-button type="primary" style="width: 40px; height: 40px; border: none; margin-right: auto">
          <el-icon size="40px">
            <ChatDotSquare/>
          </el-icon>
        </el-button>
        <el-button type="danger" style="width: 40px; height: 40px; border: none; margin-left: auto">
          <el-icon size="40px">
            <Delete/>
          </el-icon>
        </el-button>
      </div>
    </el-main>
  </el-container>
</template>

<style scoped lang="scss">
.main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .data {
    justify-content: center;
    align-items: center;
    border-bottom: 1px solid #e7e7e7;

    .photo {
      width: 60px;
      height: 60px;
    }
  }

  .desc {
    width: 360px;
    height: 135px;
    display: flex;
    border-bottom: 1px solid #e7e7e7;
  }

  .button {
    margin-top: 20px;
    width: 360px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
}
</style>
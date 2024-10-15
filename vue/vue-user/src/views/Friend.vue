<script setup>

import {Search} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {getFriendListService, searchFriendService} from "@/apis/friend.js";
import {useStore} from 'vuex';
import router from "@/router/index.js";

const store = useStore();
const searchName = ''
let friendList = reactive([])

async function getFriendList() {
  const friends = await getFriendListService();

  for (let i = 0; i < friends.data.length; i++) {
    let friendId = friends.data[i].friendId
    let friendName = friends.data[i].friendName
    let friendPhoto = friends.data[i].friendPhoto

    friendList.push({friendId, friendName, friendPhoto})
  }
  return friendList
}

getFriendList()

async function search(friendId) {
  const friend = await searchFriendService(friendId)
  console.log(friend)
  store.commit('setFriendData', friend.data)
}

async function toFriendData(friendId) {
  await search(friendId)
  await router.push("/user/friendData")
}

</script>

<template>
  <div class="main">
    <el-container direction="horizontal" style="position: relative;">
      <el-aside width="240px" class="mid">
        <el-container direction="vertical">
          <el-header class="el-header">
            <el-input v-model=searchName style="width: 200px;margin-top: 20px" placeholder="请输入昵称" :prefix-icon="Search"/>
          </el-header>

          <el-main class="mid-box" direction="vertical">
            <el-menu class="el-menu" router>
              <el-menu-item class="el-menu-item" v-for="(index) in friendList"
                            @click="toFriendData(index.friendId)">
                <el-container class="session" direction="horizontal">
                  <el-aside class="session-left">
                    <el-avatar :src=index.friendPhoto size="large" shape="square"/>
                  </el-aside>
                  <el-main class="session-main">
                    <div class="name">{{ index.friendName }}</div>
                  </el-main>
                </el-container>
              </el-menu-item>
            </el-menu>
          </el-main>
        </el-container>
      </el-aside>

      <el-main>
        <div class="right">
          <RouterView/>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<style scoped lang="scss">
.mid {
  height: 760px;
  display: flex;
  flex-direction: column;

  .mid-box {
    width: 240px;
    position: relative;
    flex-grow: 1;
    background: #f7f7f7;

    .el-menu {
      height: 100px;
      width: 240px;
      position: absolute; // 子组件设置为绝对定位
      left: 0;
      background: #f7f7f7;

      .el-menu-item {
        height: 100px;
        width: 240px;
        position: relative;
      }

      .session {
        height: 100px;
        width: 240px;
        position: absolute; // 子组件设置为绝对定位
        left: 0;

        .session-left {
          height: 100px;
          width: 80px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .session-main {
          height: 100%;
          width: 180px;
          position: relative;

          .name {
            height: 30px;
            width: 150px;
            text-align: left;
            position: absolute; // 子组件设置为绝对定位
            left: 0;
            top: auto;
            font-size: 13px;
          }
        }
      }
    }
  }
}

.right {
  width: 800px;
  height: 760px;
  background: #f5f5f5;
  position: absolute; // 子组件设置为绝对定位
  left: 240px;
  top: 0;
}
</style>
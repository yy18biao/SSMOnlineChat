<script setup>
import {Plus, ChatSquare, User, Tools} from "@element-plus/icons-vue";
import {defineProps} from "vue";

const props = defineProps({
  photo: {
    type: String
  }
})
</script>

<template>
  <el-menu class="el-menu">
    <el-menu-item class="el-menu-item">
      <el-upload action="/api-dev/user/uploadFile" :show-file-list="false"
                 :headers="headers" :on-success="handleUploadSuccess" :on-error="handleUploadError">
        <img class="photo" v-if="props.photo" :src="props.photo" alt=""/>
        <el-icon v-else>
          <Plus/>
        </el-icon>
      </el-upload>
    </el-menu-item>
    <el-menu-item @click="openMidSession" class="el-menu-item">
      <el-icon size="80px">
        <ChatSquare/>
      </el-icon>
    </el-menu-item>
    <el-menu-item @click="openMidFriend" class="el-menu-item">
      <el-icon size="80px">
        <User/>
      </el-icon>
    </el-menu-item>
    <el-menu-item @click="openSet" class="el-menu-item">
      <el-icon size="80px">
        <Tools/>
      </el-icon>
    </el-menu-item>
  </el-menu>
</template>

<style scoped lang="scss">
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

    .el-menu-item {
      margin-top: 10%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
}
</style>
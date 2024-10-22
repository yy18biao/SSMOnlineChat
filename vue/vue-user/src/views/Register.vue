<template>
  <div class="reg-page">
    <div class="main">
      <!-- logo层 -->
      <div class="logo">chatOJ</div>

      <el-button @click="router.push('/user/login')">返回</el-button>
      <el-input v-model="mobileForm.phone" type="text" placeholder="请输入手机号"/>
      <el-input v-model="mobileForm.code" type="text" placeholder="请输入验证码"/>
      <el-input v-model="mobileForm.password" type="password" placeholder="请输入密码"/>
      <el-input v-model="mobileForm.aPassword" type="password" placeholder="请确认密码"/>
      <el-button type="primary" @click="getCode">{{txt}}</el-button>
      <el-button type="primary" @click="reg">注册</el-button>
    </div>
  </div>
</template>

<script setup>
import router from '@/router'
import {ref, reactive} from 'vue'
import {sendRegCodeService, regService} from "@/apis/login.js";
import {ElMessage} from "element-plus";

let mobileForm = reactive({
  password: '',
  aPassword: '',
  phone: '',
  code: ''
})
let txt = ref('获取验证码')
let timer = null

async function getCode(){
  await sendRegCodeService(mobileForm)
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

async function reg(){
  if(mobileForm.phone === ''){
    ElMessage.error("请输入手机号码")
    return
  }else if(mobileForm.code === ''){
    ElMessage.error("请输入验证码")
    return
  }else if(mobileForm.password === ''){
    ElMessage.error("请输入密码")
    return
  }else if(mobileForm.aPassword === ''){
    ElMessage.error("请再次确认密码")
    return
  }else if(mobileForm.aPassword !== mobileForm.password){
    ElMessage.error("密码不一致")
    return
  }

  try {
    await regService(mobileForm)
    router.push("/user/login")
  }catch (error) {
  }
}
</script>

<style scoped lang="scss">
.reg-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 98vh;
  background-image: url(../assets/images/background.png);
  background-repeat: no-repeat;
  background-position: center center;
  background-size: cover;

  .main {
    width: 30%;
    height: 70%;
    border-style: solid;
    border-width: 5px;
    border-color: rgba(250, 252, 255);
    display: flex;
    flex-direction: column;

    .logo {
      width: 100%;
      height: 10%;
      text-align: center;
      color: rgba(24, 144, 255);
      font-size: 40px;
      /*设置元素保留3D位置*/
      -webkit-transform-style: preserve-3d;
      /*设置动画*/
      -webkit-animation: run ease-in-out 6s infinite;
    }

    @-webkit-keyframes run {
      /*创建动画*/
      0% {
        -webkit-transform: rotateX(-5deg) rotateY(0);
      }

      50% {
        -webkit-transform: rotateX(0) rotateY(180deg);
      }

      100% {
        -webkit-transform: rotateX(5deg) rotateY(360deg);
      }
    }

    .el-input {
      margin-left: calc(50% - 175px);
      width: 350px;
      font-family: PingFangSC, PingFang SC;
      font-weight: 400;
      font-size: 16px;
      color: #222222;
      margin-top: 40px;
    }

    .el-input__wrapper {
      border: none;
      box-shadow: none;
      background: transparent;
      width: 230px;
      padding-left: 0;
    }

    .el-button{
      width: 350px;
      margin-top: 40px;
      margin-left: calc(50% - 175px);
    }
  }
}
</style>
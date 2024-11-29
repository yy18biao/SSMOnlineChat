<template>
  <div class="login-page">
    <div class="main">
      <!-- logo层 -->
      <div class="logo">onlineChat登录</div>
      <el-button type="text" class="push-admin-button">转至管理员登录</el-button>
      <!-- 选择层 -->
      <div class="switch">
        <el-button text class="switch-pass" @click="switchPass">密码登录</el-button>
        <el-button text class="switch-code" @click="switchCode">验证码登录</el-button>
      </div>

      <div class="input">
        <el-input v-model="mobileForm.phone" type="text" class="phone-input" id="phone-input" placeholder="请输入手机号码"/>
        <el-input v-model="mobileForm.password" type="password" class="pass-input" id="pass-input"
                  v-if="!flag" placeholder="请输入密码"/>
        <div v-if="flag">
          <el-input v-model="mobileForm.code" type="password" class="code-input" id="code-input"
                    placeholder="请输入验证码"/>
          <el-button type="primary" class="code-button" id="code-button" @click="getCode">{{ txt }}</el-button>
        </div>

        <el-button type="primary" class="login-button" id="login-button" @click="login">登录</el-button>
        <el-button type="primary" class="reg-button" id="reg-button" @click="router.push('/user/reg')">注册</el-button>

      </div>

      <div class="bottom">
        <p>注册或点击登录代表您同意 <span>服务条款</span> 和 <span>隐私协议</span></p>
      </div>
    </div>
  </div>
</template>


<script setup>
import {ref, reactive} from 'vue'
import {setToken, setUserId} from '@/utils/cookie'
import router from '@/router'
import {codeLoginService, passLoginService, sendLoginCodeService} from "@/apis/user/login.js";

const flag = ref(false)
const phoneRegex = /^1[345789]\d{9}$/;
let mobileForm = reactive({
  password: '',
  phone: '',
  code: ''
})
let txt = ref('获取验证码')
let timer = null

async function switchPass() {
  if (!flag.value) return;

  flag.value = false;
  document.querySelector(".switch-pass").style.color = "rgb(76,154,255)";
  document.querySelector(".switch-pass").style.borderBottom = "2px solid rgba(24,144,255)";
  document.querySelector(".switch-code").style.color = "black";
  document.querySelector(".switch-code").style.borderBottom = "none";
}

async function switchCode() {
  if (flag.value) return;

  flag.value = true;
  document.querySelector(".switch-code").style.color = "rgb(76,154,255)";
  document.querySelector(".switch-code").style.borderBottom = "2px solid rgba(24,144,255)";
  document.querySelector(".switch-pass").style.color = "black";
  document.querySelector(".switch-pass").style.borderBottom = "none";
}

async function getCode() {
  if (mobileForm.phone === null || mobileForm.phone === '')
    return ElMessage.error("请输入手机号码")
  else if (!phoneRegex.test(mobileForm.phone))
    return ElMessage.error("手机号码格式错误")

  await sendLoginCodeService(mobileForm.phone)
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

async function login() {
  if (!flag.value) {
    // 密码登录
    // 判空
    if (mobileForm.phone === null || mobileForm.phone === '')
      return ElMessage.error("请输入手机号码")
    else if (!phoneRegex.test(mobileForm.phone))
      return ElMessage.error("手机号码格式错误")
    if (mobileForm.password === null || mobileForm.password === '')
      return ElMessage.error("请输入密码")

    const loginRef = await passLoginService(mobileForm)
    setToken(loginRef.data[0])
    setUserId(loginRef.data[1])
    await router.push("/user/main")
  } else {
    // 验证码登录
    if (mobileForm.phone === null || mobileForm.phone === '')
      return ElMessage.error("请输入手机号码")
    else if (!phoneRegex.test(mobileForm.phone))
      return ElMessage.error("手机号码格式错误")
    if (mobileForm.code === null || mobileForm.code === '')
      return ElMessage.error("请输入验证码")

    const loginRef = await codeLoginService(mobileForm)
    setToken(loginRef.data[0])
    setUserId(loginRef.data[1])
    await router.push("/user/main")
  }
}

async function reg() {
  await router.push("/user/register")
}
</script>


<style lang="scss" scoped>
.login-page {
  height: 100%;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

  .main {
    width: 30%;
    height: 70%;
    border-style: solid;
    border-width: 5px;
    border-color: rgba(250, 252, 255);
    position: relative;

    .logo {
      width: 100%;
      height: 10%;
      margin-top: 10px;
      text-align: center;
      color: rgba(24, 144, 255);
      font-size: 40px;
    }

    .push-admin-button {
      font-size: 15px;
      color: #222222;
    }

    .switch {
      margin-top: 20px;
      width: 100%;
      height: 10%;
      display: flex;

      .switch-pass {
        width: 50%;
        height: 100%;
        text-align: center;
        font-size: 20px;
        border-bottom: 2px solid rgba(24, 144, 255);
      }

      .switch-code {
        width: 50%;
        height: 100%;
        text-align: center;
        font-size: 20px;
      }
    }

    .input {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .phone-input, .pass-input {
        width: 350px;
        font-weight: 400;
        font-size: 16px;
        color: #222222;
        margin-top: 40px;
      }

      .code-input {
        width: 250px;
        font-weight: 400;
        font-size: 16px;
        color: #222222;
        margin-top: 40px;
      }

      .code-button {
        width: 100px;
        font-weight: 400;
        font-size: 16px;
        margin-top: 40px;
      }

      .login-button {
        width: 350px;
        margin-top: 40px;
      }

      .reg-button {
        width: 100px;
        height: 30px;
        margin-top: 20px;
        margin-left: 250px;
      }
    }


    .bottom {
      position: absolute;
      bottom: 0;
      text-align: center;
      width: 100%;
      height: 50px;
      font-weight: 400;
      font-size: 14px;
      line-height: 50px;

      p {
        margin: 0;
      }

      span {
        color: #32C5FF;
        cursor: pointer;
      }
    }
  }
}
</style>
<template>
  <div class="login-page">
    <div class="main">
      <!-- logo层 -->
      <div class="logo">chatChat</div>

      <!-- 选择层 -->
      <div class="tab" id="tab">
        <el-button text class="login-pass" @click="switchPass">密码登录</el-button>
        <el-button text class="login-phone" @click="switchPhone">验证码登录</el-button>
      </div>

      <!-- 密码登录层 默认 -->
      <div class="pass-input" >
        <el-input v-model="mobileForm.phone" type="text" placeholder="请输入手机号码"/>
        <el-input v-model="mobileForm.password" type="password" placeholder="请输入密码"/>
        <el-button type="primary" class="forgetPass" @click="">忘记密码?</el-button>
        <el-button type="primary" class="login-button" @click="loginPass">登录</el-button>
        <el-button type="primary" class="reg-button" @click="reg">注册账户</el-button>
      </div>

      <!-- 验证码登录层 -->
      <div class="phone-input" style="display: none">
        <el-input v-model="mobileForm.phone" type="text" placeholder="请输入手机号码"/>
        <div class="verification">
          <el-input style="width:230px" v-model="mobileForm.code" type="text" placeholder="请输入验证码"/>
          <el-button type="primary" class="verification-button" @click="getCode">{{txt}}</el-button>
        </div>
        <el-button type="primary" class="login-button" @click="loginPhone">登录</el-button>
        <el-button type="primary" class="reg-button" @click="reg">注册账户</el-button>
      </div>

      <!-- 尾段功能层 -->
      <div class="gray-bot">
        <p>注册或点击登录代表您同意 <span>服务条款</span> 和 <span>隐私协议</span></p>
      </div>
    </div>
  </div>
</template>


<script setup>
import {ref, reactive} from 'vue'
import {setToken} from '@/utils/cookie'
import {sendLoginCode, codeLoginService, passLoginService} from '@/apis/login'
import router from '@/router'

let mobileForm = reactive({
  password: '',
  phone: '',
  code: ''
})
let txt = ref('获取验证码')
let timer = null

async function switchPhone() {
  document.querySelector(".pass-input").style.display = "none";
  document.querySelector(".phone-input").style.display = "";
  document.querySelector(".login-phone").style.color = "rgb(76,154,255)";
  document.querySelector(".login-phone").style.borderBottom = "2px solid rgba(24,144,255)";
  document.querySelector(".login-pass").style.color = "black";
  document.querySelector(".login-pass").style.borderBottom = "none";
}

async function switchPass() {
  document.querySelector(".pass-input").style.display = "";
  document.querySelector(".phone-input").style.display = "none";
  document.querySelector(".login-pass").style.color = "rgb(76,154,255)";
  document.querySelector(".login-pass").style.borderBottom = "2px solid rgba(24,144,255)";
  document.querySelector(".login-phone").style.color = "black";
  document.querySelector(".login-phone").style.borderBottom = "none";
}

async function getCode() {
  await sendLoginCode(mobileForm)
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

async function loginPass() {
  try {
    const loginRef = await passLoginService(mobileForm)
    setToken(loginRef.data)
    router.push("/user/main")
  }catch (error) {
    console.log("error:", error)
  }
}

async function loginPhone() {
  try {
    const loginRef = await codeLoginService(mobileForm)
    setToken(loginRef.data)
    router.push("/user/main")
  }catch (error) {
    console.log("error:", error)
  }
}

async function reg(){
  await router.push("/user/register")
}
</script>


<style lang="scss" scoped>
.login-page {
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

    .gray-bot {
      position: absolute;
      left: 0;
      text-align: center;
      margin-top: 156px;
      width: 100%;
      height: 50px;
      font-family: PingFangSC, PingFang SC;
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

    .logo {
      width: 100%;
      height: 10%;
      margin-top: 10px;
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

    .tab {
      /*设置元素上外边距*/
      margin-top: 20px;
      width: 100%;
      height: 10%;
      display: flex;
    }

    .login-pass {
      width: 50%;
      height: 100%;
      text-align: center;
      font-size: 20px;
      border-bottom: 2px solid rgba(24, 144, 255);
    }

    .login-phone {
      width: 50%;
      height: 100%;
      text-align: center;
      font-size: 20px;
    }

    .pass-input {
      width: 100%;
      height: 40%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .el-input {
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
    }

    .phone-input {
      width: 100%;
      height: 40%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .verification {
        margin-top: 30px;
        height: 20%;
        width: 60%;
        margin-left: auto;
        margin-right: auto;
      }

      .verification-button {
        height: 100%;
        width: 30%;
        border-radius: 5px;
        border-style: solid;
        border-width: 1px;
      }

      .el-input {
        width: 350px;
        font-family: PingFangSC, PingFang SC;
        font-weight: 400;
        font-size: 16px;
        color: #222222;
        margin-top: 10px;
      }

      .el-input__wrapper {
        border: none;
        box-shadow: none;
        background: transparent;
        width: 230px;
        padding-left: 0;
      }
    }
  }

  .forgetPass {
    margin-top: 10px;
    width: 100px;
    margin-left: 63%;
  }

  .login-button {
    margin-top: 10px;
    height: 30px;
    width: 60%;
    border-radius: 5px;
    margin-left: auto;
    margin-right: auto;
    border-width: 1px;
  }

  .reg-button {
    margin-top: 10px;
    border-style: none;
    margin-left: 63%;
    width: 100px;
  }
}
</style>
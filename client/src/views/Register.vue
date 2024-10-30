<template>
    <div class="reg-page">
        <div class="main">
            <!-- logo层 -->
            <div class="logo">onlineChat注册</div>
            <el-button type="text" class="return-login" id="return-login" @click="router.push('/user/login')">返回</el-button>

            <div class="input">
                <el-input v-model="mobileForm.phone" type="text" class="phone-input" id="phone-input" placeholder="请输入手机号码"/>
                <div>
                    <el-input v-model="mobileForm.code" type="password" class="code-input" id="code-input" placeholder="请输入验证码"/>
                    <el-button type="primary" class="code-button" id="code-button" @click="getRegCode">{{ txt }}</el-button>
                </div>
                <el-input v-model="mobileForm.password" type="password" class="pass-input" id="pass-input" placeholder="请输入密码"/>
                <el-input v-model="mobileForm.aPassword" type="password" class="again-pass-input" id="again-pass-input"
                          placeholder="请确认密码"/>
                <el-button type="primary" class="reg-button" id="reg-button" @click="reg">注册</el-button>
            </div>

            <div class="bottom">
                <p>注册或点击登录代表您同意 <span>服务条款</span> 和 <span>隐私协议</span></p>
            </div>
        </div>
    </div>
</template>

<script setup>
import router from "@/router/index.js";
import {reactive, ref} from "vue";
import {regService, sendRegCodeService} from "@/apis/login.js";

let txt = ref('获取验证码')
let timer = null
const phoneRegex = /^1[3-9]\d{9}$/;
const mobileForm = reactive({
    phone: '',
    password: '',
    aPassword: '',
    code: ''
})

// 获取注册验证码
async function getRegCode() {
    if (mobileForm.phone === null || mobileForm.phone === '')
        return ElMessage.error("请输入手机号码")
    else if (!phoneRegex.test(mobileForm.phone))
        return ElMessage.error("手机号码格式错误")

    await sendRegCodeService(mobileForm.phone)
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

// 注册
async function reg(){
    if (mobileForm.phone === null || mobileForm.phone === '')
        return ElMessage.error("请输入手机号码")
    else if (!phoneRegex.test(mobileForm.phone))
        return ElMessage.error("手机号码格式错误")
    if (mobileForm.code === null || mobileForm.code === '')
        return ElMessage.error("请输入验证码")
    if (mobileForm.password === null || mobileForm.password === '')
        return ElMessage.error("请输入密码")
    if (mobileForm.aPassword === null || mobileForm.aPassword === '')
        return ElMessage.error("请确认密码")
    else if(mobileForm.aPassword !== mobileForm.password)
        return ElMessage.error("两次密码不一致")

    await regService(mobileForm)
    await router.push("/user/login")
}

</script>

<style lang="scss" scoped>
.reg-page {
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

    .return-login {
      font-size: 15px;
      color: #222222;
    }

    .input {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .phone-input, .pass-input, .nickname-input, .email-input, .again-pass-input {
        width: 350px;
        font-weight: 400;
        font-size: 16px;
        color: #222222;
        margin-top: 30px;
      }

      .code-input {
        width: 250px;
        font-weight: 400;
        font-size: 16px;
        color: #222222;
        margin-top: 30px;
      }

      .code-button {
        width: 100px;
        font-weight: 400;
        font-size: 16px;
        margin-top: 30px;
      }

      .reg-button {
        width: 350px;
        margin-top: 30px;
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
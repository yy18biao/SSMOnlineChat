<template>

    <div class="login-main">
        <div class="login-box" v-loading="loading">
            <div class="logo-box">OnlineChat后台管理</div>
            <div class="form-box">
                <div class="form-item">
                    <img src="@/assets/images/userId.png">
                    <el-input v-model="userId" type="text" placeholder="请输入账号" />
                    <span class="error-tip" v-if="showErrorTip && !form.userAccount">账号不能为空</span>
                </div>
                <div class="form-item">
                    <img src="@/assets/images/password.png">
                    <el-input v-model="password" type="password" show-password placeholder="请输入密码" />
                    <span class="error-tip" v-if="showErrorTip && !form.password">密码不能为空</span>
                </div>
                <div class="submit-box" @click="loginFun">登录</div>
            </div>
        </div>
    </div>

</template>


<script setup>

import { ref } from 'vue'
import { loginService } from '@/apis/admin'
import router from '@/router'
import { setToken } from '@/utils/cookie';

const userId = ref('')
const password = ref('')

// 点击按钮调用方法
async function loginFun() {
    try {
        // 发起请求
        const res = await loginService(userId.value, password.value)
        router.push("/admin/user") // 路由
        setToken(res.data) // 设置token
    } catch (error) {
        console.log("error:", error)
    }
}
</script>


<style lang="scss" scoped>
.login-main {
    width: 100vw;
    height: 100vh;
    position: relative;

    background-image: url('../assets/images/background.png');
    background-size: cover; // 使背景图片覆盖整个元素  
    background-position: center; // 将背景图片居中显示  
    background-repeat: no-repeat; // 防止背景图片重复 

    .login-box {
        width: 456px;
        height: 404px;
        background: #FFFFFF;
        box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, 0.1);
        border-radius: 10px;
        opacity: 0.9;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 2;
        padding: 0 72px;
        padding-top: 50px;
        overflow: hidden;

        .logo-box {
            width: 100%;
            height: 10%;
            text-align: center;
            color: rgba(24, 144, 255);
            font-size: 30px;
            margin-bottom: 30px;
        }

        :deep(.form-box) {
            .submit-box {
                margin-top: 60px;
                width: 456px;
                height: 48px;
                background: #32C5FF;
                border-radius: 8px;
                cursor: pointer;
                display: flex;
                justify-content: center;
                align-items: center;
                font-family: PingFangSC, PingFang SC;
                font-weight: 600;
                font-size: 16px;
                color: #FFFFFF;
                letter-spacing: 1px;
            }

            .form-item {
                display: flex;
                align-items: center;
                width: 456px;
                height: 48px;
                background: #F8F8F8;
                border-radius: 8px;
                margin-bottom: 30px;
                position: relative;

                .code-btn-box {
                    position: absolute;
                    right: 0;
                    width: 151px;
                    height: 48px;
                    background: #32C5FF;
                    border-radius: 8px;
                    top: 0;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    cursor: pointer;

                    span {
                        font-family: PingFangSC, PingFang SC;
                        font-weight: 400;
                        font-size: 16px;
                        color: #FFFFFF;
                    }
                }

                .error-tip {
                    position: absolute;
                    width: 140px;
                    text-align: right;
                    padding-right: 12px;
                    height: 20px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 400;
                    font-size: 14px;
                    color: #FD4C40;
                    line-height: 20px;
                    right: 0;

                    &.bottom {
                        right: 157px;
                    }
                }

                .el-input {
                    width: 380px;
                    font-family: PingFangSC, PingFang SC;
                    font-weight: 400;
                    font-size: 16px;
                    color: #222222;
                }

                .el-input__wrapper {
                    border: none;
                    box-shadow: none;
                    background: transparent;
                    width: 230px;
                    padding-left: 0;
                }

                img {
                    width: 24px;
                    height: 24px;
                    margin: 0 18px;
                }
            }
        }
    }
}
</style>
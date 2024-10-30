<template>
  <el-container class="layout-container">
    <el-header class="el-header">
      <el-dropdown>
                <span class="el-dropdown__box">
                    <div>
                        <strong>当前用户：</strong>{{ loginUser.nickname }}
                    </div>
                    <el-icon>
                        <ArrowDownBold/>
                    </el-icon>
                </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>
    <el-main class="layout-bottom-box">
      <div class="left">
        <el-aside width="150px" class="el-aside">
          <el-menu class="el-menu" router>
            <el-menu-item index="/admin/user">
              <el-icon>
                <Management/>
              </el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/apply">
              <el-icon>
                <Management/>
              </el-icon>
              <span>冻结申请管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
      </div>
      <div class="right">
        <RouterView/>
      </div>
    </el-main>
  </el-container>
</template>

<script setup>

import {
  Management,
  ArrowDownBold,
  SwitchButton
} from '@element-plus/icons-vue'
import {reactive} from 'vue'
import router from '@/router'
import {getAdminService, logoutService} from '@/apis/admin';
import {removeToken} from '@/utils/cookie';

const loginUser = reactive({
  nickname: ''
})

async function getAdmin() {
  const admin = await getAdminService();
  loginUser.nickname = admin.data.nickname;
}

getAdmin()

async function logout() {
  // 消息提示弹窗
  await ElMessageBox.confirm(
      '确认退出',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '退出',
        type: 'warning',
      }
  )

  // 调用退出登录服务
  await logoutService()
  // 删除已有token
  removeToken()
  // 跳转回登录页面
  router.push("/admin/login")
}

</script>


<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  background: #f7f7f7;

  .layout-bottom-box {
    display: flex;
    justify-content: space-between;
    height: calc(100vh - 100px);
    overflow: hidden;

    .left {
      margin-right: 20px;
      background: #fff;
      display: flex;

      :deep(.el-menu) {
        flex: 1;

        .el-menu-item.is-active {
          color: #32c5ff;
        }

        .el-menu-item:hover {
          background: #fff;
          color: #32c5ff;
        }
      }
    }

    .right {
      flex: 1;
      overflow-y: auto;
      background: #fff;
      padding: 20px;
    }
  }

  .el-aside {
    background-color: #fff;

    .el-menu {
      border-right: none;
    }
  }

  .el-header {
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    height: 40px;

    .el-dropdown__box {
      display: flex;
      align-items: center;

      .el-icon {
        color: #4c4141;
        margin-left: 20px;
      }

      &:active,
      &:focus {
        outline: none;
      }
    }
  }

  .el-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    color: #666;
  }
}
</style>
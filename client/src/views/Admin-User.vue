<template>
  <!-- 顶部搜索表单 -->
  <el-form inline="true">
    <el-form-item label="用户id">
      <el-input v-model="params.userId" placeholder="请您输入要搜索的用户id"/>
    </el-form-item>
    <el-form-item label="用户昵称">
      <el-input v-model="params.nickname" placeholder="请您输入要搜索的用户昵称"/>
    </el-form-item>
    <el-form-item>
      <el-button @click="onSearch" plain>搜索</el-button>
      <el-button @click="onReset" plain type="info">重置</el-button>
    </el-form-item>
  </el-form>

  <!-- 主内容显示表格区 -->
  <el-table height="650px" :data="userList">
    <el-table-column prop="userId" label="用户id" width="180px"/>
    <el-table-column prop="nickname" width="200px" label="昵称"/>
    <el-table-column prop="phone" width="150px" label="手机号"/>
    <el-table-column prop="email" width="300px" label="邮箱"/>
    <el-table-column prop="introduce" width="600px" label="个人介绍"/>
    <el-table-column prop="status" width="90px" label="用户状态">
      <template #default="{ row }">
        <el-tag type="success" v-if="row.status == 1">正常</el-tag>
        <el-tag type="" v-if="row.status == 0">申请冻结</el-tag>
        <el-tag type="error" v-if="row.status == 2">冻结</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="80px" fixed="right">
      <template #default="{ row }">
        <el-button class="red" v-if="row.status === 1" type="text" plain
                   @click="onUpdateUserStatus(row.userId, 2)">冻结
        </el-button>
        <el-button v-if="row.status === 2" type="text" plain
                   @click="onUpdateUserStatus(row.userId, 1)">解冻
        </el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 底部分页区 -->
  <el-pagination background size="small" layout="total, sizes, prev, pager, next, jumper" :total="total"
                 v-model:current-page="params.pageNum" v-model:page-size="params.pageSize" :page-sizes="[5, 10, 15, 20]"
                 @size-change="handleSizeChange" @current-change="handleCurrentChange"/>
</template>


<script setup>
import {reactive, ref} from 'vue';
import {getUserListService, updateStatusService} from '@/apis/admin'

const params = reactive({
  pageNum: 1,
  pageSize: 10,
  userId: '',
  nickname: '',
})

const userList = ref([])
const total = ref(0)

async function getUserList() {
  const ref = await getUserListService(params)
  userList.value = ref.rows
  total.value = ref.total
}
getUserList()

function onSearch() {
  params.pageNum = 1
  getUserList()
}

function onReset() {
  params.pageNum = 1
  params.pageSize = 10
  params.userId = ''
  params.nickname = ''
  getUserList()
}

function handleSizeChange(newSize) {
  params.pageNum = 1
  getUserList()
}

function handleCurrentChange(newPage) {
  getUserList()
}

const updateStatusParams = reactive({
  userId: '',
  status: '',
})

async function onUpdateUserStatus(userId, status) {
  updateStatusParams.userId = userId
  updateStatusParams.status = status
  await updateStatusService(updateStatusParams)
  getUserList()
}
</script>
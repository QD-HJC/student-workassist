<template>
  <div style="padding:15px;">
    <h3>本部门所有岗位报名记录</h3>
    
    <div style="margin: 15px 0;">
      <el-select v-model="filterPostId" placeholder="筛选岗位" clearable @change="loadData" style="width:200px;">
        <el-option v-for="p in postList" :key="p.postId" :label="p.postName" :value="p.postId" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="筛选状态" clearable @change="loadData" style="width:150px;margin-left:10px;">
        <el-option label="待审核" value="待审核" />
        <el-option label="已录用" value="已录用" />
        <el-option label="已拒绝" value="已拒绝" />
        <el-option label="在岗" value="在岗" />
        <el-option label="已离职" value="已离职" />
      </el-select>
      <el-button type="primary" @click="loadData" style="margin-left:10px;">刷新</el-button>
    </div>

    <el-table :data="tableData" border style="margin-top:15px;" v-loading="loading">
      <el-table-column label="岗位名称" prop="jobPost.postName" width="150" />
      <el-table-column label="学生姓名" prop="studentName" width="120" />
      <el-table-column label="报名时间" prop="applyTime" width="180" />
      <el-table-column label="报名状态" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === '待审核' ? 'warning' :
                         scope.row.status === '已录用' || scope.row.status === '在岗' ? 'success' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="面试结果" prop="interviewResult" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.interviewResult === '面试通过' ? 'success' :
                         scope.row.interviewResult === '面试淘汰' ? 'danger' : 'info'">
            {{ scope.row.interviewResult }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="320">
        <template #default="scope">
          <el-select v-model="scope.row.status" @change="audit(scope.row)" placeholder="审核报名" size="small" style="width:110px;">
            <el-option label="已录用" value="已录用" />
            <el-option label="已拒绝" value="已拒绝" />
            <el-option label="在岗" value="在岗" />
            <el-option label="已离职" value="已离职" />
          </el-select>
          <el-select v-model="scope.row.interviewResult" @change="audit(scope.row)" placeholder="面试结果" size="small" style="width:110px;margin-left:5px;">
            <el-option label="面试通过" value="面试通过" />
            <el-option label="面试淘汰" value="面试淘汰" />
            <el-option label="待面试" value="待面试" />
          </el-select>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      :total="total"
      @current-change="loadData"
      style="margin-top:15px;text-align:right"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const user = getUserInfo()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const filterPostId = ref(null)
const filterStatus = ref(null)
const postList = ref([])

// 加载本部门所有岗位
const loadPosts = async () => {
  try {
    const deptId = user?.deptId || user?.userId || 1
    console.log('加载岗位，部门ID:', deptId)
    
    const res = await request({
      url: '/api/post/dept',  // ✅ 加上 /api
      params: { deptId, current: 1, size: 100 }
    })
    console.log('岗位列表返回:', res)
    
    if (res.code === 200) {
      postList.value = res.data.records || []
      if (postList.value.length === 0) {
        ElMessage.info('当前部门暂无岗位')
      }
    } else {
      ElMessage.warning(res.msg || '加载岗位失败')
    }
  } catch (err) {
    console.error('加载岗位失败:', err)
    ElMessage.warning('加载岗位失败，请检查网络')
  }
}

// 加载报名记录
const loadData = async () => {
  loading.value = true
  try {
    const deptId = user?.deptId || user?.userId || 1
    const params = {
      current: page.value.current,
      size: page.value.size,
      deptId: deptId
    }
    if (filterPostId.value) params.postId = filterPostId.value
    if (filterStatus.value) params.status = filterStatus.value
    
    console.log('加载报名记录，参数:', params)
    const res = await request({ 
      url: '/api/apply/dept/page',  // ✅ 加上 /api
      params 
    })
    console.log('报名记录返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载报名记录失败')
    }
  } catch (err) {
    console.error('加载报名记录失败：', err)
    ElMessage.error('加载报名记录失败')
  } finally {
    loading.value = false
  }
}

// 审核报名
const audit = async (row) => {
  try {
    const res = await request({
      url: '/api/apply/audit',  // ✅ 加上 /api
      method: 'put',
      params: {
        applyId: row.applyId,
        status: row.status,
        interviewResult: row.interviewResult
      }
    })
    if (res.code === 200) {
      ElMessage.success('审核完成')
      loadData()
    } else {
      ElMessage.error(res.msg || '审核失败')
      // 刷新列表恢复数据
      loadData()
    }
  } catch (err) {
    console.error('审核失败：', err)
    ElMessage.error('审核操作失败')
    loadData()
  }
}

onMounted(() => {
  console.log('用户信息:', user)
  if (user) {
    loadPosts()
    loadData()
  } else {
    ElMessage.warning('未获取到用户信息，请重新登录')
  }
})
</script>
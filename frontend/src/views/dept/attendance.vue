<template>
  <div style="padding:20px;">
    <h3>本部门学生考勤记录</h3>
    
    <div style="margin: 15px 0;">
      <el-input v-model="searchMonth" placeholder="月份 2026-07" style="width:180px;margin-right:10px;" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="exportData">导出数据</el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="学生姓名" prop="studentName" width="120" />
      <el-table-column label="岗位" prop="postName" width="150" />
      <el-table-column label="日期" prop="workDate" width="120" />
      <el-table-column label="上班打卡" prop="checkIn" width="180" />
      <el-table-column label="下班打卡" prop="checkOut" width="180" />
      <el-table-column label="工时" prop="hour" width="100">
        <template #default="scope">
          {{ scope.row.hour || 0 }}h
        </template>
      </el-table-column>
      <el-table-column label="考勤状态" prop="status" width="140">
        <template #default="scope">
          <el-select v-model="scope.row.status" @change="updateAtt(scope.row)" size="small" placeholder="状态">
            <el-option label="正常" value="正常" />
            <el-option label="迟到" value="迟到" />
            <el-option label="早退" value="早退" />
            <el-option label="缺勤" value="缺勤" />
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
const searchMonth = ref('')

// 加载考勤数据
const loadData = async () => {
  loading.value = true
  try {
    const deptId = user?.deptId || user?.userId || 1
    const params = {
      current: page.value.current,
      size: page.value.size,
      deptId: deptId
    }
    if (searchMonth.value) {
      params.month = searchMonth.value
    }
    
    console.log('📊 加载考勤数据，参数:', params)
    const res = await request({
      url: '/api/attendance/dept/page',  // ✅ 加上 /api
      params
    })
    console.log('📊 考勤数据返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载考勤失败')
    }
  } catch (err) {
    console.error('❌ 加载考勤失败:', err)
    ElMessage.error('加载考勤数据失败')
  } finally {
    loading.value = false
  }
}

// 更新考勤状态
const updateAtt = async (row) => {
  try {
    const res = await request({
      url: '/api/attendance/update',  // ✅ 加上 /api
      method: 'put',
      data: row
    })
    if (res.code === 200) {
      ElMessage.success('更新成功')
    } else {
      ElMessage.error(res.msg || '更新失败')
      // 刷新恢复数据
      loadData()
    }
  } catch (err) {
    console.error('更新考勤失败:', err)
    ElMessage.error('更新失败')
    loadData()
  }
}

// 导出数据（暂未实现）
const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(() => {
  console.log('用户信息:', user)
  loadData()
})
</script>
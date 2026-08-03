<template>
  <div style="padding:20px;">
    <div class="page-header">
      <h3>💰 薪资审核管理</h3>
      <div style="display:flex; gap:10px;">
        <el-button type="success" @click="exportAllSalary" :loading="exporting" round>
          <el-icon><Download /></el-icon>
          导出全部薪资
        </el-button>
        <el-button type="primary" @click="loadData" round>
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>
    
    <div style="margin: 15px 0; display: flex; gap: 10px; flex-wrap: wrap;">
      <el-input v-model="search.studentId" placeholder="学生ID" style="width:150px;" clearable />
      <el-input v-model="search.month" placeholder="月份 2026-07" style="width:180px;" clearable />
      <el-select v-model="search.payStatus" placeholder="发放状态" clearable style="width:150px;">
        <el-option label="全部" value="" />
        <el-option label="待发放" value="待发放" />
        <el-option label="已发放" value="已发放" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="batchPay" :disabled="selectedIds.length === 0">
        批量发放 ({{ selectedIds.length }})
      </el-button>
    </div>

    <el-table 
      :data="tableData" 
      border 
      v-loading="loading"
      @selection-change="handleSelectionChange"
      style="width:100%;"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="学生ID" prop="studentId" width="100" />
      <el-table-column label="学生姓名" prop="studentName" width="120" />
      <el-table-column label="岗位" prop="postName" width="150" />
      <el-table-column label="月份" prop="month" width="120" />
      <el-table-column label="总工时" prop="totalHour" width="100">
        <template #default="scope">
          {{ scope.row.totalHour || 0 }}h
        </template>
      </el-table-column>
      <el-table-column label="应发薪资" prop="totalSalary" width="120">
        <template #default="scope">
          ¥{{ scope.row.totalSalary || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="发放状态" prop="payStatus" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.payStatus === '已发放' ? 'success' : 'warning'">
            {{ scope.row.payStatus }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发放时间" prop="payTime" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button 
            v-if="scope.row.payStatus === '待发放'" 
            size="small" 
            type="success" 
            @click="pay(scope.row)"
          >
            发放
          </el-button>
          <el-button size="small" @click="edit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.salaryId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      :total="total"
      @current-change="loadData"
      @size-change="loadData"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top:15px; text-align:right;"
    />

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑薪资" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学生ID">
          <el-input v-model="form.studentId" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="form.studentName" disabled />
        </el-form-item>
        <el-form-item label="岗位">
          <el-input v-model="form.postName" disabled />
        </el-form-item>
        <el-form-item label="月份">
          <el-input v-model="form.month" disabled />
        </el-form-item>
        <el-form-item label="总工时">
          <el-input v-model="form.totalHour" type="number" />
        </el-form-item>
        <el-form-item label="应发薪资">
          <el-input v-model="form.totalSalary" type="number" />
        </el-form-item>
        <el-form-item label="发放状态">
          <el-select v-model="form.payStatus" style="width:100%;">
            <el-option label="待发放" value="待发放" />
            <el-option label="已发放" value="已发放" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download, Refresh } from '@element-plus/icons-vue'

const loading = ref(false)
const exporting = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const selectedIds = ref([])
const search = ref({ studentId: '', month: '', payStatus: '' })
const dialogVisible = ref(false)
const form = ref({})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: page.value.current,
      size: page.value.size
    }
    if (search.value.studentId) params.studentId = search.value.studentId
    if (search.value.month) params.month = search.value.month
    if (search.value.payStatus) params.payStatus = search.value.payStatus
    
    console.log('📊 加载薪资数据，参数:', params)
    const res = await request({ 
      url: '/api/salary/page', 
      method: 'get',
      params 
    })
    console.log('📊 薪资数据返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (err) {
    console.error('加载失败:', err)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.salaryId)
}

// 单个发放
const pay = async (row) => {
  try {
    await ElMessageBox.confirm('确认发放该薪资？', '提示', { type: 'warning' })
    const res = await request({ 
      url: `/api/salary/pay/${row.salaryId}`, 
      method: 'put' 
    })
    if (res.code === 200) {
      ElMessage.success('发放成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '发放失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('发放失败')
    }
  }
}

// 批量发放
const batchPay = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要发放的薪资记录')
    return
  }
  try {
    await ElMessageBox.confirm(`确认批量发放 ${selectedIds.value.length} 条薪资？`, '提示', { type: 'warning' })
    let successCount = 0
    for (const id of selectedIds.value) {
      const res = await request({ 
        url: `/api/salary/pay/${id}`, 
        method: 'put' 
      })
      if (res.code === 200) successCount++
    }
    ElMessage.success(`批量发放完成，成功 ${successCount} 条`)
    loadData()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('批量发放失败')
    }
  }
}

// 编辑
const edit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    const res = await request({ 
      url: '/api/salary/update', 
      method: 'put', 
      data: form.value 
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (err) {
    ElMessage.error('保存失败')
  }
}

// 删除
const del = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该薪资记录？', '提示', { type: 'warning' })
    const res = await request({ 
      url: `/api/salary/delete/${id}`, 
      method: 'delete' 
    })
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 导出全部薪资
const exportAllSalary = async () => {
  exporting.value = true
  try {
    const params = new URLSearchParams()
    if (search.value.month) params.append('month', search.value.month)
    if (search.value.payStatus) params.append('status', search.value.payStatus)
    
    const response = await fetch(`http://localhost:8080/api/salary/export/all?${params.toString()}`, {
      headers: {
        'token': localStorage.getItem('token')
      }
    })
    
    if (!response.ok) {
      throw new Error('导出失败')
    }
    
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    const date = new Date().toISOString().slice(0, 10)
    link.download = `全部薪资明细_${date}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出失败:', err)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.page-header h3 {
  margin: 0;
}

:deep(.el-table__header) th {
  background: #f5f7fa;
  color: #2c3e50;
  font-weight: 600;
}
</style>
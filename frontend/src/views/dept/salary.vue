<template>
  <div style="padding:20px;">
    <h3>薪资核算管理</h3>
    
    <div style="margin: 15px 0;">
      <el-input v-model="search.studentId" placeholder="学生ID" style="width:150px;margin-right:10px;" clearable />
      <el-input v-model="search.month" placeholder="月份 2026-07" style="width:180px;margin-right:10px;" clearable />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="openDialog">新增薪资记录</el-button>
      <el-button type="warning" @click="batchPay" :disabled="selectedIds.length === 0">
        批量发放 ({{ selectedIds.length }})
      </el-button>
    </div>

    <el-table 
      :data="tableData" 
      border 
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="学生ID" prop="studentId" width="100" />
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
      <el-table-column label="操作" width="250">
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="学生ID">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="岗位ID">
          <el-input v-model="form.postId" placeholder="请输入岗位ID" />
        </el-form-item>
        <el-form-item label="月份">
          <el-input v-model="form.month" placeholder="2026-07" />
        </el-form-item>
        <el-form-item label="总工时">
          <el-input v-model="form.totalHour" placeholder="请输入总工时" />
        </el-form-item>
        <el-form-item label="应发薪资">
          <el-input v-model="form.totalSalary" placeholder="请输入应发薪资" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const user = getUserInfo()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const selectedIds = ref([])
const search = ref({ studentId: '', month: '' })
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.salaryId ? '编辑薪资' : '新增薪资')
const form = ref({
  salaryId: '',
  studentId: '',
  postId: '',
  month: '',
  totalHour: '',
  totalSalary: '',
  payStatus: '待发放'
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: page.value.current,
      size: page.value.size,
      deptId: user?.deptId || user?.userId
    }
    if (search.value.studentId) params.studentId = search.value.studentId
    if (search.value.month) params.month = search.value.month
    
    console.log('📊 加载薪资数据，参数:', params)
    const res = await request({ 
      url: '/api/salary/page', 
      params 
    })
    console.log('📊 薪资数据返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载薪资失败')
    }
  } catch (err) {
    console.error('加载薪资失败:', err)
    ElMessage.error('加载薪资数据失败')
  } finally {
    loading.value = false
  }
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.salaryId)
}

const openDialog = () => {
  form.value = { salaryId: '', studentId: '', postId: '', month: '', totalHour: '', totalSalary: '', payStatus: '待发放' }
  dialogVisible.value = true
}

const edit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submit = async () => {
  try {
    const url = form.value.salaryId ? '/api/salary/update' : '/api/salary/save'
    const method = form.value.salaryId ? 'put' : 'post'
    const res = await request({ url, method, data: form.value })
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

const batchPay = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请选择要发放的薪资记录')
    return
  }
  try {
    await ElMessageBox.confirm(`确认批量发放 ${selectedIds.value.length} 条薪资？`, '提示', { type: 'warning' })
    let successCount = 0
    for (const id of selectedIds.value) {
      const res = await request({ url: `/api/salary/pay/${id}`, method: 'put' })
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

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该薪资记录？', '提示', { type: 'warning' })
    const res = await request({ url: `/api/salary/delete/${id}`, method: 'delete' })
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

onMounted(() => {
  console.log('用户信息:', user)
  loadData()
})
</script>
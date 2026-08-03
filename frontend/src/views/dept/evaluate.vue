<template>
  <div style="padding:20px;">
    <h3>学生考核评价</h3>
    
    <el-button type="primary" @click="openDialog" style="margin: 15px 0;">新增评价</el-button>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="学生姓名" prop="studentName" width="120" />
      <el-table-column label="岗位" prop="postName" width="150" />
      <el-table-column label="评分" prop="score" width="120">
        <template #default="scope">
          <el-rate v-model="scope.row.score" disabled show-score />
        </template>
      </el-table-column>
      <el-table-column label="评价内容" prop="content" />
      <el-table-column label="评价时间" prop="evalTime" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="edit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.evalId)">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生ID">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="岗位ID">
          <el-input v-model="form.postId" placeholder="请输入岗位ID" />
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="form.score" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.content" type="textarea" rows="4" placeholder="请输入评价内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
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
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.evalId ? '编辑评价' : '新增评价')
const form = ref({
  evalId: '',
  studentId: '',
  postId: '',
  score: 3,
  content: ''
})

// 加载评价数据
const loadData = async () => {
  loading.value = true
  try {
    const deptId = user?.deptId || user?.userId || 1
    const params = {
      current: page.value.current,
      size: page.value.size,
      deptId: deptId
    }
    
    console.log('📊 加载评价数据，参数:', params)
    const res = await request({
      url: '/api/eval/dept/page',  // ✅ 加上 /api
      params
    })
    console.log('📊 评价数据返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载评价失败')
    }
  } catch (err) {
    console.error('❌ 加载评价失败:', err)
    ElMessage.error('加载评价数据失败')
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const openDialog = () => {
  form.value = { evalId: '', studentId: '', postId: '', score: 3, content: '' }
  dialogVisible.value = true
}

// 编辑
const edit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交
const submit = async () => {
  try {
    // 表单验证
    if (!form.value.studentId) {
      ElMessage.warning('请输入学生ID')
      return
    }
    if (!form.value.postId) {
      ElMessage.warning('请输入岗位ID')
      return
    }
    if (!form.value.content) {
      ElMessage.warning('请输入评价内容')
      return
    }
    
    const url = form.value.evalId ? '/api/eval/update' : '/api/eval/save'  // ✅ 加上 /api
    const method = form.value.evalId ? 'put' : 'post'
    const res = await request({ url, method, data: form.value })
    
    if (res.code === 200) {
      ElMessage.success('提交成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch (err) {
    console.error('提交失败:', err)
    ElMessage.error('提交失败')
  }
}

// 删除
const del = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该评价？', '提示', { type: 'warning' })
    const res = await request({ 
      url: `/api/eval/delete/${id}`,  // ✅ 加上 /api
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

onMounted(() => {
  console.log('用户信息:', user)
  loadData()
})
</script>
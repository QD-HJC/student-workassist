<template>
  <div style="padding:20px;">
    <h3>黑名单管理</h3>
    
    <el-button type="primary" @click="openDialog" style="margin: 15px 0;">新增拉黑</el-button>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="学生ID" prop="studentId" width="120" />
      <el-table-column label="拉黑原因" prop="reason" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="状态" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === '生效' ? 'danger' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button v-if="scope.row.status === '生效'" size="small" @click="cancel(scope.row)">
            解除拉黑
          </el-button>
          <el-button size="small" type="danger" @click="del(scope.row.blackId)">
            删除
          </el-button>
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

    <!-- 新增拉黑弹窗 -->
    <el-dialog v-model="dialogVisible" title="拉黑学生" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生ID">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="拉黑原因">
          <el-input v-model="form.reason" type="textarea" rows="4" placeholder="请输入拉黑原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确认拉黑</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const user = getUserInfo()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const dialogVisible = ref(false)
const form = ref({ studentId: '', reason: '' })

// 加载黑名单数据
const loadData = async () => {
  loading.value = true
  try {
    const deptId = user?.deptId || user?.userId || 1
    const res = await request({
      url: '/api/black/page',  // ✅ 加上 /api
      params: { 
        current: page.value.current, 
        size: page.value.size, 
        deptId: deptId 
      }
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载黑名单失败')
    }
  } catch (err) {
    console.error('加载黑名单失败:', err)
    ElMessage.error('加载黑名单失败')
  } finally {
    loading.value = false
  }
}

const openDialog = () => {
  form.value = { studentId: '', reason: '' }
  dialogVisible.value = true
}

const submit = async () => {
  try {
    if (!form.value.studentId) {
      ElMessage.warning('请输入学生ID')
      return
    }
    if (!form.value.reason) {
      ElMessage.warning('请输入拉黑原因')
      return
    }
    const res = await request({ 
      url: '/api/black/save',  // ✅ 加上 /api
      method: 'post', 
      data: form.value 
    })
    if (res.code === 200) {
      ElMessage.success('拉黑成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '拉黑失败')
    }
  } catch (err) {
    ElMessage.error('拉黑失败')
  }
}

const cancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认解除该学生的拉黑状态？', '提示', { type: 'warning' })
    row.status = '已解除'
    const res = await request({ 
      url: '/api/black/update',  // ✅ 加上 /api
      method: 'put', 
      data: row 
    })
    if (res.code === 200) {
      ElMessage.success('已解除拉黑')
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const del = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该黑名单记录？', '提示', { type: 'warning' })
    const res = await request({ 
      url: `/api/black/delete/${id}`,  // ✅ 加上 /api
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
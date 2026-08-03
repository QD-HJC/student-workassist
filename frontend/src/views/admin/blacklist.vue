<template>
  <div style="padding:20px;">
    <h3>黑名单管理</h3>
    
    <div style="margin: 15px 0;">
      <el-input v-model="search.studentId" placeholder="学生ID" style="width:150px;margin-right:10px;" />
      <el-select v-model="search.status" placeholder="状态" clearable style="width:150px;margin-right:10px;">
        <el-option label="生效" value="生效" />
        <el-option label="已解除" value="已解除" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button type="success" @click="openDialog">新增拉黑</el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="学生ID" prop="studentId" width="120" />
      <el-table-column label="学生姓名" prop="studentName" width="120" />
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
          <el-button 
            v-if="scope.row.status === '生效'" 
            size="small" 
            @click="cancel(scope.row)"
          >
            解除拉黑
          </el-button>
          <el-button size="small" @click="edit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.blackId)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生ID">
          <el-input v-model="form.studentId" placeholder="请输入学生ID" />
        </el-form-item>
        <el-form-item label="拉黑原因">
          <el-input v-model="form.reason" type="textarea" rows="4" placeholder="请输入拉黑原因" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%;">
            <el-option label="生效" value="生效" />
            <el-option label="已解除" value="已解除" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const search = ref({ studentId: '', status: '' })
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.blackId ? '编辑黑名单' : '新增拉黑')
const form = ref({ blackId: '', studentId: '', reason: '', status: '生效' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/api/black/page',
      params: { 
        current: page.value.current, 
        size: page.value.size,
        ...search.value
      }
    })
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

const openDialog = () => {
  form.value = { blackId: '', studentId: '', reason: '', status: '生效' }
  dialogVisible.value = true
}

const edit = (row) => {
  form.value = { ...row }
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
    const url = form.value.blackId ? '/api/black/update' : '/api/black/save'
    const method = form.value.blackId ? 'put' : 'post'
    const res = await request({ url, method, data: form.value })
    if (res.code === 200) {
      ElMessage.success('操作成功')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.msg || '操作失败')
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

const cancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认解除该学生的拉黑状态？', '提示', { type: 'warning' })
    row.status = '已解除'
    const res = await request({ url: '/api/black/update', method: 'put', data: row })
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
    const res = await request({ url: `/api/black/delete/${id}`, method: 'delete' })
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

onMounted(loadData)
</script>
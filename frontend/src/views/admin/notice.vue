<template>
  <div style="padding:20px;">
    <h3>📢 公告管理</h3>
    
    <!-- 发布按钮 -->
    <div style="margin: 15px 0;">
      <el-button type="primary" @click="openAddDialog">发布公告</el-button>
      <el-button type="success" @click="loadData">刷新</el-button>
    </div>

    <!-- 公告列表 -->
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="标题" prop="title" min-width="200">
        <template #default="scope">
          <span v-if="scope.row.isTop" style="color:#f56c6c;font-weight:bold;">[置顶]</span>
          {{ scope.row.title }}
        </template>
      </el-table-column>
      <el-table-column label="发布人" prop="author" width="120" />
      <el-table-column label="状态" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否置顶" prop="isTop" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.isTop ? 'danger' : ''">
            {{ scope.row.isTop ? '置顶' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button 
            v-if="scope.row.status === '已发布'" 
            size="small" 
            type="warning" 
            @click="offline(scope.row.noticeId)"
          >
            下架
          </el-button>
          <el-button 
            v-else 
            size="small" 
            type="success" 
            @click="publish(scope.row.noticeId)"
          >
            发布
          </el-button>
          <el-button size="small" type="danger" @click="del(scope.row.noticeId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="page.current"
      v-model:page-size="page.size"
      :total="total"
      @current-change="loadData"
      @size-change="loadData"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top:15px; text-align:right;"
    />

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input 
            v-model="form.content" 
            type="textarea" 
            rows="6" 
            placeholder="请输入公告内容"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="form.author" placeholder="请输入发布人" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">确认发布</el-button>
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
const dialogVisible = ref(false)
const isEdit = ref(false)

const form = ref({
  noticeId: '',
  title: '',
  content: '',
  author: '',
  isTop: 0
})

const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '发布公告')

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/api/newnotice/page',
      params: { current: page.value.current, size: page.value.size }
    })
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (err) {
    console.error('加载公告失败', err)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  form.value = { noticeId: '', title: '', content: '', author: '', isTop: 0 }
  dialogVisible.value = true
}

// 打开编辑弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

// 提交
const submit = async () => {
  if (!form.value.title.trim()) {
    ElMessage.warning('请输入标题')
    return
  }
  if (!form.value.content.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  
  try {
    const url = isEdit.value ? '/api/newnotice/update' : '/api/newnotice/save'
    const method = isEdit.value ? 'put' : 'post'
    const res = await request({ url, method, data: form.value })
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// 下架
const offline = async (id) => {
  try {
    await ElMessageBox.confirm('确认下架该公告？', '提示', { type: 'warning' })
    const res = await request({ url: `/api/newnotice/offline/${id}`, method: 'put' })
    if (res.code === 200) {
      ElMessage.success('已下架')
      loadData()
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

// 发布（重新上架）
const publish = async (id) => {
  try {
    const res = await request({ 
      url: `/api/newnotice/update`, 
      method: 'put',
      data: { noticeId: id, status: '已发布' }
    })
    if (res.code === 200) {
      ElMessage.success('已发布')
      loadData()
    }
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

// 删除
const del = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该公告？', '提示', { type: 'warning' })
    const res = await request({ url: `/api/newnotice/delete/${id}`, method: 'delete' })
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(loadData)
</script>
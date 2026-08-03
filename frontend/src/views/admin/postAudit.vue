<template>
  <div>
    <h3>岗位审核管理</h3>
    
    <div style="margin: 15px 0;">
      <el-select v-model="filterStatus" placeholder="审核状态" clearable @change="loadData" style="width:150px;">
        <el-option label="待审核" value="待审核" />
        <el-option label="已发布" value="已发布" />
        <el-option label="已驳回" value="已驳回" />
      </el-select>
      <el-button type="primary" @click="loadData" style="margin-left:10px;">刷新</el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="岗位名称" prop="postName" width="150" />
      <el-table-column label="所属部门" prop="deptId" width="120" />
      <el-table-column label="工作地点" prop="address" width="150" />
      <el-table-column label="时薪" prop="salary" width="100">
        <template #default="scope">
          ¥{{ scope.row.salary }}
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === '待审核' ? 'warning' :
                         scope.row.status === '已发布' ? 'success' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <template v-if="scope.row.status === '待审核'">
            <el-button size="small" type="success" @click="audit(scope.row.postId, '已发布')">
              通过
            </el-button>
            <el-button size="small" type="danger" @click="audit(scope.row.postId, '已驳回')">
              驳回
            </el-button>
          </template>
          <span v-else style="color:#999;">已处理</span>
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
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref({ current: 1, size: 10 })
const filterStatus = ref('待审核')

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: page.value.current,
      size: page.value.size
    }
    // 默认只查待审核的，如果选择了其他状态则按选择查询
    if (filterStatus.value) {
      params.status = filterStatus.value
    }
    
    console.log('📊 加载岗位数据，参数:', params)
    const res = await request({
      url: '/api/post/page',  // ✅ 加上 /api
      method: 'get',
      params
    })
    console.log('📊 岗位数据返回:', res)
    
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  } catch (err) {
    console.error('加载失败:', err)
    ElMessage.error('加载数据失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const audit = async (postId, status) => {
  try {
    const res = await request({
      url: '/api/post/audit',  // ✅ 加上 /api
      method: 'put',
      params: { postId, status }
    })
    if (res.code === 200) {
      ElMessage.success('审核完成')
      loadData()
    } else {
      ElMessage.error(res.msg || '审核失败')
    }
  } catch (err) {
    console.error('审核失败:', err)
    ElMessage.error('审核操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>
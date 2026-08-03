<template>
  <div class="admin-home">
    <el-row :gutter="20">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="32"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>待审核岗位</span>
          </template>
          <el-table :data="pendingPosts" style="width:100%" max-height="300" v-loading="loading">
            <el-table-column prop="postName" label="岗位名称" />
            <el-table-column prop="deptId" label="所属部门" width="120" />
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button size="small" type="success" @click="auditPost(scope.row.postId, '已发布')">
                  通过
                </el-button>
                <el-button size="small" type="danger" @click="auditPost(scope.row.postId, '已驳回')">
                  驳回
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>待处理薪资</span>
          </template>
          <el-table :data="pendingSalary" style="width:100%" max-height="300" v-loading="loading">
            <el-table-column prop="studentId" label="学生ID" width="100" />
            <el-table-column prop="postId" label="岗位ID" width="100" />
            <el-table-column prop="totalSalary" label="金额" width="120" />
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button size="small" type="primary" @click="approveSalary(scope.row)">
                  确认发放
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { Document, Clock, User, Money } from '@element-plus/icons-vue'

const loading = ref(false)
const stats = ref([
  { label: '总岗位数', value: 0, icon: 'Document', color: '#409EFF' },
  { label: '待审核岗位', value: 0, icon: 'Clock', color: '#E6A23C' },
  { label: '在岗学生', value: 0, icon: 'User', color: '#67C23A' },
  { label: '待发放薪资', value: 0, icon: 'Money', color: '#F56C6C' }
])

const pendingPosts = ref([])
const pendingSalary = ref([])

const loadData = async () => {
  loading.value = true
  try {
    // 获取统计数据
    const res = await request({ 
      url: '/api/admin/statistics',  // ✅ 加上 /api
      method: 'get'
    })
    console.log('统计数据返回:', res)
    if (res.code === 200) {
      const data = res.data
      stats.value[0].value = data.totalPosts || 0
      stats.value[1].value = data.pendingPosts || 0
      stats.value[2].value = data.activeStudents || 0
      stats.value[3].value = data.pendingSalary || 0
    }
    
    // 获取待审核岗位
    const postRes = await request({ 
      url: '/api/admin/post/pending',  // ✅ 加上 /api
      method: 'get'
    })
    if (postRes.code === 200) {
      pendingPosts.value = postRes.data || []
    }
    
    // 获取待处理薪资
    const salaryRes = await request({ 
      url: '/api/admin/salary/pending',  // ✅ 加上 /api
      method: 'get'
    })
    if (salaryRes.code === 200) {
      pendingSalary.value = salaryRes.data || []
    }
  } catch (err) {
    console.error('加载数据失败:', err)
    ElMessage.error('加载数据失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

const auditPost = async (postId, status) => {
  try {
    const res = await request({
      url: '/api/post/audit',
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
    ElMessage.error('审核失败')
  }
}

const approveSalary = async (row) => {
  try {
    const res = await request({
      url: '/api/salary/update',
      method: 'put',
      data: { ...row, payStatus: '已发放', payTime: new Date() }
    })
    if (res.code === 200) {
      ElMessage.success('薪资已发放')
      loadData()
    } else {
      ElMessage.error(res.msg || '发放失败')
    }
  } catch (err) {
    ElMessage.error('发放失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-home {
  padding: 10px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: default;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  color: #999;
  font-size: 14px;
}
</style>
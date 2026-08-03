<template>
  <div class="dept-home">
    <!-- 欢迎卡片 -->
    <el-card shadow="hover" class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-left">
          <div class="welcome-greeting">👋 欢迎回来</div>
          <h2>{{ user?.realName || '管理员' }}</h2>
          <p>今天有 <strong class="highlight">{{ stats.waitApply }}</strong> 条报名申请待处理，请及时查看。</p>
        </div>
        <div class="welcome-divider"></div>
        <div class="welcome-right">
          <div class="dept-info">
            <span class="dept-label">🏢 部门归属</span>
            <span class="dept-name">{{ user?.deptName || '未知部门' }}</span>
          </div>
          <div class="dept-stats">
            <span>📋 总岗位：{{ stats.totalPosts || 0 }}</span>
            <span>👥 在岗学生：{{ stats.activeStudents || 0 }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-top: 24px;">
      <el-col :span="6" v-for="stat in statCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card" @click="stat.path && $router.push(stat.path)">
          <div class="stat-content">
            <div class="stat-left">
              <div class="stat-number" :style="{ color: stat.color }">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
            <div class="stat-icon" :style="{ background: stat.color }">
              <el-icon :size="28"><component :is="stat.icon" /></el-icon>
            </div>
          </div>
          <div class="stat-trend" v-if="stat.trend">
            <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
              {{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%
            </span>
            <span class="trend-label">较上月</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近期活动 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <div class="notice-header">
          <span class="notice-title">📢 近期活动提醒</span>
          <span class="notice-more" @click="$router.push('/dept/applyList')">
            查看全部 <el-icon><Right /></el-icon>
          </span>
        </div>
        <el-card shadow="hover" class="notice-card">
          <div class="notice-item" v-for="item in noticeList" :key="item.id">
            <div class="notice-tag" :class="item.tagType">{{ item.tagName }}</div>
            <div class="notice-body">
              <div class="notice-text">{{ item.title }}</div>
              <div class="notice-sub">{{ item.subText }}</div>
            </div>
            <div class="notice-time">{{ item.time }}</div>
          </div>
          <div v-if="noticeList.length === 0" class="notice-empty">
            暂无通知
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部 -->
    <div class="footer">
      <span>关于系统</span>
      <span>使用手册</span>
      <span>联系后勤处</span>
      <span>反馈建议</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo } from '../../utils/storage'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'
import { Document, User, Money, Checked, Right } from '@element-plus/icons-vue'

const user = ref(getUserInfo())

const stats = ref({
  waitAuditPost: 0,
  waitApply: 0,
  waitSalary: 0,
  activeStudents: 0,
  totalPosts: 0
})

const statCards = computed(() => [
  { 
    label: '待审核岗位', 
    value: stats.value.waitAuditPost, 
    icon: 'Document', 
    color: '#409EFF',
    path: '/dept/postAdd',
    trend: 5
  },
  { 
    label: '待处理报名', 
    value: stats.value.waitApply, 
    icon: 'User', 
    color: '#E6A23C',
    path: '/dept/applyList',
    trend: -2
  },
  { 
    label: '待核算薪资', 
    value: stats.value.waitSalary, 
    icon: 'Money', 
    color: '#F56C6C',
    path: '/dept/salary',
    trend: 0
  },
  { 
    label: '在岗学生', 
    value: stats.value.activeStudents, 
    icon: 'Checked', 
    color: '#67C23A',
    path: '/dept/attendance',
    trend: 8
  }
])

const noticeList = ref([
  {
    id: 1,
    tagType: 'urgent',
    tagName: '紧急',
    title: '关于2026年7月份考勤确认截止日期的通知',
    subText: '截止日期: 2026-07-15 17:00',
    time: '今天'
  },
  {
    id: 2,
    tagType: 'normal',
    tagName: '普通',
    title: '新增3名学生通过了"图书馆管理员"岗位的初步筛选',
    subText: '更新时间: 2026-07-08 09:30',
    time: '昨天'
  },
  {
    id: 3,
    tagType: 'tip',
    tagName: '提示',
    title: '系统将于2026-07-10进行版本升级维护 (23:00-01:00)',
    subText: '发布单位: 校网络中心',
    time: '3天前'
  }
])

const loadOverview = async () => {
  try {
    const deptId = user.value?.deptId || user.value?.userId || 1
    const res = await request({
      url: '/api/dept/home/overview',
      params: { deptId }
    })
    if (res.code === 200) {
      const data = res.data
      stats.value.waitAuditPost = data.waitAuditPost || 0
      stats.value.waitApply = data.waitApply || 0
      stats.value.waitSalary = data.waitSalary || 0
      stats.value.activeStudents = data.activeStudents || 0
      stats.value.totalPosts = data.totalPosts || 0
    }
  } catch (err) {
    console.error('加载统计数据失败:', err)
  }
}

onMounted(() => {
  const storedUser = getUserInfo()
  if (storedUser) {
    user.value = storedUser
  }
  loadOverview()
})
</script>

<style scoped>
.dept-home {
  width: 100%;
}

/* ===== 欢迎卡片 ===== */
.welcome-card {
  border-radius: 16px;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.welcome-card :deep(.el-card__body) {
  padding: 0;
}

.welcome-content {
  display: flex;
  align-items: center;
  padding: 32px 40px;
  gap: 40px;
  color: #fff;
}

.welcome-left {
  flex: 1;
}

.welcome-greeting {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.welcome-left h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 6px;
}

.welcome-left p {
  font-size: 15px;
  opacity: 0.9;
  margin: 0;
}

.welcome-left .highlight {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}

.welcome-divider {
  width: 1px;
  height: 60px;
  background: rgba(255, 255, 255, 0.2);
}

.welcome-right {
  text-align: right;
  flex-shrink: 0;
}

.dept-info {
  margin-bottom: 8px;
}

.dept-label {
  font-size: 13px;
  opacity: 0.7;
  display: block;
}

.dept-name {
  font-size: 22px;
  font-weight: 700;
}

.dept-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  opacity: 0.8;
}

/* ===== 统计卡片 ===== */
.stat-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e8ecf1;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.10);
  border-color: transparent;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.stat-left {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #8a9aa9;
  margin-top: 2px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-trend {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.trend-label {
  color: #aab8c5;
}

/* ===== 通知 ===== */
.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.notice-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.notice-more {
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.notice-more:hover {
  text-decoration: underline;
}

.notice-card {
  border-radius: 12px;
  border: 1px solid #e8ecf1;
}

.notice-item {
  display: flex;
  align-items: flex-start;
  padding: 14px 0;
  border-bottom: 1px solid #f0f2f5;
  gap: 14px;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-tag {
  padding: 2px 12px;
  font-size: 12px;
  border-radius: 4px;
  font-weight: 500;
  flex-shrink: 0;
  margin-top: 2px;
}

.notice-tag.urgent {
  background: #fef0f0;
  color: #f56c6c;
}

.notice-tag.normal {
  background: #f0f2f5;
  color: #5a6a7a;
}

.notice-tag.tip {
  background: #e6f7ff;
  color: #409EFF;
}

.notice-tag.warn {
  background: #fdf6ec;
  color: #e6a23c;
}

.notice-body {
  flex: 1;
}

.notice-text {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
}

.notice-sub {
  font-size: 13px;
  color: #aab8c5;
  margin-top: 2px;
}

.notice-time {
  font-size: 12px;
  color: #aab8c5;
  flex-shrink: 0;
}

.notice-empty {
  text-align: center;
  padding: 24px;
  color: #aab8c5;
}

/* ===== 底部 ===== */
.footer {
  text-align: center;
  margin-top: 30px;
  padding: 16px 0 8px;
  color: #aab8c5;
  font-size: 13px;
  display: flex;
  justify-content: center;
  gap: 20px;
  border-top: 1px solid #e8ecf1;
}

.footer span {
  cursor: pointer;
  transition: color 0.3s;
}

.footer span:hover {
  color: #409EFF;
}
</style>
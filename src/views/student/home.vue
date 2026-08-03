<template>
  <div class="home-page">
    <!-- Banner 广告区 -->
    <el-card shadow="hover" class="banner-card">
      <div class="banner-content">
        <div class="banner-text">
          <h1>🌟 2026夏季勤工助学岗位火热申请中</h1>
          <p>覆盖全校 <strong>22</strong> 个部门，共计 <strong>200+</strong> 岗位空缺</p>
          <div class="banner-stats">
            <span><span class="num">{{ bannerStats.totalPosts }}</span> 个岗位等你来</span>
            <span><span class="num">{{ bannerStats.totalApplies }}</span> 名学生已报名</span>
            <span><span class="num">{{ bannerStats.totalDepts }}</span> 个部门参与</span>
          </div>
        </div>
        <el-button type="primary" size="large" round class="banner-btn" @click="$router.push('/student/post')">
          立即查看 <el-icon><Right /></el-icon>
        </el-button>
      </div>
    </el-card>

    <!-- 快捷功能 -->
    <div class="quick-box">
      <div class="quick-grid">
        <div class="quick-card-wrap" v-for="item in quickList" :key="item.path" @click="$router.push(item.path)">
          <el-card shadow="hover" class="quick-card">
            <div class="quick-icon" :style="{ background: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <div class="quick-text">{{ item.name }}</div>
            <div class="quick-desc">{{ item.desc }}</div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 最新岗位 -->
    <div class="new-post-area">
      <div class="area-title">
        <div class="area-title-left">
          <span class="title-icon">🔥</span>
          <h3>最新发布岗位</h3>
          <el-tag size="small" type="danger" effect="plain">Hot</el-tag>
        </div>
        <el-text type="primary" class="view-all" @click="$router.push('/student/post')">
          查看更多 <el-icon><Right /></el-icon>
        </el-text>
      </div>

      <el-row :gutter="20" style="margin-top: 16px;">
        <el-col :span="8" v-for="item in newPostList" :key="item.postId">
          <el-card shadow="hover" class="post-card" @click="handleApply(item)">
            <div class="post-top">
              <span class="post-dept">{{ item.deptName || '未知部门' }}</span>
              <el-tag size="small" type="success" effect="plain">招聘中</el-tag>
            </div>
            <h4 class="post-name">{{ item.postName }}</h4>
            <div class="post-info">
              <span class="post-salary">
                <span class="salary-icon">💰</span>
                {{ item.salary }}元/小时
              </span>
              <span class="post-address">
                <el-icon><Location /></el-icon>
                {{ item.address || '校内' }}
              </span>
            </div>
            <div class="post-bottom">
              <span class="post-time">
                <el-icon><Clock /></el-icon>
                {{ item.workTime || '面议' }}
              </span>
              <el-button size="small" type="primary" round @click.stop="handleApply(item)">
                立即申请
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewPostList } from '../../api/post'
import { saveApply } from '../../api/apply'
import { getUserInfo } from '../../utils/storage'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { Right, Location, Clock, Document, List, Timer, Money, ChatDotRound } from '@element-plus/icons-vue'

const userInfo = getUserInfo()

const quickList = ref([
  { name: '岗位列表', path: '/student/post', icon: 'Document', color: '#409EFF', desc: '浏览所有岗位' },
  { name: '我的报名', path: '/student/myApply', icon: 'List', color: '#67C23A', desc: '查看报名进度' },
  { name: '考勤打卡', path: '/student/attendance', icon: 'Timer', color: '#E6A23C', desc: '上下班打卡' },
  { name: '我的薪资', path: '/student/salary', icon: 'Money', color: '#F56C6C', desc: '查看薪资明细' },
  { name: '我的评价', path: '/student/evaluate', icon: 'ChatDotRound', color: '#9B59B6', desc: '查看评价记录' }
])

const newPostList = ref([])
const bannerStats = ref({
  totalPosts: 0,
  totalApplies: 0,
  totalDepts: 0
})
const loadBannerStats = async () => {
  try {
    const res = await request({
      url: '/api/student/home/statistics',
      method: 'get'
    })
    if (res.code === 200) {
      bannerStats.value = res.data
    }
  } catch (err) {
    console.error('加载统计数据失败', err)
  }
}
const loadNewPost = async () => {
  try {
    const res = await getNewPostList()
    if (res.code === 200) {
      newPostList.value = res.data.slice(0, 3) || []
    }
  } catch (err) {
    console.error('加载最新岗位失败', err)
  }
}

const handleApply = async (row) => {
  try {
    const params = {
      postId: row.postId,
      studentId: userInfo.userId
    }
    const res = await saveApply(params)
    if (res.code === 200) {
      ElMessage.success('🎉 报名成功！请前往"我的报名"查看审核状态')
    } else {
      ElMessage.error(res.msg || '报名失败')
    }
  } catch (err) {
    ElMessage.error('报名失败，请稍后重试')
  }
}

onMounted(() => {
  loadNewPost()
  loadBannerStats()
})
</script>

<style scoped>
.home-page {
  width: 100%;
}

/* ===== Banner ===== */
.banner-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 16px;
  overflow: hidden;
  padding: 0;
}

.banner-card :deep(.el-card__body) {
  padding: 0;
}

.banner-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 40px 40px;
  text-align: center;
  position: relative;
  min-height: 220px;
}

.banner-text h1 {
  font-size: 30px;
  color: #fff;
  margin: 0 0 12px;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.banner-text p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 20px;
}

.banner-text p strong {
  color: #fff;
  font-weight: 700;
}

.banner-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 24px;
}

.banner-stats span {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.banner-stats .num {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  margin-right: 4px;
}

.banner-btn {
  background: #fff;
  color: #667eea;
  border: none;
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;
}

.banner-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
  background: #fff;
  color: #667eea;
}

/* ===== 快捷功能 ===== */
.quick-box {
  margin: 24px 0;
}

.quick-grid {
  display: flex;
  gap: 16px;
}

.quick-card-wrap {
  flex: 1;
}

.quick-card {
  text-align: center;
  padding: 20px 12px;
  cursor: pointer;
  border-radius: 12px;
  border: 1px solid #e8ecf1;
  transition: all 0.3s ease;
}

.quick-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.10);
  border-color: transparent;
}

.quick-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 12px;
}

.quick-text {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

.quick-desc {
  font-size: 13px;
  color: #aab8c5;
  margin-top: 4px;
}

/* ===== 最新岗位 ===== */
.new-post-area {
  margin-top: 8px;
}

.area-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.area-title-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  font-size: 24px;
}

.area-title-left h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
}

.view-all {
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 2px;
}

.view-all:hover {
  text-decoration: underline;
}

.post-card {
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid #e8ecf1;
  transition: all 0.3s ease;
  height: 210px;
  display: flex;
  flex-direction: column;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.10);
  border-color: transparent;
}

.post-card :deep(.el-card__body) {
  padding: 16px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.post-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.post-dept {
  font-size: 12px;
  color: #8a9aa9;
  background: #f0f4f8;
  padding: 2px 12px;
  border-radius: 10px;
}

.post-name {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin: 4px 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.post-info {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #5a6a7a;
  margin-bottom: auto;
}

.post-salary {
  color: #f56c6c;
  font-weight: 600;
}

.salary-icon {
  margin-right: 2px;
}

.post-address {
  display: flex;
  align-items: center;
  gap: 2px;
}

.post-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f0f2f5;
}

.post-time {
  font-size: 12px;
  color: #aab8c5;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
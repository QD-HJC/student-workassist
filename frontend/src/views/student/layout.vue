<template>
  <div class="layout-wrap">
    <!-- 顶部导航栏 -->
    <el-header class="top-header">
      <div class="logo-area">
        <span class="logo-icon">📚</span>
        <span class="logo-text">广软勤工助学管理系统</span>
        <span class="logo-tag">学生端</span>
      </div>

      <el-menu class="top-menu" mode="horizontal" router :default-active="$route.path" background-color="#2c3e50" text-color="#ecf0f1" active-text-color="#ffffff">
        <el-menu-item index="/student/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/post">
          <el-icon><Document /></el-icon>
          <span>岗位列表</span>
        </el-menu-item>
        <el-menu-item index="/student/myApply">
          <el-icon><List /></el-icon>
          <span>我的报名</span>
        </el-menu-item>
        <el-menu-item index="/student/attendance">
          <el-icon><Clock /></el-icon>
          <span>考勤打卡</span>
        </el-menu-item>
        <el-menu-item index="/student/salary">
          <el-icon><Money /></el-icon>
          <span>我的薪资</span>
        </el-menu-item>
        <el-menu-item index="/student/evaluate">
          <el-icon><ChatDotRound /></el-icon>
          <span>我的评价</span>
        </el-menu-item>
      </el-menu>

      <div class="header-right">
        <div class="search-wrap">
          <el-input v-model="searchKey" placeholder="搜索岗位..." @keyup.enter="handleSearch" size="default" class="search-input">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>
          </el-button>
        </div>

        <el-dropdown trigger="click" class="user-dropdown">
          <span class="user-info">
            <el-avatar :size="36" class="user-avatar">
              {{ userInfo?.realName?.charAt(0) || 'U' }}
            </el-avatar>
            <span class="user-name">{{ userInfo?.realName || '用户' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-item icon="User">
              <span>个人信息</span>
            </el-dropdown-item>
            <el-dropdown-item icon="Setting">
              <span>设置</span>
            </el-dropdown-item>
            <el-dropdown-item divided icon="SwitchButton" @click="logout">
              <span style="color: #f56c6c;">退出登录</span>
            </el-dropdown-item>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <div class="main-body">
      <!-- 左侧分类侧边栏 -->
      <el-aside width="200px" class="left-category">
        <div class="aside-header">
          <el-icon><Grid /></el-icon>
          <span>岗位分类</span>
        </div>
        <el-menu router class="category-menu" :default-active="$route.query.postType ? `/student/post?postType=${$route.query.postType}` : ''">
          <el-menu-item index="/student/post" @click="clearCategory">
            <el-icon><Menu /></el-icon>
            <span>全部岗位</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=行政助理" @click="setCategory('行政助理')">
            <el-icon><Briefcase /></el-icon>
            <span>行政助理</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=后勤服务" @click="setCategory('后勤服务')">
            <el-icon><Tools /></el-icon>
            <span>后勤服务</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=实验室管理员" @click="setCategory('实验室管理员')">
            <el-icon><Monitor /></el-icon>
            <span>实验室管理员</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=新媒体运营" @click="setCategory('新媒体运营')">
            <el-icon><Share /></el-icon>
            <span>新媒体运营</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=外语翻译" @click="setCategory('外语翻译')">
            <el-icon><ChatLineSquare /></el-icon>
            <span>外语翻译</span>
          </el-menu-item>
          <el-menu-item index="/student/post?postType=其他助理" @click="setCategory('其他助理')">
            <el-icon><More /></el-icon>
            <span>其他助理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <div class="content-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>

        <!-- 底部页脚 -->
        <div class="footer">
          <span>© 2026 广软勤工助学管理系统</span>
          <span class="footer-divider">|</span>
          <span>关于系统</span>
          <span class="footer-divider">|</span>
          <span>使用手册</span>
          <span class="footer-divider">|</span>
          <span>联系后勤处</span>
          <span class="footer-divider">|</span>
          <span>反馈建议</span>
        </div>
      </div>

      <!-- 右侧信息面板 -->
      <el-aside width="280px" class="right-panel">
        <!-- 我的概览 -->
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>📊 我的概览</span>
              <el-tag size="small" type="success" effect="plain">本月</el-tag>
            </div>
          </template>
          <div class="overview-grid">
            <div class="overview-item">
              <div class="overview-icon" style="background: #409EFF;">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="overview-info">
                <span class="overview-label">本月工时</span>
                <span class="overview-value">{{ overview.monthHour || 0 }}h</span>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-icon" style="background: #67C23A;">
                <el-icon><Money /></el-icon>
              </div>
              <div class="overview-info">
                <span class="overview-label">待结薪资</span>
                <span class="overview-value">¥{{ overview.waitSalary || 0 }}</span>
              </div>
            </div>
            <div class="overview-item">
              <div class="overview-icon" style="background: #E6A23C;">
                <el-icon><Briefcase /></el-icon>
              </div>
              <div class="overview-info">
                <span class="overview-label">在岗岗位</span>
                <span class="overview-value">{{ overview.runningPost || 0 }} 个</span>
              </div>
            </div>
          </div>
          <el-button type="primary" block class="clock-btn" @click="$router.push('/student/attendance')">
            <el-icon><Pointer /></el-icon>
            打卡进入岗位
          </el-button>
        </el-card>

        <!-- 通知公告 -->
        <el-card shadow="hover" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>📢 通知公告</span>
              <el-tag size="small" type="info" effect="plain">最新</el-tag>
            </div>
          </template>
          <div class="notice-list">
            <div class="notice-item" v-for="item in noticeList" :key="item.noticeId || item.id" @click="showNoticeDetail(item)">
              <div class="notice-dot" :class="item.isTop ? 'top' : ''"></div>
              <div class="notice-content">
                <div class="notice-title">{{ item.title }}</div>
                <div class="notice-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(item.createTime) }}
                </div>
              </div>
            </div>
            <div v-if="noticeList.length === 0" class="notice-empty">暂无公告</div>
          </div>
        </el-card>
      </el-aside>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" :title="detailTitle" width="520px" center>
      <div class="notice-detail-content">
        <div class="detail-meta">
          <span class="detail-author">📝 发布人：{{ detailAuthor || '资助中心' }}</span>
          <span class="detail-time">🕐 发布时间：{{ detailTime }}</span>
        </div>
        <el-divider />
        <div class="detail-body">{{ detailContent }}</div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfo, clearUserInfo } from '../../utils/storage'
import { getStudentOverview } from '../../api/student'
import request from '../../utils/request'
import {
  HomeFilled, Document, List, Clock, Money, ChatDotRound,
  Search, ArrowDown, Grid, Menu, Briefcase, Tools,
  Monitor, Share, ChatLineSquare, More, Timer, Pointer,
  User, Setting, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = ref(getUserInfo())
const searchKey = ref('')
const overview = reactive({
  monthHour: 0,
  waitSalary: 0,
  runningPost: 0
})
const noticeList = ref([])

// 公告详情弹窗
const detailDialogVisible = ref(false)
const detailTitle = ref('')
const detailAuthor = ref('')
const detailTime = ref('')
const detailContent = ref('')

// 加载公告
const loadNotices = async () => {
  try {
    const res = await request({
      url: '/api/newnotice/list',
      method: 'get'
    })
    if (res.code === 200) {
      noticeList.value = res.data || []
    }
  } catch (err) {
    console.error('加载公告失败', err)
  }
}

// 显示公告详情
const showNoticeDetail = (item) => {
  detailTitle.value = item.title
  detailAuthor.value = item.author || '资助中心'
  detailTime.value = formatDate(item.createTime)
  detailContent.value = item.content || '暂无详细内容'
  detailDialogVisible.value = true
}

// 格式化时间
const formatDate = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const loadOverview = async () => {
  try {
    const res = await getStudentOverview(userInfo.value?.userId)
    if (res.code === 200) {
      Object.assign(overview, res.data)
    }
  } catch (err) {
    console.error('加载概览失败', err)
  }
}

const handleSearch = () => {
  if (searchKey.value.trim()) {
    router.push({ path: '/student/post', query: { keyWord: searchKey.value } })
  }
}

const clearCategory = () => {
  router.push('/student/post')
}

const setCategory = (type) => {
  router.push({ path: '/student/post', query: { postType: type } })
}

const logout = () => {
  clearUserInfo()
  router.push('/login')
}

onMounted(() => {
  loadOverview()
  loadNotices()
})
</script>

<style scoped>
/* ===== 整体布局 ===== */
.layout-wrap {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

/* ===== 顶部导航 ===== */
.top-header {
  display: flex;
  align-items: center;
  padding: 0 24px;
  background: #2c3e50;
  height: 64px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 30px;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #ecf0f1;
  letter-spacing: 1px;
}

.logo-tag {
  font-size: 11px;
  color: #3498db;
  background: rgba(52, 152, 219, 0.2);
  padding: 2px 10px;
  border-radius: 10px;
  border: 1px solid rgba(52, 152, 219, 0.3);
}

.top-menu {
  flex: 1;
  border-bottom: none;
  background: transparent;
}

.top-menu .el-menu-item {
  color: #bfcbd9;
  border-bottom: 2px solid transparent;
  height: 64px;
  line-height: 64px;
  padding: 0 16px;
  font-weight: 500;
  transition: all 0.3s;
}

.top-menu .el-menu-item:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.05);
}

.top-menu .el-menu-item.is-active {
  color: #ffffff;
  border-bottom-color: #3498db;
  background: rgba(52, 152, 219, 0.15);
}

.top-menu .el-menu-item .el-icon {
  margin-right: 6px;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.search-wrap {
  display: flex;
  align-items: center;
}

.search-input {
  width: 220px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px 0 0 20px;
  border-right: none;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
}

.search-input :deep(.el-input__wrapper) .el-input__inner {
  color: #ecf0f1;
}

.search-input :deep(.el-input__wrapper) .el-input__inner::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.search-btn {
  border-radius: 0 20px 20px 0;
  background: #3498db;
  border: none;
}

.search-btn:hover {
  background: #2980b9;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #ecf0f1;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  transition: background 0.3s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  background: #3498db;
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.user-name {
  font-weight: 500;
  font-size: 14px;
}

/* ===== 主体布局 ===== */
.main-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* ===== 左侧分类 ===== */
.left-category {
  background: #ffffff;
  border-right: 1px solid #e8ecf1;
  padding: 12px 0;
  overflow-y: auto;
  flex-shrink: 0;
}

.aside-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 20px 14px;
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #e8ecf1;
}

.aside-header .el-icon {
  font-size: 18px;
  color: #3498db;
}

.category-menu {
  border-right: none;
  margin-top: 8px;
}

.category-menu .el-menu-item {
  height: 44px;
  line-height: 44px;
  padding: 0 20px;
  border-radius: 8px;
  margin: 2px 8px;
  color: #5a6a7a;
}

.category-menu .el-menu-item:hover {
  background: #f0f4f8;
  color: #2c3e50;
}

.category-menu .el-menu-item.is-active {
  background: #e8f4fd;
  color: #3498db;
  font-weight: 500;
}

.category-menu .el-menu-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
}

/* ===== 内容区域 ===== */
.content-main {
  flex: 1;
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

/* ===== 右侧面板 ===== */
.right-panel {
  width: 280px !important;
  padding: 20px 16px;
  background: #f5f6fa;
  border-left: 1px solid #e8ecf1;
  overflow-y: auto;
  flex-shrink: 0;
}

.panel-card {
  border-radius: 12px;
  overflow: hidden;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 16px;
}

.panel-card :deep(.el-card__header) {
  padding: 14px 18px;
  border-bottom: 1px solid #f0f2f5;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: #2c3e50;
}

.overview-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 4px 0;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 12px;
  background: #f8f9fb;
  border-radius: 10px;
  transition: background 0.3s;
}

.overview-item:hover {
  background: #f0f4f8;
}

.overview-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.overview-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.overview-label {
  font-size: 13px;
  color: #8a9aa9;
}

.overview-value {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
}

.clock-btn {
  margin-top: 12px;
  background: linear-gradient(135deg, #2c3e50, #34495e);
  border: none;
  border-radius: 10px;
  height: 44px;
  font-weight: 600;
  letter-spacing: 1px;
}

.clock-btn:hover {
  background: linear-gradient(135deg, #34495e, #2c3e50);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(44, 62, 80, 0.3);
}

/* ===== 公告列表 ===== */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 4px 0;
}

.notice-item {
  display: flex;
  gap: 12px;
  padding: 10px 12px;
  background: #f8f9fb;
  border-radius: 10px;
  transition: all 0.3s;
  cursor: pointer;
}

.notice-item:hover {
  background: #f0f4f8;
  transform: translateX(4px);
}

.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3498db;
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-dot.top {
  background: #f56c6c;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-time {
  font-size: 12px;
  color: #aab8c5;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 2px;
}

.notice-time .el-icon {
  font-size: 14px;
}

.notice-empty {
  text-align: center;
  color: #aab8c5;
  padding: 16px 0;
  font-size: 14px;
}

/* ===== 公告详情弹窗 ===== */
.notice-detail-content {
  padding: 10px 0;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #8a9aa9;
  margin-bottom: 6px;
}

.detail-body {
  font-size: 15px;
  color: #2c3e50;
  line-height: 1.8;
  padding: 10px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* ===== 底部页脚 ===== */
.footer {
  text-align: center;
  margin-top: auto;
  padding: 16px 0 8px;
  color: #aab8c5;
  font-size: 13px;
  border-top: 1px solid #e8ecf1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.footer span {
  cursor: pointer;
  transition: color 0.3s;
}

.footer span:hover {
  color: #3498db;
}

.footer-divider {
  color: #dce1e8;
  cursor: default !important;
}

.footer-divider:hover {
  color: #dce1e8 !important;
}

/* ===== 路由切换动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .right-panel {
    width: 240px !important;
  }
}

@media (max-width: 992px) {
  .right-panel {
    display: none;
  }
  .search-input {
    width: 150px;
  }
}

@media (max-width: 768px) {
  .left-category {
    display: none;
  }
  .top-menu .el-menu-item span {
    display: none;
  }
  .top-menu .el-menu-item .el-icon {
    margin-right: 0;
  }
  .logo-text {
    font-size: 16px;
  }
}
</style>
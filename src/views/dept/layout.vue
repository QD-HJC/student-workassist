<template>
  <el-container style="height: 100vh; width:100%; background: #f0f2f5;">
    <!-- 左侧侧边栏 -->
    <el-aside width="220px" style="background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%); color: #fff; position: relative; flex-shrink: 0;">
      <!-- 侧边顶部 -->
      <div class="sidebar-top">
        <div class="sidebar-logo">
          <span class="logo-icon">🏢</span>
          <span class="logo-title">勤工助学</span>
        </div>
        <p class="logo-sub">用工部门端</p>
      </div>

      <!-- 侧边菜单 -->
      <el-menu
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.65)"
        active-text-color="#409EFF"
        :default-active="$route.path"
        class="sidebar-menu"
      >
        <el-menu-item index="/dept/home">
          <el-icon><HomeFilled /></el-icon>
          <span>系统主页</span>
        </el-menu-item>
        <el-menu-item index="/dept/postAdd">
          <el-icon><Plus /></el-icon>
          <span>发布岗位</span>
        </el-menu-item>
        <el-menu-item index="/dept/applyList">
          <el-icon><Document /></el-icon>
          <span>报名管理</span>
          <el-badge :value="pendingCount" class="menu-badge" v-if="pendingCount > 0" />
        </el-menu-item>
        <el-menu-item index="/dept/attendance">
          <el-icon><Clock /></el-icon>
          <span>考勤管理</span>
        </el-menu-item>
        <el-menu-item index="/dept/salary">
          <el-icon><Money /></el-icon>
          <span>薪资核算</span>
        </el-menu-item>
        <el-menu-item index="/dept/evaluate">
          <el-icon><ChatDotRound /></el-icon>
          <span>考核评价</span>
        </el-menu-item>
        <el-menu-item index="/dept/blacklist">
          <el-icon><Remove /></el-icon>
          <span>黑名单管理</span>
        </el-menu-item>
      </el-menu>

      <!-- 底部版本 -->
      <div class="sidebar-bottom">v2.0 正式版</div>
    </el-aside>

    <!-- 右侧内容 -->
    <el-container direction="vertical">
      <!-- 顶部导航 -->
      <el-header class="dept-top-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dept/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-center">
          <span class="time-display">
            <el-icon><Clock /></el-icon>
            {{ nowTime }}
          </span>
        </div>
        <div class="header-right">
          <el-badge :value="3" class="header-badge" :hidden="false">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" class="user-avatar" style="background: #409EFF;">
                {{ user?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ user?.realName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon> 个人信息
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出系统
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main style="overflow-y:auto; padding: 20px; background: #f0f2f5;">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getUserInfo, clearUserInfo } from '../../utils/storage'
import { ElMessage } from 'element-plus'
import {
  HomeFilled, Plus, Document, Clock, Money, ChatDotRound, Remove,
  Bell, User, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const user = ref(getUserInfo())
const nowTime = ref('')
let timer = null

const currentPageName = computed(() => {
  const map = {
    '/dept/home': '系统主页',
    '/dept/postAdd': '发布岗位',
    '/dept/applyList': '报名管理',
    '/dept/attendance': '考勤管理',
    '/dept/salary': '薪资核算',
    '/dept/evaluate': '考核评价',
    '/dept/blacklist': '黑名单管理'
  }
  return map[route.path] || '系统主页'
})

const pendingCount = ref(0)

const updateTime = () => {
  const date = new Date()
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const mi = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  nowTime.value = `${y}-${m}-${d} ${h}:${mi}:${s}`
}

const handleCommand = (command) => {
  if (command === 'logout') {
    clearUserInfo()
    router.push('/login')
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中...')
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  // 加载待处理数量
  // loadPendingCount()
})

onUnmounted(() => {
  clearInterval(timer)
})
</script>

<style scoped>
/* ===== 侧边栏 ===== */
.sidebar-top {
  padding: 24px 20px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.sidebar-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.logo-icon {
  font-size: 28px;
}

.logo-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 1px;
}

.logo-sub {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  margin: 4px 0 0;
  letter-spacing: 4px;
}

.sidebar-menu {
  border-right: none;
  padding: 12px 0;
}

.sidebar-menu .el-menu-item {
  height: 46px;
  line-height: 46px;
  padding: 0 20px;
  margin: 2px 12px;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.sidebar-menu .el-menu-item.is-active {
  background: rgba(64, 158, 255, 0.15);
  color: #409EFF;
}

.sidebar-menu .el-menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
}

.sidebar-menu .el-menu-item .menu-badge {
  margin-left: auto;
}

.sidebar-bottom {
  position: absolute;
  bottom: 16px;
  left: 0;
  width: 100%;
  text-align: center;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.15);
  letter-spacing: 2px;
}

/* ===== 顶部导航 ===== */
.dept-top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #ffffff;
  border-bottom: 1px solid #e8ecf1;
  height: 60px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

:deep(.el-breadcrumb) {
  font-size: 14px;
}

:deep(.el-breadcrumb .el-breadcrumb__item) {
  color: #5a6a7a;
}

:deep(.el-breadcrumb .el-breadcrumb__item:last-child) {
  color: #2c3e50;
  font-weight: 600;
}

.header-center {
  flex: 1;
  text-align: center;
}

.time-display {
  font-size: 14px;
  color: #5a6a7a;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #f5f7fa;
  padding: 4px 16px;
  border-radius: 16px;
}

.time-display .el-icon {
  font-size: 16px;
  color: #8a9aa9;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-badge {
  cursor: pointer;
}

.header-badge :deep(.el-badge__content) {
  background: #f56c6c;
  border: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  cursor: pointer;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f0f2f5;
}

.user-avatar {
  font-weight: 600;
  font-size: 16px;
  flex-shrink: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

/* ===== 路由动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-12px);
}
</style>
<template>
  <el-container style="height: 100vh;">
    <!-- 左侧侧边栏 -->
    <el-aside width="220px" style="background: linear-gradient(180deg, #0c0e1a 0%, #1a1a2e 100%); color: #fff; position: relative; flex-shrink: 0;">
      <div class="sidebar-top">
        <div class="sidebar-logo">
          <span class="logo-icon">🏛️</span>
          <span class="logo-title">资助中心</span>
        </div>
        <p class="logo-sub">管理端</p>
      </div>

      <el-menu
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.65)"
        active-text-color="#409EFF"
        :default-active="$route.path"
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/home">
          <el-icon><HomeFilled /></el-icon>
          <span>系统首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/postAudit">
          <el-icon><Document /></el-icon>
          <span>岗位审核</span>
          <!-- ❌ 移除红点 -->
        </el-menu-item>
        <el-menu-item index="/admin/salaryAudit">
          <el-icon><Money /></el-icon>
          <span>薪资审核</span>
          <!-- ❌ 移除红点 -->
        </el-menu-item>
        <el-menu-item index="/admin/blacklist">
          <el-icon><Remove /></el-icon>
          <span>黑名单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/admin/notice">
          <el-icon><Bell /></el-icon>
          <span>发布公告</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-bottom">v2.0 管理端</div>
    </el-aside>

    <!-- 右侧 -->
    <el-container>
      <el-header class="admin-top-header">
        <div class="header-left">
          <span class="page-title-text">{{ currentPageName }}</span>
        </div>
        <div class="header-right">
          <span class="time-display">
            <el-icon><Clock /></el-icon>
            {{ nowTime }}
          </span>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" style="background: #409EFF; font-weight: 600;">
                {{ userInfo?.realName?.charAt(0) || 'A' }}
              </el-avatar>
              <span class="user-name">{{ userInfo?.realName || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main style="background: #f0f2f5; padding: 20px; overflow-y: auto;">
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
import { HomeFilled, Document, Money, Remove, DataAnalysis, Clock, ArrowDown, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userInfo = ref(getUserInfo())
const nowTime = ref('')
let timer = null

const currentPageName = computed(() => {
  const map = {
    '/admin/home': '系统首页',
    '/admin/postAudit': '岗位审核',
    '/admin/salaryAudit': '薪资审核',
    '/admin/blacklist': '黑名单管理',
    '/admin/statistics': '数据统计'
  }
  return map[route.path] || '系统首页'
})

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
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
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

.sidebar-bottom {
  position: absolute;
  bottom: 16px;
  left: 0;
  width: 100%;
  text-align: center;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.12);
  letter-spacing: 2px;
}

/* ===== 顶部 ===== */
.admin-top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #e8ecf1;
  height: 60px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title-text {
  font-size: 18px;
  font-weight: 700;
  color: #2c3e50;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.time-display {
  font-size: 14px;
  color: #5a6a7a;
  display: flex;
  align-items: center;
  gap: 6px;
  background: #f5f7fa;
  padding: 4px 16px;
  border-radius: 16px;
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

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
}

/* ===== 动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
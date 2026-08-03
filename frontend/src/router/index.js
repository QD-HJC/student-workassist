import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getUserInfo } from '../utils/storage'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')  // ✅ 使用 ../
  },
  {
    path: '/register',
    component: () => import('../views/register/index.vue')
  },
  {
    path: '/student',
    redirect: '/student/home',
    name: 'Student',
    component: () => import('../views/student/layout.vue'),
    meta: { role: '学生' },
    children: [
      { path: 'home', component: () => import('../views/student/home.vue') },
      { path: 'post', component: () => import('../views/student/postList.vue') },
      { path: 'myApply', component: () => import('../views/student/myApply.vue') },
      { path: 'salary', component: () => import('../views/student/Salary.vue') },
      { path: 'evaluate', component: () => import('../views/student/Evaluate.vue') },
      { path: 'attendance', component: () => import('../views/student/Attendance.vue') }
    ]
  },
  {
    path: '/dept',
    name: 'Dept',
    component: () => import('../views/dept/layout.vue'),  // ✅ 使用 ../
    meta: { role: '用工部门' },
    children: [
      { path: 'home', component: () => import('../views/dept/home.vue') },
      { path: 'postAdd', component: () => import('../views/dept/postAdd.vue') },
      { path: 'applyList', component: () => import('../views/dept/applyList.vue') },
      { path: 'attendance', component: () => import('../views/dept/attendance.vue') },
      { path: 'salary', component: () => import('../views/dept/salary.vue') },
      { path: 'evaluate', component: () => import('../views/dept/evaluate.vue') },
      { path: 'blacklist', component: () => import('../views/dept/blacklist.vue') }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/layout.vue'),  // ✅ 使用 ../
    meta: { role: '资助中心' },
    children: [
      { path: 'home', component: () => import('../views/admin/index.vue') },
      { path: 'postAudit', component: () => import('../views/admin/postAudit.vue') },
      { path: 'salaryAudit', component: () => import('../views/admin/salaryAudit.vue') },
      { path: 'blacklist', component: () => import('../views/admin/blacklist.vue') },
      { path: 'statistics', component: () => import('../views/admin/statistics.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  const user = getUserInfo()

  console.log('当前访问路由：', to.path)
  console.log('本地存储用户信息：', user)

  if (!token) {
    if (to.path === '/login') {
      return next()
    }
    return next('/login')
  }

  let requiredRole = null
  to.matched.forEach(routeRecord => {
    if (routeRecord.meta?.role) {
      requiredRole = routeRecord.meta.role
    }
  })

  console.log('该路由需要的角色：', requiredRole)

  if (requiredRole) {
    if (!user || user.role !== requiredRole) {
      ElMessage.error('权限不足，无法访问该页面')
      return next('/login')
    }
  }

  next()
})

export default router
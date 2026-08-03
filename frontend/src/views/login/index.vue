<template>
  <div class="login-container">
    <!-- 背景动画 -->
    <div class="bg-animation">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>

    <!-- 语言切换 -->
    <div class="lang-switch">
      <el-button-group>
        <el-button :type="locale === 'zh' ? 'primary' : ''" size="small" @click="switchLang('zh')">中文</el-button>
        <el-button :type="locale === 'en' ? 'primary' : ''" size="small" @click="switchLang('en')">English</el-button>
      </el-button-group>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">📚</div>
        <h1 class="login-title">{{ $t('login.title') }}</h1>
        <p class="login-subtitle">{{ $t('login.subTitle') }}</p>
      </div>

      <el-form ref="loginRef" :model="loginForm" :rules="rules" class="login-form" @keyup.enter="login">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            :placeholder="$t('login.username')" 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            :placeholder="$t('login.password')" 
            show-password 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="role">
          <el-select 
            v-model="loginForm.role" 
            :placeholder="$t('login.role')" 
            size="large"
            class="custom-select"
          >
            <el-option :label="$t('login.roleStudent')" value="学生">
              <span class="option-label">👨‍🎓 {{ $t('login.roleStudent') }}</span>
            </el-option>
            <el-option :label="$t('login.roleDept')" value="用工部门">
              <span class="option-label">🏢 {{ $t('login.roleDept') }}</span>
            </el-option>
            <el-option :label="$t('login.roleAdmin')" value="资助中心">
              <span class="option-label">🏛️ {{ $t('login.roleAdmin') }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="login" 
            :loading="loading"
            class="login-btn"
          >
            {{ loading ? $t('login.loggingIn') : $t('login.loginBtn') }}
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span class="footer-text">{{ $t('login.noAccount') }}</span>
          <span class="footer-link" @click="$router.push('/register')">{{ $t('login.registerNow') }}</span>
        </div>

        <div class="demo-hint">
          <el-icon><InfoFilled /></el-icon>
          {{ $t('login.demoHint') }}
        </div>
      </el-form>
    </div>

    <div class="deco-badge">v2.0</div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { loginApi } from '../../api/auth'
import { setToken, setUserInfo } from '../../utils/storage'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, InfoFilled } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const router = useRouter()
const { locale, t } = useI18n()
const loading = ref(false)
const loginRef = ref(null)

const loginForm = reactive({
  username: 'stu001',
  password: '123456',
  role: '学生'
})

const rules = {
  username: [
    { required: true, message: t('login.usernameRequired'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('login.passwordRequired'), trigger: 'blur' }
  ],
  role: [
    { required: true, message: t('login.roleRequired'), trigger: 'change' }
  ]
}

const switchLang = (lang) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

const login = async () => {
  if (!loginRef.value) return
  const valid = await loginRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await loginApi(loginForm)
    if (res.code === 200) {
      const user = res.data.user
      if (user.role === '用工部门' && !user.deptName) {
        user.deptName = '图书馆'
        user.deptId = user.userId
      }
      
      setToken(res.data.token)
      setUserInfo(user)
      
      ElMessage.success(t('login.loginSuccess', { name: user.realName || '用户' }))
      
      setTimeout(() => {
        if (user.role === '学生') {
          router.push('/student/home')
        } else if (user.role === '用工部门') {
          router.push('/dept/home')
        } else if (user.role === '资助中心') {
          router.push('/admin/home')
        } else {
          router.push('/student/home')
        }
      }, 300)
    } else {
      ElMessage.error(res.msg || t('login.loginFailed'))
    }
  } catch (err) {
    console.error('登录错误:', err)
    ElMessage.error(t('login.networkError'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0c0e1a 0%, #1a1a2e 30%, #16213e 60%, #0f3460 100%);
  position: relative;
  overflow: hidden;
}

/* 背景动画 */
.bg-animation {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 500px;
  height: 500px;
  top: -150px;
  right: -100px;
  background: rgba(64, 158, 255, 0.05);
  animation-delay: 0s;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  background: rgba(103, 194, 58, 0.04);
  animation-delay: -5s;
}

.circle-3 {
  width: 300px;
  height: 300px;
  top: 30%;
  left: 60%;
  background: rgba(230, 162, 60, 0.03);
  animation-delay: -10s;
}

.circle-4 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  right: 10%;
  background: rgba(245, 108, 108, 0.04);
  animation-delay: -15s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.05); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(40px, 30px) scale(1.02); }
}

/* 语言切换 */
.lang-switch {
  position: absolute;
  top: 24px;
  right: 32px;
  z-index: 20;
}

.lang-switch .el-button {
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.5);
}

.lang-switch .el-button.is-active {
  background: rgba(64, 158, 255, 0.2);
  border-color: rgba(64, 158, 255, 0.3);
  color: #409EFF;
}

/* 登录卡片 */
.login-card {
  width: 440px;
  padding: 48px 40px 36px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.10);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 10;
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.6);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: inline-block;
  animation: pulse 2s infinite ease-in-out;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #ffffff 30%, #74b9ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.3);
  margin: 4px 0 0;
  letter-spacing: 6px;
  font-weight: 300;
}

/* 表单 */
.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.custom-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: none;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.3);
  background: rgba(255, 255, 255, 0.08);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(64, 158, 255, 0.5);
  background: rgba(255, 255, 255, 0.08);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.08);
}

.custom-input :deep(.el-input__inner) {
  color: #ecf0f1;
  font-size: 15px;
  height: 48px;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.3);
}

.custom-input :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.3);
  font-size: 18px;
}

.custom-select :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: none;
}

.custom-select :deep(.el-input__wrapper:hover) {
  border-color: rgba(64, 158, 255, 0.3);
  background: rgba(255, 255, 255, 0.08);
}

.custom-select :deep(.el-input__wrapper.is-focus) {
  border-color: rgba(64, 158, 255, 0.5);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.08);
}

.custom-select :deep(.el-input__inner) {
  color: #ecf0f1;
  font-size: 15px;
  height: 48px;
}

.custom-select :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.3);
}

.custom-select :deep(.el-select__caret) {
  color: rgba(255, 255, 255, 0.3);
  font-size: 18px;
}

.option-label {
  font-size: 15px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #409EFF 0%, #2d7fd3 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
  margin-top: 4px;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.35);
}

.login-btn.is-disabled {
  background: rgba(64, 158, 255, 0.3);
  box-shadow: none;
}

.login-footer {
  text-align: center;
  margin-top: 16px;
}

.footer-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.4);
}

.footer-link {
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
  margin-left: 4px;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.demo-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 14px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.2);
}

.demo-hint .el-icon {
  font-size: 14px;
}

/* 错误信息 */
.login-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: rgba(245, 108, 108, 0.5);
  background: rgba(245, 108, 108, 0.06);
}

.login-form :deep(.el-form-item .el-form-item__error) {
  color: rgba(245, 108, 108, 0.7);
  font-size: 12px;
  padding-top: 2px;
}

/* 下拉菜单 */
:deep(.el-select-dropdown) {
  background: rgba(30, 30, 50, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
}

:deep(.el-select-dropdown .el-select-dropdown__item) {
  color: #ecf0f1;
  padding: 10px 20px;
}

:deep(.el-select-dropdown .el-select-dropdown__item:hover) {
  background: rgba(255, 255, 255, 0.06);
}

:deep(.el-select-dropdown .el-select-dropdown__item.is-selected) {
  background: rgba(64, 158, 255, 0.15);
  color: #409EFF;
}

.deco-badge {
  position: absolute;
  bottom: 24px;
  right: 32px;
  z-index: 10;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.06);
  letter-spacing: 2px;
  font-weight: 300;
}

/* 响应式 */
@media (max-width: 500px) {
  .login-card {
    width: 92%;
    padding: 32px 24px 28px;
    margin: 0 16px;
  }
  
  .login-title {
    font-size: 22px;
  }
  
  .logo-icon {
    font-size: 36px;
  }
  
  .login-btn {
    height: 46px;
    font-size: 16px;
  }

  .lang-switch {
    top: 16px;
    right: 16px;
  }
}
</style>
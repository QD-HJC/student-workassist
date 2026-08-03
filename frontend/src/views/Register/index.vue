<template>
  <div class="register-container">
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

    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="register-header">
        <div class="logo-icon">📚</div>
        <h1 class="register-title">{{ $t('register.title') }}</h1>
        <p class="register-subtitle">{{ $t('app.subTitle') }}</p>
      </div>

      <el-form 
        ref="registerRef" 
        :model="registerForm" 
        :rules="rules" 
        class="register-form"
        @keyup.enter="register"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="registerForm.username" 
            :placeholder="$t('register.username')" 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="realName">
          <el-input 
            v-model="registerForm.realName" 
            :placeholder="$t('register.realName')" 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Edit /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phone">
          <el-input 
            v-model="registerForm.phone" 
            :placeholder="$t('register.phone')" 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input 
            v-model="registerForm.password" 
            :placeholder="$t('register.password')" 
            show-password 
            size="large"
            class="custom-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="registerForm.confirmPassword" 
            :placeholder="$t('register.confirmPassword')" 
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
            v-model="registerForm.role" 
            :placeholder="$t('register.role')" 
            size="large"
            class="custom-select"
          >
            <el-option :label="$t('register.roleStudent')" value="学生">
              <span class="option-label">👨‍🎓 {{ $t('register.roleStudent') }}</span>
            </el-option>
            <el-option :label="$t('register.roleDept')" value="用工部门">
              <span class="option-label">🏢 {{ $t('register.roleDept') }}</span>
            </el-option>
            <el-option :label="$t('register.roleAdmin')" value="资助中心">
              <span class="option-label">🏛️ {{ $t('register.roleAdmin') }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="register" 
            :loading="loading"
            class="register-btn"
          >
            {{ loading ? $t('register.registering') : $t('register.registerBtn') }}
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span class="footer-text">{{ $t('register.haveAccount') }}</span>
          <span class="footer-link" @click="$router.push('/login')">{{ $t('register.loginNow') }}</span>
        </div>

        <div class="demo-hint">
          <el-icon><InfoFilled /></el-icon>
          {{ $t('register.demoHint') }}
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Edit, Phone, InfoFilled } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import request from '../../utils/request'

const router = useRouter()
const { locale, t } = useI18n()
const loading = ref(false)
const registerRef = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  role: ''
})

// 验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error(t('register.confirmPasswordMatch')))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error(t('register.phoneInvalid')))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: t('register.usernameRequired'), trigger: 'blur' },
    { min: 3, max: 20, message: t('register.usernameMin'), trigger: 'blur' }
  ],
  realName: [
    { required: true, message: t('register.realNameRequired'), trigger: 'blur' }
  ],
  phone: [
    { required: true, message: t('register.phoneRequired'), trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('register.passwordRequired'), trigger: 'blur' },
    { min: 6, max: 20, message: t('register.passwordMin'), trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: t('register.confirmPasswordRequired'), trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  role: [
    { required: true, message: t('register.roleRequired'), trigger: 'change' }
  ]
}

// 切换语言
const switchLang = (lang) => {
  locale.value = lang
  localStorage.setItem('lang', lang)
}

// 注册
const register = async () => {
  if (!registerRef.value) return
  const valid = await registerRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // 调用注册接口
    const res = await request({
      url: '/api/user/register',
      method: 'post',
      data: {
        username: registerForm.username,
        password: registerForm.password,
        realName: registerForm.realName,
        phone: registerForm.phone,
        role: registerForm.role
      }
    })
    
    if (res.code === 200) {
      ElMessage.success(t('register.registerSuccess'))
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error(res.msg || t('register.registerFailed'))
    }
  } catch (err) {
    console.error('注册错误:', err)
    ElMessage.error(t('register.networkError'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
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

/* 注册卡片 */
.register-card {
  width: 480px;
  padding: 40px 36px 32px;
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.10);
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.5);
  position: relative;
  z-index: 10;
  max-height: 90vh;
  overflow-y: auto;
}

.register-card::-webkit-scrollbar {
  width: 4px;
}

.register-card::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
}

.register-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo-icon {
  font-size: 40px;
  margin-bottom: 8px;
  display: inline-block;
}

.register-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
  letter-spacing: 2px;
}

.register-subtitle {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.3);
  margin: 2px 0 0;
  letter-spacing: 4px;
}

/* 表单 */
.register-form :deep(.el-form-item) {
  margin-bottom: 18px;
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
  font-size: 14px;
  height: 44px;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.3);
}

.custom-input :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.3);
  font-size: 16px;
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
  font-size: 14px;
  height: 44px;
}

.custom-select :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.3);
}

.custom-select :deep(.el-select__caret) {
  color: rgba(255, 255, 255, 0.3);
}

.option-label {
  font-size: 14px;
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #409EFF 0%, #2d7fd3 100%);
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
  margin-top: 4px;
}

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.35);
}

.register-footer {
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
  margin-left: 6px;
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
.register-form :deep(.el-form-item.is-error .el-input__wrapper) {
  border-color: rgba(245, 108, 108, 0.5);
  background: rgba(245, 108, 108, 0.06);
}

.register-form :deep(.el-form-item .el-form-item__error) {
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

/* 响应式 */
@media (max-width: 520px) {
  .register-card {
    width: 92%;
    padding: 28px 20px 24px;
    margin: 0 16px;
  }
  
  .register-title {
    font-size: 20px;
  }
  
  .logo-icon {
    font-size: 32px;
  }
  
  .register-btn {
    height: 44px;
    font-size: 15px;
  }

  .lang-switch {
    top: 16px;
    right: 16px;
  }
}
</style>
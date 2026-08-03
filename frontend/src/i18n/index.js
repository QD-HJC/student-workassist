import { createI18n } from 'vue-i18n'
import zh from '../locales/zh.js'
import en from '../locales/en.js'

// 从 localStorage 获取语言设置
const savedLang = localStorage.getItem('lang') || 'zh'

const i18n = createI18n({
  legacy: false,
  locale: savedLang,
  fallbackLocale: 'zh',
  messages: {
    zh,
    en
  }
})

export default i18n
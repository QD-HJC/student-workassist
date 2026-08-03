import axios from 'axios'
import { getToken, removeToken } from './storage'
import { ElMessage } from 'element-plus'
import router from '../router'
const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})
// 请求拦截
service.interceptors.request.use(config => {
  const token = getToken()
  if (token) config.headers.token = token
  return config
})
// 响应拦截
service.interceptors.response.use(
  res => res.data,
  err => {
    // 先判断响应对象是否存在，防止undefined报错
    if (!err.response) {
      ElMessage.error('后端服务连接失败，请检查后端是否启动')
      return Promise.reject(err)
    }
    // 只有存在response时才读取status
    if(err.response.status === 401){
      ElMessage.error('登录失效，请重新登录')
      clearUserInfo()
      location.href = '/login'
    } else {
      ElMessage.error('请求异常：' + err.response.status)
    }
    return Promise.reject(err)
  }
)
export default service
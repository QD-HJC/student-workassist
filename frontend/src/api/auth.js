import request from '../utils/request'

// 登录接口
export function loginApi(data) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}
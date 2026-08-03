import request from '../utils/request'

// 获取部门首页统计数字
export function getDeptHomeCount() {
  return request({
    url: '/api/dept/home/overview',
    method: 'get'
  })
}
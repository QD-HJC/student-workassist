import request from '../utils/request'
// 查询学生打卡记录
export function getClockList(studentId) {
  return request({
    url: '/api/attendance/list',
    method: 'get',
    params: { studentId }
  })
}

// 上下班打卡提交
export function addClock(data) {
  return request({
    url: '/api/attendance/save',
    method: 'post',
    data
  })
}
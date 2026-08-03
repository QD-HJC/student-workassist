import request from '../utils/request'
export function getStudentOverview(studentId) {
  return request({
    url: '/api/student/overview',
    method: 'get',
    params: { studentId }
  })
}
// 获取学生当前在岗岗位
export function getRunningPost(studentId) {
  return request({
    url: '/api/student/runningPost',
    method: 'get',
    params: { studentId }
  })
}
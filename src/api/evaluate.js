import request from '../utils/request'
// 获取学生在岗岗位（用于新增评价下拉）
export function getMyPost(studentId) {
  return request({
    url: '/api/student/myPost',
    method: 'get',
    params: { studentId }
  })
}
// 查询评价记录
export function getEvaluateList(studentId) {
  return request({
    url: '/api/evaluate/list',
    method: 'get',
    params: { studentId }
  })
}
// 提交评价
export function addEvaluate(data) {
  return request({
    url: '/api/evaluate/save',
    method: 'post',
    data
  })
}
export function delEvaluate(evalId) {
  return request({
    url: `/api/evaluate/delete/${evalId}`,
    method: 'delete'
  })
}

// 修改评价
export function updateEvaluate(data) {
  return request({
    url: '/api/evaluate/update',
    method: 'put',
    data
  })
}

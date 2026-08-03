import request from '../utils/request'
export function getStudentSalary(studentId) {
  return request({
    url: '/api/salary/my',
    method: 'get',
    params: { studentId }
  })
}

// 管理员分页薪资
export function getSalaryPage(params) {
  return request({
    url: '/api/salary/page',
    method: 'get',
    params
  })
}

// 新增薪资
export function saveSalary(data) {
  return request({
    url: '/api/salary/save',
    method: 'post',
    data
  })
}
export function updateSalary(data) {
  return request({
    url: '/api/salary/update',
    method: 'put',
    data
  })
}

// 删除薪资
export function delSalary(id) {
  return request({
    url: `/api/salary/delete/${id}`,
    method: 'delete'
  })
}

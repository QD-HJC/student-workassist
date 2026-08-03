import request from '../utils/request'

// 提交报名
export function saveApply(data) {
    return request({
        url: '/api/apply/save',
        method: 'post',
        data
    })
}

// 根据岗位id查询报名列表
export function getApplyByPost(postId) {
    return request({
        url: `/api/apply/getByPost/${postId}`,
        method: 'get'
    })
}

// 审核报名
export function auditApply(params) {
    return request({
        url: '/api/apply/audit',
        method: 'put',
        params
    })
}

// 取消报名
export function deleteApply(applyId) {
    return request({
        url: `/api/apply/delete/${applyId}`,
        method: 'delete'
    })
}

// 查询当前学生自己的报名记录
export function getMyApply(studentId) {
    return request({
        url: '/api/apply/my',
        method: 'get',
        params: { studentId }
    })
}
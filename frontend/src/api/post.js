import request from '../utils/request'

// 分页查询岗位
export function getPostPage(params) {
    return request({
        url: '/api/post/page',
        method: 'get',
        params
    })
}

// 不分页查询全部岗位（首页最新岗位）
export function getNewPostList() {
    return request({ 
        url: '/api/post/allData', 
        method: 'get' 
    })
}

// 根据部门分页查询岗位
export function getPostByDept(deptId) {
    return request({
        url: '/api/post/dept',
        method: 'get',
        params: { 
            deptId, 
            current: 1, 
            size: 100 
        }
    })
}

// 新增岗位
export function savePost(data) {
    return request({
        url: '/api/post/save',  // ✅ 加上 /api
        method: 'post',
        data
    })
}

// 审核岗位
export function auditPost(postId, status) {
    return request({
        url: '/api/post/audit',  // ✅ 加上 /api
        method: 'put',
        params: { postId, status }
    })
}

// 删除岗位
export function deletePost(postId) {
    return request({
        url: `/api/post/delete/${postId}`,  // ✅ 加上 /api
        method: 'delete'
    })
}

// 获取岗位分类列表
export function getPostTypeList() {
    return request({
        url: '/api/post/category/list',
        method: 'get'
    })
}

// 上传岗位图片
export function uploadPostImg(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
        url: '/api/upload/postImg',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
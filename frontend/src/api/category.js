import request from '../utils/request'
export function getCategoryList() {
  return request({
    url: '/api/category/list',
    method: 'get'
  })

}
export function getPostTypeList() {
  return request({
    url: '/api/post/category/list',
    method: 'get'
  })}
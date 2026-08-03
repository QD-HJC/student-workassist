import request from '../utils/request'
export function getNoticeList() {
  return request({
    url: '/api/notice/list',
    method: 'get'
  })
}
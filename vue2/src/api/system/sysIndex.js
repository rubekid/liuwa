import request from '@/utils/request'

// 首页数据
export function index(query) {
  return request({
    url: '/',
    method: 'get'
  })
}

import request from '@/utils/request'

// 查询公告列表(发布者列表)
export function listNotice(query) {
  return request({
    url: '/system/notice/list',
    method: 'get',
    params: query
  })
}
// 查询公告列表(用户列表)
export function listReadNotice(query) {
  return request({
    url: '/system/notice/readList',
    method: 'get',
    params: query
  })
}
// 查询公告列表(未读列表)
export function listUnReadNotice(query) {
  return request({
    url: '/system/notice/unReadList',
    method: 'get',
    params: query
  })
}

// 查询公告详细
export function getNotice(noticeId) {
  return request({
    url: '/system/notice/' + noticeId,
    method: 'get'
  })
}

// 新增公告
export function addNotice(data) {
  return request({
    url: '/system/notice',
    method: 'post',
    data: data
  })
}

// 修改公告
export function updateNotice(data) {
  return request({
    url: '/system/notice',
    method: 'put',
    data: data
  })
}

// 删除公告
export function delNotice(noticeId) {
  return request({
    url: '/system/notice/' + noticeId,
    method: 'delete'
  })
}

// 查询公告未读数量
export function getUnReadCount() {
  return request({
    url: '/system/read/getUnReadCount',
    method: 'get'
  })
}

// 查询公告详细
export function changeReadStatus(noticeId) {
  return request({
    url: '/system/notice/changeReadStatus/' + noticeId,
    method: 'get'
  })
}

// 全部设置为已读
export function setAllNoticeRead(ids) {
  return request({
    url: '/system/notice/setAllNoticeRead/' + ids,
    method: 'get'
  })
}


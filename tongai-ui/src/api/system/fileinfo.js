import request from '@/utils/request'

// 查询文件管理列表
export function listFileinfo(query) {
  return request({
    url: '/system/fileinfo/list',
    method: 'get',
    params: query
  })
}

// 查询文件管理详细
export function getFileinfo(fileId) {
  return request({
    url: '/system/fileinfo/' + fileId,
    method: 'get'
  })
}

// 新增文件管理
export function addFileinfo(data) {
  return request({
    url: '/system/fileinfo',
    method: 'post',
    data: data
  })
}

// 修改文件管理
export function updateFileinfo(data) {
  return request({
    url: '/system/fileinfo',
    method: 'put',
    data: data
  })
}

// 删除文件管理
export function delFileinfo(fileId) {
  return request({
    url: '/system/fileinfo/' + fileId,
    method: 'delete'
  })
}

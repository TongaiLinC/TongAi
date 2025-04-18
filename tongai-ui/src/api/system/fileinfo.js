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

// 分片上传文件管理
export function checkFile(data) {
  return request({
    url: '/common/checkFile',
    method: 'post',
    data: data
  })
}

// 分片上传文件管理
export function upload(data, onUploadProgress) {
  const config = {}

  if (onUploadProgress) {
    config.onUploadProgress = onUploadProgress
  }
  return request({
    url: '/common/upload',
    method: 'post',
    data: data,
    ...config
  })
}

// 分片上传文件管理
export function uploadChunk(data) {
  return request({
    url: '/common/uploadChunk',
    method: 'post',
    headers: {
      repeatSubmit: false
    },
    data: data
  })
}

// 合并分片上传文件管理
export function mergeChunks(data) {
  return request({
    url: '/common/mergeChunks',
    method: 'post',
    data: data
  })
}

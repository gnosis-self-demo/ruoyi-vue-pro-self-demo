import request from '../utils/request'

export const getList = (data) => request({ url: '/process-orchestration/resource-binding/list', method: 'post', data })
export const getDetail = (data) => request({ url: '/process-orchestration/resource-binding/detail', method: 'post', data })
export const save = (data) => request({ url: '/process-orchestration/resource-binding/save', method: 'post', data })
export const update = (data) => request({ url: '/process-orchestration/resource-binding/update', method: 'post', data })
export const remove = (data) => request({ url: '/process-orchestration/resource-binding/delete', method: 'post', data })
export const batchDelete = (data) => request({ url: '/process-orchestration/resource-binding/batchDelete', method: 'post', data })
export const batchEnable = (data) => request({ url: '/process-orchestration/resource-binding/batchEnable', method: 'post', data })
export const batchDisable = (data) => request({ url: '/process-orchestration/resource-binding/batchDisable', method: 'post', data })
export const importData = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/process-orchestration/resource-binding/import', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
export const exportData = (data) => request({ url: '/process-orchestration/resource-binding/export', method: 'post', data, responseType: 'blob' })

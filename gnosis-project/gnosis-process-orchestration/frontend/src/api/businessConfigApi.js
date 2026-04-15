import request from '../utils/request'

export const getList = (data) => request({ url: '/process-orchestration/business-config/list', method: 'post', data })
export const getDetail = (data) => request({ url: '/process-orchestration/business-config/detail', method: 'post', data })
export const save = (data) => request({ url: '/process-orchestration/business-config/save', method: 'post', data })
export const update = (data) => request({ url: '/process-orchestration/business-config/update', method: 'post', data })
export const remove = (data) => request({ url: '/process-orchestration/business-config/delete', method: 'post', data })
export const batchDelete = (data) => request({ url: '/process-orchestration/business-config/batchDelete', method: 'post', data })
export const batchEnable = (data) => request({ url: '/process-orchestration/business-config/batchEnable', method: 'post', data })
export const batchDisable = (data) => request({ url: '/process-orchestration/business-config/batchDisable', method: 'post', data })
export const importData = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/process-orchestration/business-config/import', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
export const exportData = (data) => request({ url: '/process-orchestration/business-config/export', method: 'post', data, responseType: 'blob' })

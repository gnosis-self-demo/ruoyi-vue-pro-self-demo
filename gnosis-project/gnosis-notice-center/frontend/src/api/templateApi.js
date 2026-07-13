import request from '../utils/request'

export const templateApi = {
  pageList: (data) => request.post('/template/page', data),
  detail: (id) => request.post('/template/detail', { id }),
  create: (data) => request.post('/template/create', data),
  update: (data) => request.post('/template/update', data),
  delete: (id) => request.post('/template/delete', { id }),
  batchDelete: (ids) => request.post('/template/batchDelete', ids),
  batchEnable: (ids) => request.post('/template/batchEnable', ids),
  batchDisable: (ids) => request.post('/template/batchDisable', ids),

  // 导出模板
  export: (data) => request.post('/template/export', data, {
    responseType: 'blob',
  }),

  // 导入模板
  import: (formData) => request.post('/template/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
}

import request from '../utils/request';

// 文件管理 API
export const fileApi = {
  pageList: (data) => request.post('/file/page', data),
  detail: (id) => request.post('/file/detail', { id }),
  create: (data) => request.post('/file/create', data),
  update: (data) => request.post('/file/update', data),
  delete: (id) => request.post('/file/delete', { id }),
  batchDelete: (ids) => request.post('/file/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/file/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/file/batchDisable', { ids }),
  export: (ids) => request.post('/file/export', { ids }),
  import: (formData) => request.post('/file/import', formData),
};

// 模板管理 API
export const templateApi = {
  pageList: (data) => request.post('/template/page', data),
  detail: (id) => request.post('/template/detail', { id }),
  create: (data) => request.post('/template/create', data),
  update: (data) => request.post('/template/update', data),
  delete: (id) => request.post('/template/delete', { id }),
  batchDelete: (ids) => request.post('/template/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/template/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/template/batchDisable', { ids }),
  export: (ids) => request.post('/template/export', { ids }),
  import: (formData) => request.post('/template/import', formData),
};

// 流程管理 API
export const processApi = {
  pageList: (data) => request.post('/process/page', data),
  detail: (id) => request.post('/process/detail', { id }),
  create: (data) => request.post('/process/create', data),
  update: (data) => request.post('/process/update', data),
  delete: (id) => request.post('/process/delete', { id }),
  batchDelete: (ids) => request.post('/process/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/process/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/process/batchDisable', { ids }),
  export: (ids) => request.post('/process/export', { ids }),
  import: (formData) => request.post('/process/import', formData),
};

// 印章管理 API
export const sealApi = {
  pageList: (data) => request.post('/seal/page', data),
  detail: (id) => request.post('/seal/detail', { id }),
  create: (data) => request.post('/seal/create', data),
  update: (data) => request.post('/seal/update', data),
  delete: (id) => request.post('/seal/delete', { id }),
  batchDelete: (ids) => request.post('/seal/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/seal/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/seal/batchDisable', { ids }),
  export: (ids) => request.post('/seal/export', { ids }),
  import: (formData) => request.post('/seal/import', formData),
};

// 供应商管理 API
export const supplierApi = {
  pageList: (data) => request.post('/supplier/page', data),
  detail: (id) => request.post('/supplier/detail', { id }),
  create: (data) => request.post('/supplier/create', data),
  update: (data) => request.post('/supplier/update', data),
  delete: (id) => request.post('/supplier/delete', { id }),
  batchDelete: (ids) => request.post('/supplier/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/supplier/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/supplier/batchDisable', { ids }),
  export: (ids) => request.post('/supplier/export', { ids }),
  import: (formData) => request.post('/supplier/import', formData),
};

// 证书管理 API
export const certificateApi = {
  pageList: (data) => request.post('/certificate/page', data),
  detail: (id) => request.post('/certificate/detail', { id }),
  create: (data) => request.post('/certificate/create', data),
  update: (data) => request.post('/certificate/update', data),
  delete: (id) => request.post('/certificate/delete', { id }),
  batchDelete: (ids) => request.post('/certificate/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/certificate/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/certificate/batchDisable', { ids }),
  export: (ids) => request.post('/certificate/export', { ids }),
  import: (formData) => request.post('/certificate/import', formData),
};

// 审计日志管理 API
export const auditLogApi = {
  pageList: (data) => request.post('/audit-log/page', data),
  detail: (id) => request.post('/audit-log/detail', { id }),
  batchDelete: (ids) => request.post('/audit-log/batchDelete', { ids }),
  export: (ids) => request.post('/audit-log/export', { ids }),
};

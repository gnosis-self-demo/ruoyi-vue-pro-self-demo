import axios from 'axios';

const api = axios.create({
  baseURL: '/api/paramcheck',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.response.use(
  response => {
    const data = response.data;
    if (data.code === 200) {
      return data;
    }
    return Promise.reject(new Error(data.message || '请求失败'));
  },
  error => {
    return Promise.reject(error);
  }
);

export const businessTypeApi = {
  getPage: (params) => api.get('/business-types/page', { params }),
  getList: () => api.get('/business-types'),
  getActiveList: () => api.get('/business-types/active'),
  getByCode: (code) => api.get(`/business-types/${code}`),
  create: (data) => api.post('/business-types', data),
  update: (data) => api.put('/business-types', data),
  delete: (code) => api.delete(`/business-types/${code}`),
  batchDelete: (codes) => api.post('/business-types/batch/delete', codes),
  batchActivate: (codes) => api.post('/business-types/batch/activate', codes),
  batchDeactivate: (codes) => api.post('/business-types/batch/deactivate', codes),
  exportData: () => api.get('/business-types/export', { responseType: 'blob' }),
  importData: (data) => api.post('/business-types/import', data)
};

export const flowConfigApi = {
  getPage: (params) => api.get('/config/flows/page', { params }),
  getActiveList: () => api.get('/config/flows'),
  getAllList: () => api.get('/config/flows/all'),
  getByFlowId: (flowId) => api.get(`/config/flows/${flowId}`),
  create: (data) => api.post('/config/flows', data),
  update: (data) => api.put('/config/flows', data),
  delete: (flowId) => api.delete(`/config/flows/${flowId}`),
  batchDelete: (flowIds) => api.post('/config/flows/batch/delete', flowIds),
  batchActivate: (flowIds) => api.post('/config/flows/batch/activate', flowIds),
  batchDeactivate: (flowIds) => api.post('/config/flows/batch/deactivate', flowIds),
  refresh: (flowId) => api.post(`/config/flows/${flowId}/refresh`),
  refreshAll: () => api.post('/config/flows/refresh-all'),
  exportData: (params) => api.get('/config/flows/export', { params, responseType: 'blob' }),
  importData: (data) => api.post('/config/flows/import', data)
};

export const validationLogApi = {
  getPage: (params) => api.get('/logs/page', { params }),
  getList: () => api.get('/logs'),
  getByLogId: (logId) => api.get(`/logs/${logId}`),
  delete: (logId) => api.delete(`/logs/${logId}`),
  batchDelete: (logIds) => api.post('/logs/batch/delete', logIds),
  batchActivate: (logIds) => api.post('/logs/batch/activate', logIds),
  batchDeactivate: (logIds) => api.post('/logs/batch/deactivate', logIds),
  exportData: (params) => api.get('/logs/export', { params, responseType: 'blob' })
};

export const validationApi = {
  validate: (flowId, data) => axios.post(`/api/validate?flowId=${flowId}`, data, {
    headers: { 'Content-Type': 'application/json' },
    timeout: 10000
  })
};

export default api;

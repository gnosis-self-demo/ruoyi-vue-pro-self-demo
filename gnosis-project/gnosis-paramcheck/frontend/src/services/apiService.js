import axios from 'axios';

const api = axios.create({
  baseURL: '/api/paramcheck',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 业务类型管理
export const businessTypeApi = {
  // 获取业务类型列表
  getBusinessTypes: () => api.get('/business-types'),
  // 新增业务类型
  createBusinessType: (data) => api.post('/business-types', data),
  // 更新业务类型
  updateBusinessType: (id, data) => api.put(`/business-types/${id}`, data),
  // 删除业务类型
  deleteBusinessType: (id) => api.delete(`/business-types/${id}`),
  // 批量删除业务类型
  batchDeleteBusinessTypes: (ids) => api.delete('/business-types/batch', { data: ids })
};

// 流程配置管理
export const flowConfigApi = {
  // 获取流程配置列表
  getFlowConfigs: () => api.get('/config/flows/all'),
  // 新增流程配置
  createFlowConfig: (data) => api.post('/config/flows', data),
  // 更新流程配置
  updateFlowConfig: (id, data) => api.post(`/config/flows`, data),
  // 批量删除流程配置
  batchDeleteFlowConfigs: (ids) => api.post('/config/flows/batch/delete', ids),
  // 批量启用流程配置
  batchEnableFlowConfigs: (ids) => api.post('/config/flows/batch/activate', ids),
  // 批量禁用流程配置
  batchDisableFlowConfigs: (ids) => api.post('/config/flows/batch/deactivate', ids)
};

// 校验测试
export const validationApi = {
  // 执行参数校验
  validate: (data) => api.post('/validate', data, { params: { flowId: data.validationMode === 'FLOW' ? 'SIMPLE_CHECK_FLOW' : data.validationMode === 'HANDLER' ? 'USER_REGISTER_FLOW' : 'ORDER_CREATE_FLOW' } })
};

export default api;
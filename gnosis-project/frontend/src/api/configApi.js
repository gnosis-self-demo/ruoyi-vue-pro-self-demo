import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api/paramcheck',
  timeout: 10000
})

// 流程配置API
export const configApi = {
  // 获取所有激活的流程配置
  getActiveFlows() {
    return api.get('/config/flows')
  },
  
  // 获取所有流程配置，包括禁用的
  getAllFlows(params) {
    return api.get('/config/flows/all', { params })
  },
  
  // 获取单个流程配置详情
  getFlow(flowId) {
    return api.get(`/config/flows/${flowId}`)
  },
  
  // 保存流程配置
  saveFlow(flow) {
    return api.post('/config/flows', flow)
  },
  
  // 手动刷新指定流程
  refreshFlow(flowId) {
    return api.post(`/config/flows/${flowId}/refresh`)
  },
  
  // 全量刷新所有流程
  refreshAllFlows() {
    return api.post('/config/flows/refresh-all')
  },
  
  // 禁用流程
  deactivateFlow(flowId) {
    return api.post(`/config/flows/${flowId}/deactivate`)
  },
  
  // 启用流程
  activateFlow(flowId) {
    return api.post(`/config/flows/${flowId}/activate`)
  },
  
  // 批量启用流程
  batchActivateFlows(flowIds) {
    return api.post('/config/flows/batch/activate', flowIds)
  },
  
  // 批量禁用流程
  batchDeactivateFlows(flowIds) {
    return api.post('/config/flows/batch/deactivate', flowIds)
  },
  
  // 批量删除流程
  batchDeleteFlows(flowIds) {
    return api.post('/config/flows/batch/delete', flowIds)
  },
  
  // 获取所有业务类型
  getAllBusinessTypes() {
    return api.get('/business-types')
  },
  
  // 保存业务类型
  saveBusinessType(businessType) {
    return api.post('/business-types', businessType)
  },
  
  // 删除业务类型
  deleteBusinessType(code) {
    return api.delete(`/business-types/${code}`)
  }
}

export default api
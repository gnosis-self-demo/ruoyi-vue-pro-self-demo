import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api/paramcheck/config',
  timeout: 10000
})

// 流程配置API
export const configApi = {
  // 获取所有激活的流程配置
  getActiveFlows() {
    return api.get('/flows')
  },
  
  // 获取所有流程配置，包括禁用的
  getAllFlows() {
    return api.get('/flows/all')
  },
  
  // 获取单个流程配置详情
  getFlow(flowId) {
    return api.get(`/flows/${flowId}`)
  },
  
  // 保存流程配置
  saveFlow(flow) {
    return api.post('/flows', flow)
  },
  
  // 手动刷新指定流程
  refreshFlow(flowId) {
    return api.post(`/flows/${flowId}/refresh`)
  },
  
  // 全量刷新所有流程
  refreshAllFlows() {
    return api.post('/flows/refresh-all')
  },
  
  // 禁用流程
  deactivateFlow(flowId) {
    return api.post(`/flows/${flowId}/deactivate`)
  },
  
  // 启用流程
  activateFlow(flowId) {
    return api.post(`/flows/${flowId}/activate`)
  }
}

export default api
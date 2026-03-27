import request from '../utils/request'

// 分页查询路由规则列表
export const getRouteRuleList = (params) => {
  return request({
    url: '/lifecycle/route-rule/list',
    method: 'get',
    params
  })
}

// 获取路由规则详情
export const getRouteRuleDetail = (id) => {
  return request({
    url: `/lifecycle/route-rule/${id}`,
    method: 'get'
  })
}

// 创建路由规则
export const createRouteRule = (data) => {
  return request({
    url: '/lifecycle/route-rule',
    method: 'post',
    data
  })
}

// 更新路由规则
export const updateRouteRule = (id, data) => {
  return request({
    url: `/lifecycle/route-rule/${id}`,
    method: 'put',
    data
  })
}

// 删除路由规则
export const deleteRouteRule = (id) => {
  return request({
    url: `/lifecycle/route-rule/${id}`,
    method: 'delete'
  })
}

// 批量启用路由规则
export const batchEnableRules = (ids) => {
  return request({
    url: '/lifecycle/route-rule/batch-enable',
    method: 'post',
    data: { ids }
  })
}

// 批量禁用路由规则
export const batchDisableRules = (ids) => {
  return request({
    url: '/lifecycle/route-rule/batch-disable',
    method: 'post',
    data: { ids }
  })
}

// 批量删除路由规则
export const batchDeleteRules = (ids) => {
  return request({
    url: '/lifecycle/route-rule/batch-delete',
    method: 'post',
    data: { ids }
  })
}

// 获取规则版本历史
export const getRuleVersionHistory = (ruleId) => {
  return request({
    url: `/lifecycle/route-rule/${ruleId}/versions`,
    method: 'get'
  })
}

// 回滚规则版本
export const rollbackRuleVersion = (versionId) => {
  return request({
    url: `/lifecycle/route-rule/rollback/${versionId}`,
    method: 'post'
  })
}

// 导入路由规则
export const importRouteRules = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/route-rule/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出路由规则
export const exportRouteRules = (params) => {
  return request({
    url: '/lifecycle/route-rule/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

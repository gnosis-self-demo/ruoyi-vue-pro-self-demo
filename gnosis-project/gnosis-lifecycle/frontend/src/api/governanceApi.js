import request from '../utils/request'

// 分页查询元数据列表
export const getMetadataList = (params) => {
  return request({
    url: '/lifecycle/governance/metadata/list',
    method: 'get',
    params
  })
}

// 创建元数据
export const createMetadata = (data) => {
  return request({
    url: '/lifecycle/governance/metadata',
    method: 'post',
    data
  })
}

// 更新元数据
export const updateMetadata = (id, data) => {
  return request({
    url: `/lifecycle/governance/metadata/${id}`,
    method: 'put',
    data
  })
}

// 删除元数据
export const deleteMetadata = (id) => {
  return request({
    url: `/lifecycle/governance/metadata/${id}`,
    method: 'delete'
  })
}

// 获取规则版本历史
export const getRuleVersionHistory = (ruleId) => {
  return request({
    url: `/lifecycle/governance/rule/${ruleId}/versions`,
    method: 'get'
  })
}

// 回滚规则版本
export const rollbackRuleVersion = (versionId) => {
  return request({
    url: '/lifecycle/governance/rule/rollback',
    method: 'post',
    params: { versionId }
  })
}

// 获取实例追踪
export const getInstanceTrace = (instanceId) => {
  return request({
    url: '/lifecycle/governance/instance-trace',
    method: 'get',
    params: { instanceId }
  })
}

// 获取追踪看板
export const getTraceDashboard = (params) => {
  return request({
    url: '/lifecycle/governance/trace-dashboard',
    method: 'get',
    params
  })
}

// 导入元数据
export const importMetadata = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/governance/metadata/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出元数据
export const exportMetadata = (params) => {
  return request({
    url: '/lifecycle/governance/metadata/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

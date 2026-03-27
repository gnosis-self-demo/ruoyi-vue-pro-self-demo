import request from '../utils/request'

// 获取终态列表
export const getFinalStateList = (params) => {
  return request({
    url: '/lifecycle/exit/final-state/list',
    method: 'get',
    params
  })
}

// 创建终态
export const createFinalState = (data) => {
  return request({
    url: '/lifecycle/exit/final-state',
    method: 'post',
    data
  })
}

// 更新终态
export const updateFinalState = (id, data) => {
  return request({
    url: `/lifecycle/exit/final-state/${id}`,
    method: 'put',
    data
  })
}

// 删除终态
export const deleteFinalState = (id) => {
  return request({
    url: `/lifecycle/exit/final-state/${id}`,
    method: 'delete'
  })
}

// 批量删除终态
export const batchDeleteFinalStates = (ids) => {
  return request({
    url: '/lifecycle/exit/final-state/batch-delete',
    method: 'post',
    data: { ids }
  })
}

// 批量启用终态
export const batchEnableFinalStates = (ids) => {
  return request({
    url: '/lifecycle/exit/final-state/batch-enable',
    method: 'post',
    data: { ids }
  })
}

// 批量禁用终态
export const batchDisableFinalStates = (ids) => {
  return request({
    url: '/lifecycle/exit/final-state/batch-disable',
    method: 'post',
    data: { ids }
  })
}

// 获取下游配置列表
export const getDownstreamList = (params) => {
  return request({
    url: '/lifecycle/exit/downstream/list',
    method: 'get',
    params
  })
}

// 创建下游配置
export const createDownstream = (data) => {
  return request({
    url: '/lifecycle/exit/downstream',
    method: 'post',
    data
  })
}

// 更新下游配置
export const updateDownstream = (id, data) => {
  return request({
    url: `/lifecycle/exit/downstream/${id}`,
    method: 'put',
    data
  })
}

// 删除下游配置
export const deleteDownstream = (id) => {
  return request({
    url: `/lifecycle/exit/downstream/${id}`,
    method: 'delete'
  })
}

// 批量删除下游配置
export const batchDeleteDownstreams = (ids) => {
  return request({
    url: '/lifecycle/exit/downstream/batch-delete',
    method: 'post',
    data: { ids }
  })
}

// 批量启用下游配置
export const batchEnableDownstreams = (ids) => {
  return request({
    url: '/lifecycle/exit/downstream/batch-enable',
    method: 'post',
    data: { ids }
  })
}

// 批量禁用下游配置
export const batchDisableDownstreams = (ids) => {
  return request({
    url: '/lifecycle/exit/downstream/batch-disable',
    method: 'post',
    data: { ids }
  })
}

// 发布事件
export const publishEvent = (data) => {
  return request({
    url: '/lifecycle/exit/publish-event',
    method: 'post',
    data
  })
}

// 导入终态配置
export const importFinalStates = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/exit/final-state/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出终态配置
export const exportFinalStates = (params) => {
  return request({
    url: '/lifecycle/exit/final-state/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 导入下游配置
export const importDownstreams = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/exit/downstream/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出下游配置
export const exportDownstreams = (params) => {
  return request({
    url: '/lifecycle/exit/downstream/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

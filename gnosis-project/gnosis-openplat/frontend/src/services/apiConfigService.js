import request from '../utils/request'

export const getApiConfigList = (params) => {
  return request({
    url: '/openplat/api-config/list',
    method: 'get',
    params
  })
}

export const getApiConfigById = (id) => {
  return request({
    url: `/openplat/api-config/${id}`,
    method: 'get'
  })
}

export const saveApiConfig = (data) => {
  return request({
    url: '/openplat/api-config',
    method: 'post',
    data
  })
}

export const updateApiConfig = (data) => {
  return request({
    url: '/openplat/api-config',
    method: 'put',
    data
  })
}

export const deleteApiConfig = (id) => {
  return request({
    url: `/openplat/api-config/${id}`,
    method: 'delete'
  })
}

export const batchDeleteApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/batch',
    method: 'delete',
    data: ids
  })
}
// 批量启用API
export const batchEnableApiConfigs = async (ids) => {
  return request({
    url: '/openplat/api-config/enable',
    method: 'put',
    data: ids
  })
}

// 批量禁用API
export const batchDisableApiConfigs = async (ids) => {
  return request({
    url: '/openplat/api-config/disable',
    method: 'put',
    data: ids
  })
}

// 关联系统到API
export const relateSystemsToApi = async (apiId, systemIds) => {
  return request({
    url: '/openplat/api-config/relate-systems',
    method: 'post',
    data: { apiId, systemIds }
  })
}

// 解除API与系统的关联
export const unrelateSystemsFromApi = async (apiId, systemIds) => {
  return request({
    url: '/openplat/api-config/unrelate-systems',
    method: 'post',
    data: { apiId, systemIds }
  })
}

import request from '../utils/request'

export const getApiConfigList = (data) => {
  return request({
    url: '/openplat/api-config/list',
    method: 'post',
    data: data || {}
  })
}

export const getApiConfigById = (id) => {
  return request({
    url: '/openplat/api-config/detail',
    method: 'post',
    data: { id }
  })
}

export const saveApiConfig = (data) => {
  return request({
    url: '/openplat/api-config/save',
    method: 'post',
    data
  })
}

export const updateApiConfig = (data) => {
  return request({
    url: '/openplat/api-config/update',
    method: 'post',
    data
  })
}

export const deleteApiConfig = (id) => {
  return request({
    url: '/openplat/api-config/delete',
    method: 'post',
    data: { id }
  })
}

export const batchDeleteApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/batchDelete',
    method: 'post',
    data: { ids }
  })
}

export const batchEnableApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/batchEnable',
    method: 'post',
    data: { ids }
  })
}

export const batchDisableApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/batchDisable',
    method: 'post',
    data: { ids }
  })
}

export const checkApiConfig = (apiCode, apiPath) => {
  return request({
    url: '/openplat/api-config/check',
    method: 'post',
    data: { apiCode, apiPath }
  })
}

export const relateSystemsToApi = (apiId, systemIds) => {
  return request({
    url: '/openplat/api-config/relateSystems',
    method: 'post',
    data: { apiId, systemIds }
  })
}

export const unrelateSystemsFromApi = (apiId, systemIds) => {
  return request({
    url: '/openplat/api-config/unrelateSystems',
    method: 'post',
    data: { apiId, systemIds }
  })
}

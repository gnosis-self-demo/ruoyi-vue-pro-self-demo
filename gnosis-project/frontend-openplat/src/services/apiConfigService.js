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

export const batchEnableApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/enable',
    method: 'put',
    data: ids
  })
}

export const batchDisableApiConfigs = (ids) => {
  return request({
    url: '/openplat/api-config/disable',
    method: 'put',
    data: ids
  })
}

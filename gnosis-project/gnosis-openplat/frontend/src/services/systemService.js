import request from '../utils/request'

export const getSystemList = (params) => {
  return request({
    url: '/openplat/system/list',
    method: 'get',
    params
  })
}

export const getSystemById = (id) => {
  return request({
    url: `/openplat/system/${id}`,
    method: 'get'
  })
}

export const saveSystem = (data) => {
  return request({
    url: '/openplat/system',
    method: 'post',
    data
  })
}

export const updateSystem = (data) => {
  return request({
    url: '/openplat/system',
    method: 'put',
    data
  })
}

export const deleteSystem = (id) => {
  return request({
    url: `/openplat/system/${id}`,
    method: 'delete'
  })
}

export const batchDeleteSystems = (ids) => {
  return request({
    url: '/openplat/system/batch',
    method: 'delete',
    data: ids
  })
}
// 批量启用系统
export const batchEnableSystems = async (ids) => {
  return request({
    url: '/openplat/system/enable',
    method: 'put',
    data: ids
  })
}

// 批量禁用系统
export const batchDisableSystems = async (ids) => {
  return request({
    url: '/openplat/system/disable',
    method: 'put',
    data: ids
  })
}

// 查询系统关联的API列表
export const getSystemApis = async (systemId) => {
  return request({
    url: `/openplat/system/apis/${systemId}`,
    method: 'get'
  })
}

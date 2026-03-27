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

export const batchEnableSystems = (ids) => {
  return request({
    url: '/openplat/system/enable',
    method: 'put',
    data: ids
  })
}

export const batchDisableSystems = (ids) => {
  return request({
    url: '/openplat/system/disable',
    method: 'put',
    data: ids
  })
}

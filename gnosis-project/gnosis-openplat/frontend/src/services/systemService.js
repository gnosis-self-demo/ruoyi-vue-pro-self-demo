import request from '../utils/request'

export const getSystemList = (data) => {
  return request({
    url: '/openplat/system/list',
    method: 'post',
    data: data || {}
  })
}

export const getSystemById = (id) => {
  return request({
    url: '/openplat/system/detail',
    method: 'post',
    data: { id }
  })
}

export const saveSystem = (data) => {
  return request({
    url: '/openplat/system/save',
    method: 'post',
    data
  })
}

export const updateSystem = (data) => {
  return request({
    url: '/openplat/system/update',
    method: 'post',
    data
  })
}

export const deleteSystem = (id) => {
  return request({
    url: '/openplat/system/delete',
    method: 'post',
    data: { id }
  })
}

export const batchDeleteSystems = (ids) => {
  return request({
    url: '/openplat/system/batchDelete',
    method: 'post',
    data: { ids }
  })
}

export const batchEnableSystems = (ids) => {
  return request({
    url: '/openplat/system/batchEnable',
    method: 'post',
    data: { ids }
  })
}

export const batchDisableSystems = (ids) => {
  return request({
    url: '/openplat/system/batchDisable',
    method: 'post',
    data: { ids }
  })
}

export const getSystemApis = (systemId) => {
  return request({
    url: '/openplat/system/getSystemApis',
    method: 'post',
    data: { id: systemId }
  })
}

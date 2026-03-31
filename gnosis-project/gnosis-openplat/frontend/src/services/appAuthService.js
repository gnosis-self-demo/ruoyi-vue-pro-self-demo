import request from '../utils/request'

export const getAppAuthList = (params) => {
  return request({
    url: '/openplat/app-auth/list',
    method: 'get',
    params
  })
}

export const getAppAuthById = (id) => {
  return request({
    url: `/openplat/app-auth/${id}`,
    method: 'get'
  })
}

export const saveAppAuth = (data) => {
  return request({
    url: '/openplat/app-auth',
    method: 'post',
    data
  })
}

export const updateAppAuth = (data) => {
  return request({
    url: '/openplat/app-auth',
    method: 'put',
    data
  })
}

export const deleteAppAuth = (id) => {
  return request({
    url: `/openplat/app-auth/${id}`,
    method: 'delete'
  })
}

export const batchDeleteAppAuths = (ids) => {
  return request({
    url: '/openplat/app-auth/batch',
    method: 'delete',
    data: ids
  })
}

export const batchEnableAppAuths = (ids) => {
  return request({
    url: '/openplat/app-auth/enable',
    method: 'put',
    data: ids
  })
}

export const batchDisableAppAuths = (ids) => {
  return request({
    url: '/openplat/app-auth/disable',
    method: 'put',
    data: ids
  })
}

export const getSystemList = (params) => {
  return request({
    url: '/openplat/system/list',
    method: 'get',
    params
  })
}

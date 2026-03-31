import request from '../utils/request'

export const getWebhookConfigList = (params) => {
  return request({
    url: '/openplat/webhook-config/list',
    method: 'get',
    params
  })
}

export const getWebhookConfigById = (id) => {
  return request({
    url: `/openplat/webhook-config/${id}`,
    method: 'get'
  })
}

export const saveWebhookConfig = (data) => {
  return request({
    url: '/openplat/webhook-config',
    method: 'post',
    data
  })
}

export const updateWebhookConfig = (data) => {
  return request({
    url: '/openplat/webhook-config',
    method: 'put',
    data
  })
}

export const deleteWebhookConfig = (id) => {
  return request({
    url: `/openplat/webhook-config/${id}`,
    method: 'delete'
  })
}

export const batchDeleteWebhookConfigs = (ids) => {
  return request({
    url: '/openplat/webhook-config/batch',
    method: 'delete',
    data: ids
  })
}

export const batchEnableWebhookConfigs = (ids) => {
  return request({
    url: '/openplat/webhook-config/enable',
    method: 'put',
    data: ids
  })
}

export const batchDisableWebhookConfigs = (ids) => {
  return request({
    url: '/openplat/webhook-config/disable',
    method: 'put',
    data: ids
  })
}

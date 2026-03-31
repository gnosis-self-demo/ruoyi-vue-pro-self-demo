import request from '../utils/request'

// 分页查询业务类型列表
export const getBusinessTypeList = (params) => {
  return request({
    url: '/lifecycle/business-type/list',
    method: 'get',
    params
  })
}

// 获取业务类型详情
export const getBusinessTypeDetail = (id) => {
  return request({
    url: `/lifecycle/business-type/${id}`,
    method: 'get'
  })
}

// 创建业务类型
export const createBusinessType = (data) => {
  return request({
    url: '/lifecycle/business-type',
    method: 'post',
    data
  })
}

// 更新业务类型
export const updateBusinessType = (id, data) => {
  return request({
    url: `/lifecycle/business-type/${id}`,
    method: 'put',
    data
  })
}

// 删除业务类型
export const deleteBusinessType = (id) => {
  return request({
    url: `/lifecycle/business-type/${id}`,
    method: 'delete'
  })
}

// 批量删除业务类型
export const batchDeleteBusinessTypes = (ids) => {
  return request({
    url: '/lifecycle/business-type/batch-delete',
    method: 'post',
    data: { ids }
  })
}

// 批量启用业务类型
export const batchEnableBusinessTypes = (ids) => {
  return request({
    url: '/lifecycle/business-type/batch-enable',
    method: 'post',
    data: { ids }
  })
}

// 批量禁用业务类型
export const batchDisableBusinessTypes = (ids) => {
  return request({
    url: '/lifecycle/business-type/batch-disable',
    method: 'post',
    data: { ids }
  })
}

// 入口校验
export const validateEntry = (data) => {
  return request({
    url: '/lifecycle/business-type/validate-entry',
    method: 'post',
    data
  })
}

// 导入业务类型
export const importBusinessTypes = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/business-type/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出业务类型
export const exportBusinessTypes = (params) => {
  return request({
    url: '/lifecycle/business-type/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

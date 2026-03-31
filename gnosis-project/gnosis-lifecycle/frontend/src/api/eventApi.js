import request from '../utils/request'

// 提交事件
export const submitEvent = (data) => {
  return request({
    url: '/lifecycle/event/submit',
    method: 'post',
    data
  })
}

// 分页查询事件列表
export const getEventList = (params) => {
  return request({
    url: '/lifecycle/event/list',
    method: 'get',
    params
  })
}

// 获取事件详情
export const getEventDetail = (id) => {
  return request({
    url: `/lifecycle/event/${id}`,
    method: 'get'
  })
}

// 测试路由规则
export const testRouteRule = (data) => {
  return request({
    url: '/lifecycle/event/test-route',
    method: 'post',
    data
  })
}

// 执行流转
export const executeTransition = (data) => {
  return request({
    url: '/lifecycle/event/execute-transition',
    method: 'post',
    data
  })
}

// 获取元数据字典
export const getMetadataDictionary = (businessTypeId) => {
  return request({
    url: '/lifecycle/event/metadata-dictionary',
    method: 'get',
    params: { businessTypeId }
  })
}

// 导入事件
export const importEvents = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/lifecycle/event/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出事件
export const exportEvents = (params) => {
  return request({
    url: '/lifecycle/event/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

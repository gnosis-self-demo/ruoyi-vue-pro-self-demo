import request from '../utils/request'

export const getList = (data) => request({ url: '/process-orchestration/transition-flow/list', method: 'post', data })
export const getDetail = (data) => request({ url: '/process-orchestration/transition-flow/detail', method: 'post', data })
export const exportData = (data) => request({ url: '/process-orchestration/transition-flow/export', method: 'post', data, responseType: 'blob' })

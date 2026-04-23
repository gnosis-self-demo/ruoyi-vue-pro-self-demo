import axios from 'axios'
import { message } from 'antd'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    // 处理 blob 类型的响应（如导出文件），直接返回原始响应
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response.data
    }
    const res = response.data
    if (res.code !== 0 && res.code !== 200) {
      message.error(res.msg || res.message || '请求失败')
      return Promise.reject(new Error(res.msg || res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('响应错误:', error)
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message.error('未授权，请重新登录')
          break
        case 403:
          message.error('拒绝访问')
          break
        case 404:
          message.error('请求资源不存在')
          break
        case 500:
          message.error('服务器错误')
          break
        default:
          message.error(error.response.data?.msg || '请求失败')
      }
    } else {
      message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request

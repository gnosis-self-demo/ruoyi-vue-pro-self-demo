import axios from 'axios'
import { message } from 'antd'

const request = axios.create({
  baseURL: '/notice',
  timeout: 30000,
})

request.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data.code !== 200) {
      message.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (error) => {
    console.error('API Error:', error)
    message.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request

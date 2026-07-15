import axios from 'axios';
import { message } from 'antd';

const request = axios.create({
  baseURL: '/signature',
  timeout: 30000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 处理二进制响应(文件下载)
    if (response.config.responseType === 'blob' || response.data instanceof Blob) {
      return response;
    }
    const res = response.data;
    if (res.code !== 200) {
      message.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message || '请求失败'));
    }
    return res;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message.error('未授权，请重新登录');
          break;
        case 403:
          message.error('拒绝访问');
          break;
        case 404:
          message.error('请求资源不存在');
          break;
        case 500:
          message.error('服务器错误');
          break;
        default:
          message.error(error.response.data?.message || '请求失败');
      }
    } else {
      message.error('网络错误，请稍后重试');
    }
    return Promise.reject(error);
  }
);

export default request;

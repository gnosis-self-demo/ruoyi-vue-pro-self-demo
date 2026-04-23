import axios from 'axios';

const request = axios.create({
  baseURL: '/datamapping',
  timeout: 10000,
});

request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

export default request;

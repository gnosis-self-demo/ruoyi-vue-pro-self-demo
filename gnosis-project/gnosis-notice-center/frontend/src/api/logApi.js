import request from '../utils/request'

export const logApi = {
  pageList: (data) => request.post('/log/page', data),
  detail: (id) => request.post('/log/detail', { id }),
  retry: (id) => request.post('/log/retry', { id }),
}

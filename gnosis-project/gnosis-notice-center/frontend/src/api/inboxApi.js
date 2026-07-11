import request from '../utils/request'

export const inboxApi = {
  pageList: (data) => request.post('/inbox/page', data),
  detail: (id) => request.post('/inbox/detail', { id }),
  countUnread: (userId) => request.post('/inbox/countUnread', { userId }),
  create: (data) => request.post('/inbox/create', data),
  markRead: (id) => request.post('/inbox/markRead', { id }),
  batchMarkRead: (ids) => request.post('/inbox/batchRead', ids),
  delete: (id) => request.post('/inbox/delete', { id }),
  batchDelete: (ids) => request.post('/inbox/batchDelete', ids),
}

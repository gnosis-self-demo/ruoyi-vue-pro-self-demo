import request from '../utils/request';

// 科目管理
export const subjectApi = {
  pageList: (data) => request.post('/subject/page', data),
  detail: (id) => request.post('/subject/detail', { id }),
  create: (data) => request.post('/subject/create', data),
  update: (data) => request.post('/subject/update', data),
  delete: (id) => request.post('/subject/delete', { id }),
  batchDelete: (ids) => request.post('/subject/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/subject/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/subject/batchDisable', { ids }),
};

// 账户管理
export const accountApi = {
  pageList: (data) => request.post('/account/page', data),
  detail: (id) => request.post('/account/detail', { id }),
  create: (data) => request.post('/account/create', data),
  update: (data) => request.post('/account/update', data),
  delete: (id) => request.post('/account/delete', { id }),
  batchDelete: (ids) => request.post('/account/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/account/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/account/batchDisable', { ids }),
  getBalance: (id) => request.post('/account/getBalance', { id }),
};

// 会计流水
export const journalApi = {
  pageList: (data) => request.post('/journal/page', data),
  detail: (id) => request.post('/journal/detail', { id }),
  create: (data) => request.post('/journal/create', data),
  update: (data) => request.post('/journal/update', data),
  delete: (id) => request.post('/journal/delete', { id }),
  batchDelete: (ids) => request.post('/journal/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/journal/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/journal/batchDisable', { ids }),
  post: (id) => request.post('/journal/post', { id }),
  reverse: (id) => request.post('/journal/reverse', { id }),
};

// 分录明细
export const entryApi = {
  pageList: (data) => request.post('/entry/page', data),
  listByJournal: (journalId) => request.post('/entry/listByJournal', { journalId }),
};

// 渠道清算
export const clearingApi = {
  pageList: (data) => request.post('/clearing/page', data),
  detail: (id) => request.post('/clearing/detail', { id }),
  create: (data) => request.post('/clearing/create', data),
  update: (data) => request.post('/clearing/update', data),
  delete: (id) => request.post('/clearing/delete', { id }),
  batchDelete: (ids) => request.post('/clearing/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/clearing/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/clearing/batchDisable', { ids }),
  executeClearing: (id) => request.post('/clearing/executeClearing', { id }),
};

// 银存结转
export const transferApi = {
  pageList: (data) => request.post('/transfer/page', data),
  detail: (id) => request.post('/transfer/detail', { id }),
  create: (data) => request.post('/transfer/create', data),
  update: (data) => request.post('/transfer/update', data),
  delete: (id) => request.post('/transfer/delete', { id }),
  batchDelete: (ids) => request.post('/transfer/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/transfer/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/transfer/batchDisable', { ids }),
  executeTransfer: (id) => request.post('/transfer/executeTransfer', { id }),
};

// 客资结算
export const settlementApi = {
  pageList: (data) => request.post('/settlement/page', data),
  detail: (id) => request.post('/settlement/detail', { id }),
  create: (data) => request.post('/settlement/create', data),
  update: (data) => request.post('/settlement/update', data),
  delete: (id) => request.post('/settlement/delete', { id }),
  batchDelete: (ids) => request.post('/settlement/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/settlement/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/settlement/batchDisable', { ids }),
  executeSettlement: (id) => request.post('/settlement/executeSettlement', { id }),
};

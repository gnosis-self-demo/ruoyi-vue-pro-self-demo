import request from '../utils/request';

export const dataMappingApi = {
  pageList: (data) => request.post('/config/page', data),
  detail: (id) => request.post('/config/detail', { id }),
  create: (data) => request.post('/config/create', data),
  update: (data) => request.post('/config/update', data),
  delete: (id) => request.post('/config/delete', { id }),
  batchDelete: (ids) => request.post('/config/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/config/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/config/batchDisable', { ids }),
  execute: (configId, requestJson) => request.post('/config/execute', { configId, requestJson }),
  test: (configId, requestJson) => request.post('/config/test', { configId, requestJson }),
  logPageList: (configId, pageNum, pageSize) => request.post('/log/page', { configId, pageNum, pageSize }),
};

export const testCaseApi = {
  pageList: (data) => request.post('/testcase/page', data),
  detail: (id) => request.post('/testcase/detail', { id }),
  create: (data) => request.post('/testcase/create', data),
  update: (data) => request.post('/testcase/update', data),
  delete: (id) => request.post('/testcase/delete', { id }),
  batchDelete: (ids) => request.post('/testcase/batchDelete', { ids }),
  batchEnable: (ids) => request.post('/testcase/batchEnable', { ids }),
  batchDisable: (ids) => request.post('/testcase/batchDisable', { ids }),
  execute: (testCaseId) => request.post('/testcase/execute', { testCaseId }),
  executeByConfig: (configId) => request.post('/testcase/executeByConfig', { configId }),
};

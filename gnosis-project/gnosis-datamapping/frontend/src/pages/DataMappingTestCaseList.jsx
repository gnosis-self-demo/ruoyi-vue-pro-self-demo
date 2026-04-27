import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Space, Tag, Form, Select, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, PlayCircleOutlined, SearchOutlined, ThunderboltOutlined, ExportOutlined, DownloadOutlined } from '@ant-design/icons';
import { testCaseApi } from '../api/dataMappingApi';
import DataMappingTestCaseForm from './DataMappingTestCaseForm';
import DataMappingTestCaseResult from './DataMappingTestCaseResult';

const DataMappingTestCaseList = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [query, setQuery] = useState({ configId: null, caseName: '', status: null, isPassed: null });
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [formVisible, setFormVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [resultVisible, setResultVisible] = useState(false);
  const [resultData, setResultData] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await testCaseApi.pageList({
        query,
        pageNum: (pageNum - 1) * pageSize,
        pageSize,
      });
      if (res.code === 200) {
        setData(res.data.list || []);
        setTotal(res.data.total || 0);
      }
    } catch (error) {
      message.error('获取数据失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [pageNum, pageSize, query]);

  const handleCreate = () => {
    setCurrentRecord(null);
    setFormVisible(true);
  };

  const handleEdit = (record) => {
    setCurrentRecord(record);
    setFormVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      const res = await testCaseApi.delete(id);
      if (res.code === 200) {
        message.success('删除成功');
        fetchData();
      }
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要删除的用例');
      return;
    }
    try {
      const res = await testCaseApi.batchDelete(selectedRowKeys);
      if (res.code === 200) {
        message.success('批量删除成功');
        setSelectedRowKeys([]);
        fetchData();
      }
    } catch (error) {
      message.error('批量删除失败');
    }
  };

  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要启用的用例');
      return;
    }
    try {
      const res = await testCaseApi.batchEnable(selectedRowKeys);
      if (res.code === 200) {
        message.success('批量启用成功');
        setSelectedRowKeys([]);
        fetchData();
      }
    } catch (error) {
      message.error('批量启用失败');
    }
  };

  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要禁用的用例');
      return;
    }
    try {
      const res = await testCaseApi.batchDisable(selectedRowKeys);
      if (res.code === 200) {
        message.success('批量禁用成功');
        setSelectedRowKeys([]);
        fetchData();
      }
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleExecute = async (testCaseId) => {
    try {
      const res = await testCaseApi.execute(testCaseId);
      if (res.code === 200) {
        setResultData(res.data);
        setResultVisible(true);
        fetchData();
      }
    } catch (error) {
      message.error('执行失败');
    }
  };

  const handleBatchExecute = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请先选择要执行的用例');
      return;
    }
    let passed = 0;
    let failed = 0;
    for (const id of selectedRowKeys) {
      try {
        const res = await testCaseApi.execute(id);
        if (res.code === 200) {
          if (res.data.isPassed) {
            passed++;
          } else {
            failed++;
          }
        }
      } catch (e) {
        failed++;
      }
    }
    message.success(`批量执行完成：总计${selectedRowKeys.length}个，通过${passed}个，失败${failed}个`);
    setSelectedRowKeys([]);
    fetchData();
  };

  const handleExport = () => {
    const exportData = selectedRowKeys.length > 0
      ? data.filter(item => selectedRowKeys.includes(item.id))
      : data;
    const headers = ['用例编码', '用例名称', '关联配置编码', '请求JSON', '期望结果JSON', '实际结果JSON', '测试结果', '差异信息', '描述', '创建人ID', '创建时间', '更新人ID', '更新时间'];
    const csvRows = [headers.join(',')];
    exportData.forEach(item => {
      const row = [
        `"${(item.caseCode || '').replace(/"/g, '""')}"`,
        `"${(item.caseName || '').replace(/"/g, '""')}"`,
        `"${(item.configCode || '').replace(/"/g, '""')}"`,
        `"${(item.requestJson || '').replace(/"/g, '""')}"`,
        `"${(item.expectedResultJson || '').replace(/"/g, '""')}"`,
        `"${(item.actualResultJson || '').replace(/"/g, '""')}"`,
        item.isPassed === 1 ? '通过' : item.isPassed === 0 ? '失败' : '未执行',
        `"${(item.diffInfo || '').replace(/"/g, '""')}"`,
        `"${(item.description || '').replace(/"/g, '""')}"`,
        `"${(item.createUserId || '').replace(/"/g, '""')}"`,
        item.createTime || '',
        `"${(item.updateUserId || '').replace(/"/g, '""')}"`,
        item.updateTime || '',
      ];
      csvRows.push(row.join(','));
    });
    const BOM = '\uFEFF';
    const blob = new Blob([BOM + csvRows.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `test_cases_${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    message.success('导出成功');
  };

  const formatJson = (jsonStr) => {
    if (!jsonStr) return '-';
    try {
      return JSON.stringify(JSON.parse(jsonStr), null, 2);
    } catch (e) {
      return jsonStr;
    }
  };

  const columns = [
    { title: '用例编码', dataIndex: 'caseCode', key: 'caseCode', width: 140 },
    { title: '用例名称', dataIndex: 'caseName', key: 'caseName', width: 180 },
    { title: '关联配置', dataIndex: 'configCode', key: 'configCode', width: 140 },
    {
      title: '请求JSON',
      dataIndex: 'requestJson',
      key: 'requestJson',
      width: 250,
      ellipsis: true,
      render: (val) => (
        <pre style={{ margin: 0, fontSize: 11, whiteSpace: 'pre-wrap', wordBreak: 'break-all', maxHeight: 80, overflow: 'auto' }}>
          {formatJson(val)}
        </pre>
      ),
    },
    {
      title: '期望结果JSON',
      dataIndex: 'expectedResultJson',
      key: 'expectedResultJson',
      width: 250,
      ellipsis: true,
      render: (val) => (
        <pre style={{ margin: 0, fontSize: 11, whiteSpace: 'pre-wrap', wordBreak: 'break-all', maxHeight: 80, overflow: 'auto' }}>
          {formatJson(val)}
        </pre>
      ),
    },
    {
      title: '测试结果',
      dataIndex: 'isPassed',
      key: 'isPassed',
      width: 80,
      render: (val) => {
        if (val === 1) return <Tag color="success">通过</Tag>;
        if (val === 0) return <Tag color="error">失败</Tag>;
        return <Tag>未执行</Tag>;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<PlayCircleOutlined />} onClick={() => handleExecute(record.id)}>执行</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Space>
          <Input placeholder="用例名称" allowClear onChange={(e) => setQuery({ ...query, caseName: e.target.value })} style={{ width: 180 }} />
          <Select placeholder="关联配置" style={{ width: 180 }} allowClear onChange={(val) => setQuery({ ...query, configId: val })}
            options={data.map(d => ({ label: d.configCode, value: d.configId })).filter((v, i, a) => a.findIndex(t => t.value === v.value) === i)} />
          <Select placeholder="测试状态" style={{ width: 120 }} allowClear onChange={(val) => setQuery({ ...query, isPassed: val })}
            options={[{ label: '通过', value: 1 }, { label: '失败', value: 0 }]} />
          <Button icon={<SearchOutlined />} onClick={fetchData}>查询</Button>
        </Space>
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新建</Button>
          <Button icon={<ThunderboltOutlined />} onClick={handleBatchExecute} disabled={selectedRowKeys.length === 0}>批量执行</Button>
          <Button icon={<ExportOutlined />} onClick={handleExport}>导出</Button>
          <Button onClick={handleBatchEnable} disabled={selectedRowKeys.length === 0}>批量启用</Button>
          <Button onClick={handleBatchDisable} disabled={selectedRowKeys.length === 0}>批量禁用</Button>
          <Popconfirm title="确认批量删除？" onConfirm={handleBatchDelete}>
            <Button danger disabled={selectedRowKeys.length === 0}>批量删除</Button>
          </Popconfirm>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        rowSelection={{ selectedRowKeys, onChange: setSelectedRowKeys }}
        pagination={{ total, current: pageNum, pageSize, onChange: setPageNum, onShowSizeChange: (_, size) => setPageSize(size), showSizeChanger: true, showTotal: (t) => `共 ${t} 条` }}
        scroll={{ x: 1200 }}
      />
      {formVisible && <DataMappingTestCaseForm visible={formVisible} onCancel={() => setFormVisible(false)} record={currentRecord} onSuccess={() => { setFormVisible(false); fetchData(); }} />}
      {resultVisible && <DataMappingTestCaseResult visible={resultVisible} onCancel={() => setResultVisible(false)} data={resultData} />}
    </div>
  );
};

export default DataMappingTestCaseList;

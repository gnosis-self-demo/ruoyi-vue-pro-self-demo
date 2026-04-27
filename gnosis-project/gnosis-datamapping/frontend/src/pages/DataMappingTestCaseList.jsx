import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Space, Tag, Modal, Form, Select, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, PlayCircleOutlined, SearchOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { testCaseApi } from '../api/dataMappingApi';
import DataMappingTestCaseForm from './DataMappingTestCaseForm';
import DataMappingTestCaseResult from './DataMappingTestCaseResult';

const { Search } = Input;

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

  const handleExecuteByConfig = async (configId) => {
    if (!configId) {
      message.warning('请先选择一个配置');
      return;
    }
    try {
      const res = await testCaseApi.executeByConfig(configId);
      if (res.code === 200) {
        message.success(`执行完成：总计${res.data.total}个，通过${res.data.passed}个，失败${res.data.failed}个`);
        fetchData();
      }
    } catch (error) {
      message.error('批量执行失败');
    }
  };

  const columns = [
    { title: '用例编码', dataIndex: 'caseCode', key: 'caseCode', width: 150 },
    { title: '用例名称', dataIndex: 'caseName', key: 'caseName', width: 200 },
    { title: '关联配置', dataIndex: 'configCode', key: 'configCode', width: 150 },
    {
      title: '测试结果',
      dataIndex: 'isPassed',
      key: 'isPassed',
      width: 100,
      render: (val) => {
        if (val === 1) return <Tag color="success">通过</Tag>;
        if (val === 0) return <Tag color="error">失败</Tag>;
        return <Tag>未执行</Tag>;
      },
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    {
      title: '操作',
      key: 'action',
      width: 250,
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
          <Search placeholder="用例名称" onSearch={(val) => setQuery({ ...query, caseName: val })} style={{ width: 200 }} />
          <Select placeholder="测试状态" style={{ width: 120 }} allowClear onChange={(val) => setQuery({ ...query, isPassed: val })}
            options={[{ label: '通过', value: 1 }, { label: '失败', value: 0 }]} />
          <Button icon={<SearchOutlined />} onClick={fetchData}>查询</Button>
        </Space>
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新建用例</Button>
          <Button icon={<ThunderboltOutlined />} onClick={() => handleExecuteByConfig(query.configId)}>批量执行</Button>
        </Space>
      </div>
      <Table columns={columns} dataSource={data} rowKey="id" loading={loading}
        pagination={{ total, current: pageNum, pageSize, onChange: setPageNum, onShowSizeChange: (_, size) => setPageSize(size) }} />
      {formVisible && <DataMappingTestCaseForm visible={formVisible} onCancel={() => setFormVisible(false)} record={currentRecord} onSuccess={() => { setFormVisible(false); fetchData(); }} />}
      {resultVisible && <DataMappingTestCaseResult visible={resultVisible} onCancel={() => setResultVisible(false)} data={resultData} />}
    </div>
  );
};

export default DataMappingTestCaseList;

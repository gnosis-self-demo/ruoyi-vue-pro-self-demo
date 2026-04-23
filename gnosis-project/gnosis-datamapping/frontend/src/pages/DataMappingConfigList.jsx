import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined, PlayCircleOutlined } from '@ant-design/icons';
import { dataMappingApi } from '../api/dataMappingApi';
import DataMappingConfigForm from './DataMappingConfigForm';
import DataMappingConfigDetail from './DataMappingConfigDetail';
import DataMappingSimulator from './DataMappingSimulator';
import DataMappingVisualEditor from './DataMappingVisualEditor';
import DataMappingApiManagement from './DataMappingApiManagement';

const { Option } = Select;

const DataMappingConfigList = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchForm] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [simulatorModalVisible, setSimulatorModalVisible] = useState(false);
  const [editorModalVisible, setEditorModalVisible] = useState(false);
  const [apiModalVisible, setApiModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [currentEditRecord, setCurrentEditRecord] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async (params = {}) => {
    setLoading(true);
    try {
      const searchValues = searchForm.getFieldsValue();
      const request = {
        pageNum: params.pageNum || 1,
        pageSize: params.pageSize || 10,
        query: { ...searchValues, ...params.query }
      };
      const res = await dataMappingApi.pageList(request);
      if (res.code === 200) {
        setData(res.data.list || []);
        setTotal(res.data.total || 0);
      }
    } catch (error) {
      message.error('查询失败');
    }
    setLoading(false);
  };

  const handleSearch = () => {
    fetchData({ pageNum: 1 });
  };

  const handleReset = () => {
    searchForm.resetFields();
    fetchData({ pageNum: 1 });
  };

  const handleCreate = () => {
    setCreateModalVisible(true);
  };

  const handleCreateSuccess = () => {
    setCreateModalVisible(false);
    fetchData();
  };

  const handleEdit = (record) => {
    setCurrentEditRecord(record);
    setEditModalVisible(true);
  };

  const handleEditSuccess = () => {
    setEditModalVisible(false);
    setCurrentEditRecord(null);
    fetchData();
  };

  const handleDelete = async (id) => {
    try {
      const res = await dataMappingApi.delete(id);
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
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      const res = await dataMappingApi.batchDelete(selectedRowKeys);
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
      message.warning('请选择要启用的数据');
      return;
    }
    try {
      const res = await dataMappingApi.batchEnable(selectedRowKeys);
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
      message.warning('请选择要禁用的数据');
      return;
    }
    try {
      const res = await dataMappingApi.batchDisable(selectedRowKeys);
      if (res.code === 200) {
        message.success('批量禁用成功');
        setSelectedRowKeys([]);
        fetchData();
      }
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleDetail = (record) => {
    setCurrentRecord(record);
    setDetailModalVisible(true);
  };

  const handleTest = (record) => {
    setCurrentRecord(record);
    setSimulatorModalVisible(true);
  };

  const handleVisualEdit = (record) => {
    setCurrentEditRecord(record);
    setEditorModalVisible(true);
  };

  const handleApiManagement = () => {
    setApiModalVisible(true);
  };

  const columns = [
    {
      title: '配置名称',
      dataIndex: 'configName',
      key: 'configName',
      width: 150,
    },
    {
      title: '配置编码',
      dataIndex: 'configCode',
      key: 'configCode',
      width: 150,
    },
    {
      title: '系统ID',
      dataIndex: 'systemId',
      key: 'systemId',
      width: 120,
    },
    {
      title: '系统名称',
      dataIndex: 'systemName',
      key: 'systemName',
      width: 120,
    },
    {
      title: '配置版本',
      dataIndex: 'configVersion',
      key: 'configVersion',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '创建人ID',
      dataIndex: 'createUserId',
      key: 'createUserId',
      width: 120,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
    },
    {
      title: '更新人ID',
      dataIndex: 'updateUserId',
      key: 'updateUserId',
      width: 120,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 300,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>
            详情
          </Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Button type="link" size="small" icon={<PlayCircleOutlined />} onClick={() => handleTest(record)}>
            测试
          </Button>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  return (
    <div style={{ padding: 24 }}>
      <h2>数据映射配置</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="configName">
          <Input placeholder="配置名称" />
        </Form.Item>
        <Form.Item name="configCode">
          <Input placeholder="配置编码" />
        </Form.Item>
        <Form.Item name="status">
          <Select placeholder="状态" style={{ width: 120 }} allowClear>
            <Option value={1}>启用</Option>
            <Option value={0}>禁用</Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" onClick={handleSearch}>查询</Button>
            <Button onClick={handleReset}>重置</Button>
          </Space>
        </Form.Item>
      </Form>

      <div style={{ marginBottom: 16 }}>
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建
          </Button>
          <Button type="primary" icon={<EyeOutlined />} onClick={handleVisualEdit}>
            可视化配置
          </Button>
          <Button icon={<PlayCircleOutlined />} onClick={handleApiManagement}>
            API管理
          </Button>
          <Button icon={<ExportOutlined />} onClick={() => message.info('导出功能开发中')}>
            导出
          </Button>
          <Button icon={<ImportOutlined />} onClick={() => message.info('导入功能开发中')}>
            导入
          </Button>
          <Popconfirm title="确认批量删除?" onConfirm={handleBatchDelete}>
            <Button danger icon={<DeleteOutlined />}>批量删除</Button>
          </Popconfirm>
          <Button icon={<CheckCircleOutlined />} onClick={handleBatchEnable}>
            批量启用
          </Button>
          <Button icon={<PoweroffOutlined />} onClick={handleBatchDisable}>
            批量禁用
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        rowSelection={rowSelection}
        scroll={{ x: 1600 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />

      <Modal
        title="新建配置"
        open={createModalVisible}
        onCancel={() => setCreateModalVisible(false)}
        footer={null}
        width={800}
        destroyOnClose
      >
        <DataMappingConfigForm onSuccess={handleCreateSuccess} />
      </Modal>

      <Modal
        title="编辑配置"
        open={editModalVisible}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        footer={null}
        width={800}
        destroyOnClose
      >
        {currentEditRecord && (
          <DataMappingConfigForm record={currentEditRecord} onSuccess={handleEditSuccess} isEdit />
        )}
      </Modal>

      <Modal
        title="配置详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={800}
        destroyOnClose
      >
        {currentRecord && <DataMappingConfigDetail record={currentRecord} />}
      </Modal>

      <Modal
        title="模拟器"
        open={simulatorModalVisible}
        onCancel={() => { setSimulatorModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={1200}
        destroyOnClose
      >
        {currentRecord && <DataMappingSimulator configId={currentRecord.id} configCode={currentRecord.configCode} />}
      </Modal>

      <Modal
        title="可视化配置编辑器"
        open={editorModalVisible}
        onCancel={() => { setEditorModalVisible(false); setCurrentEditRecord(null); }}
        footer={null}
        width={1400}
        destroyOnClose
      >
        {currentEditRecord && <DataMappingVisualEditor record={currentEditRecord} />}
      </Modal>

      <Modal
        title="API管理"
        open={apiModalVisible}
        onCancel={() => setApiModalVisible(false)}
        footer={null}
        width={1200}
        destroyOnClose
      >
        <DataMappingApiManagement />
      </Modal>
    </div>
  );
};

export default DataMappingConfigList;

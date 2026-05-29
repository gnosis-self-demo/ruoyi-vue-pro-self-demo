import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions, DatePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { clearingApi } from '../api/accountsApi';

const { Option } = Select;
const { RangePicker } = DatePicker;

const clearingTypeMap = { CHANNEL: '渠道清算' };
const clearingTypeColorMap = { CHANNEL: 'cyan' };
const clearingStatusMap = { PENDING: '待清算', CLEARED: '已清算', FAILED: '失败' };
const clearingStatusColorMap = { PENDING: 'orange', CLEARED: 'green', FAILED: 'red' };

const ChannelClearing = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchForm] = Form.useForm();
  const [form] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [currentEditRecord, setCurrentEditRecord] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async (params = {}) => {
    setLoading(true);
    try {
      const searchValues = searchForm.getFieldsValue();
      const query = { ...searchValues, ...params.query };
      if (searchValues.dateRange && searchValues.dateRange.length === 2) {
        query.startDate = searchValues.dateRange[0].format('YYYY-MM-DD');
        query.endDate = searchValues.dateRange[1].format('YYYY-MM-DD');
        delete query.dateRange;
      }
      const req = {
        pageNum: params.pageNum || 1,
        pageSize: params.pageSize || 10,
        query,
      };
      const res = await clearingApi.pageList(req);
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
    form.resetFields();
    setCreateModalVisible(true);
  };

  const handleCreateSubmit = async () => {
    try {
      const values = await form.validateFields();
      const res = await clearingApi.create(values);
      if (res.code === 200) {
        message.success('创建成功');
        setCreateModalVisible(false);
        fetchData();
      } else {
        message.error(res.msg || '创建失败');
      }
    } catch (error) {
      if (error.errorFields) return;
      message.error('创建失败');
    }
  };

  const handleEdit = (record) => {
    setCurrentEditRecord(record);
    form.setFieldsValue(record);
    setEditModalVisible(true);
  };

  const handleEditSubmit = async () => {
    try {
      const values = await form.validateFields();
      const res = await clearingApi.update({ ...values, id: currentEditRecord.id });
      if (res.code === 200) {
        message.success('编辑成功');
        setEditModalVisible(false);
        setCurrentEditRecord(null);
        fetchData();
      } else {
        message.error(res.msg || '编辑失败');
      }
    } catch (error) {
      if (error.errorFields) return;
      message.error('编辑失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      const res = await clearingApi.delete(id);
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
      const res = await clearingApi.batchDelete(selectedRowKeys);
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
      const res = await clearingApi.batchEnable(selectedRowKeys);
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
      const res = await clearingApi.batchDisable(selectedRowKeys);
      if (res.code === 200) {
        message.success('批量禁用成功');
        setSelectedRowKeys([]);
        fetchData();
      }
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleDetail = async (record) => {
    try {
      const res = await clearingApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const handleExecuteClearing = async (record) => {
    try {
      const res = await clearingApi.executeClearing(record.id);
      if (res.code === 200) {
        message.success('执行清算成功');
        fetchData();
      } else {
        message.error(res.msg || '执行清算失败');
      }
    } catch (error) {
      message.error('执行清算失败');
    }
  };

  const columns = [
    { title: '清算单号', dataIndex: 'clearingNo', key: 'clearingNo', width: 150 },
    { title: '清算日期', dataIndex: 'clearingDate', key: 'clearingDate', width: 120 },
    { title: '渠道编码', dataIndex: 'channelCode', key: 'channelCode', width: 110 },
    { title: '渠道名称', dataIndex: 'channelName', key: 'channelName', width: 120 },
    {
      title: '清算类型', dataIndex: 'clearingType', key: 'clearingType', width: 110,
      render: (val) => <Tag color={clearingTypeColorMap[val]}>{clearingTypeMap[val] || val}</Tag>,
    },
    {
      title: '清算金额', dataIndex: 'clearingAmount', key: 'clearingAmount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '清算状态', dataIndex: 'clearingStatus', key: 'clearingStatus', width: 100,
      render: (val) => <Tag color={clearingStatusColorMap[val]}>{clearingStatusMap[val] || val}</Tag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 300, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>详情</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)} disabled={record.clearingStatus !== 'PENDING'}>编辑</Button>
          <Popconfirm title="确认执行清算?" onConfirm={() => handleExecuteClearing(record)}>
            <Button type="link" size="small" icon={<ThunderboltOutlined />} disabled={record.clearingStatus !== 'PENDING'}>执行清算</Button>
          </Popconfirm>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />} disabled={record.clearingStatus !== 'PENDING'}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  const formItems = (
    <>
      <Form.Item name="clearingDate" label="清算日期" rules={[{ required: true, message: '请输入清算日期' }]}>
        <Input placeholder="YYYY-MM-DD" />
      </Form.Item>
      <Form.Item name="channelCode" label="渠道编码" rules={[{ required: true, message: '请输入渠道编码' }]}>
        <Input placeholder="请输入渠道编码" />
      </Form.Item>
      <Form.Item name="channelName" label="渠道名称">
        <Input placeholder="请输入渠道名称" />
      </Form.Item>
      <Form.Item name="clearingType" label="清算类型" rules={[{ required: true, message: '请选择清算类型' }]}>
        <Select placeholder="请选择清算类型">
          <Option value="CHANNEL">渠道清算</Option>
        </Select>
      </Form.Item>
      <Form.Item name="clearingAmount" label="清算金额" rules={[{ required: true, message: '请输入清算金额' }]}>
        <Input placeholder="请输入清算金额" type="number" />
      </Form.Item>
      <Form.Item name="description" label="描述">
        <Input.TextArea rows={3} placeholder="请输入描述" />
      </Form.Item>
    </>
  );

  return (
    <div style={{ padding: 24 }}>
      <h2>渠道清算</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="clearingNo">
          <Input placeholder="清算单号" />
        </Form.Item>
        <Form.Item name="channelCode">
          <Input placeholder="渠道编码" />
        </Form.Item>
        <Form.Item name="clearingType">
          <Select placeholder="清算类型" style={{ width: 120 }} allowClear>
            <Option value="CHANNEL">渠道清算</Option>
          </Select>
        </Form.Item>
        <Form.Item name="clearingStatus">
          <Select placeholder="清算状态" style={{ width: 110 }} allowClear>
            <Option value="PENDING">待清算</Option>
            <Option value="CLEARED">已清算</Option>
            <Option value="FAILED">失败</Option>
          </Select>
        </Form.Item>
        <Form.Item name="dateRange">
          <RangePicker />
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
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新建</Button>
          <Button icon={<ExportOutlined />} onClick={() => message.info('导出功能开发中')}>导出</Button>
          <Button icon={<ImportOutlined />} onClick={() => message.info('导入功能开发中')}>导入</Button>
          <Popconfirm title="确认批量删除?" onConfirm={handleBatchDelete}>
            <Button danger icon={<DeleteOutlined />}>批量删除</Button>
          </Popconfirm>
          <Button icon={<CheckCircleOutlined />} onClick={handleBatchEnable}>批量启用</Button>
          <Button icon={<PoweroffOutlined />} onClick={handleBatchDisable}>批量禁用</Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        rowSelection={rowSelection}
        scroll={{ x: 1800 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />

      <Modal
        title="新建清算"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑清算"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="清算详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={700}
        destroyOnClose
      >
        {currentRecord && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="清算单号">{currentRecord.clearingNo}</Descriptions.Item>
            <Descriptions.Item label="清算日期">{currentRecord.clearingDate}</Descriptions.Item>
            <Descriptions.Item label="渠道编码">{currentRecord.channelCode}</Descriptions.Item>
            <Descriptions.Item label="渠道名称">{currentRecord.channelName}</Descriptions.Item>
            <Descriptions.Item label="清算类型">
              <Tag color={clearingTypeColorMap[currentRecord.clearingType]}>{clearingTypeMap[currentRecord.clearingType]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="清算金额">{currentRecord.clearingAmount !== undefined && currentRecord.clearingAmount !== null ? Number(currentRecord.clearingAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="清算状态">
              <Tag color={clearingStatusColorMap[currentRecord.clearingStatus]}>{clearingStatusMap[currentRecord.clearingStatus]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="描述">{currentRecord.description}</Descriptions.Item>
            <Descriptions.Item label="创建人ID">{currentRecord.createUserId}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{currentRecord.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新人ID">{currentRecord.updateUserId}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{currentRecord.updateTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  );
};

export default ChannelClearing;

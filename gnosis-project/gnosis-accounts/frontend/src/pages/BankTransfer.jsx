import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions, DatePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { transferApi } from '../api/accountsApi';

const { Option } = Select;
const { RangePicker } = DatePicker;

const transferTypeMap = { INCOME: '入款', EXPENSE: '出款' };
const transferTypeColorMap = { INCOME: 'green', EXPENSE: 'red' };
const transferStatusMap = { PENDING: '待结转', COMPLETED: '已完成', FAILED: '失败' };
const transferStatusColorMap = { PENDING: 'orange', COMPLETED: 'green', FAILED: 'red' };

const BankTransfer = () => {
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
      const res = await transferApi.pageList(req);
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
      const res = await transferApi.create(values);
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
      const res = await transferApi.update({ ...values, id: currentEditRecord.id });
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
      const res = await transferApi.delete(id);
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
      const res = await transferApi.batchDelete(selectedRowKeys);
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
      const res = await transferApi.batchEnable(selectedRowKeys);
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
      const res = await transferApi.batchDisable(selectedRowKeys);
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
      const res = await transferApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const handleExecuteTransfer = async (record) => {
    try {
      const res = await transferApi.executeTransfer(record.id);
      if (res.code === 200) {
        message.success('执行结转成功');
        fetchData();
      } else {
        message.error(res.msg || '执行结转失败');
      }
    } catch (error) {
      message.error('执行结转失败');
    }
  };

  const columns = [
    { title: '结转单号', dataIndex: 'transferNo', key: 'transferNo', width: 150 },
    { title: '结转日期', dataIndex: 'transferDate', key: 'transferDate', width: 120 },
    { title: '渠道编码', dataIndex: 'channelCode', key: 'channelCode', width: 110 },
    { title: '渠道名称', dataIndex: 'channelName', key: 'channelName', width: 120 },
    {
      title: '结转类型', dataIndex: 'transferType', key: 'transferType', width: 100,
      render: (val) => <Tag color={transferTypeColorMap[val]}>{transferTypeMap[val] || val}</Tag>,
    },
    {
      title: '结转金额', dataIndex: 'transferAmount', key: 'transferAmount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '转出账户', dataIndex: 'fromAccountName', key: 'fromAccountName', width: 120 },
    { title: '转入账户', dataIndex: 'toAccountName', key: 'toAccountName', width: 120 },
    {
      title: '结转状态', dataIndex: 'transferStatus', key: 'transferStatus', width: 100,
      render: (val) => <Tag color={transferStatusColorMap[val]}>{transferStatusMap[val] || val}</Tag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 300, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>详情</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)} disabled={record.transferStatus !== 'PENDING'}>编辑</Button>
          <Popconfirm title="确认执行结转?" onConfirm={() => handleExecuteTransfer(record)}>
            <Button type="link" size="small" icon={<ThunderboltOutlined />} disabled={record.transferStatus !== 'PENDING'}>执行结转</Button>
          </Popconfirm>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />} disabled={record.transferStatus !== 'PENDING'}>删除</Button>
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
      <Form.Item name="transferDate" label="结转日期" rules={[{ required: true, message: '请输入结转日期' }]}>
        <Input placeholder="YYYY-MM-DD" />
      </Form.Item>
      <Form.Item name="channelCode" label="渠道编码" rules={[{ required: true, message: '请输入渠道编码' }]}>
        <Input placeholder="请输入渠道编码" />
      </Form.Item>
      <Form.Item name="channelName" label="渠道名称">
        <Input placeholder="请输入渠道名称" />
      </Form.Item>
      <Form.Item name="transferType" label="结转类型" rules={[{ required: true, message: '请选择结转类型' }]}>
        <Select placeholder="请选择结转类型">
          <Option value="INCOME">入款</Option>
          <Option value="EXPENSE">出款</Option>
        </Select>
      </Form.Item>
      <Form.Item name="transferAmount" label="结转金额" rules={[{ required: true, message: '请输入结转金额' }]}>
        <Input placeholder="请输入结转金额" type="number" />
      </Form.Item>
      <Form.Item name="fromAccountName" label="转出账户">
        <Input placeholder="请输入转出账户" />
      </Form.Item>
      <Form.Item name="toAccountName" label="转入账户">
        <Input placeholder="请输入转入账户" />
      </Form.Item>
      <Form.Item name="description" label="描述">
        <Input.TextArea rows={3} placeholder="请输入描述" />
      </Form.Item>
    </>
  );

  return (
    <div style={{ padding: 24 }}>
      <h2>银存结转</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="transferNo">
          <Input placeholder="结转单号" />
        </Form.Item>
        <Form.Item name="channelCode">
          <Input placeholder="渠道编码" />
        </Form.Item>
        <Form.Item name="transferType">
          <Select placeholder="结转类型" style={{ width: 110 }} allowClear>
            <Option value="INCOME">入款</Option>
            <Option value="EXPENSE">出款</Option>
          </Select>
        </Form.Item>
        <Form.Item name="transferStatus">
          <Select placeholder="结转状态" style={{ width: 110 }} allowClear>
            <Option value="PENDING">待结转</Option>
            <Option value="COMPLETED">已完成</Option>
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
        scroll={{ x: 2000 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />

      <Modal
        title="新建结转"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑结转"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="结转详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={700}
        destroyOnClose
      >
        {currentRecord && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="结转单号">{currentRecord.transferNo}</Descriptions.Item>
            <Descriptions.Item label="结转日期">{currentRecord.transferDate}</Descriptions.Item>
            <Descriptions.Item label="渠道编码">{currentRecord.channelCode}</Descriptions.Item>
            <Descriptions.Item label="渠道名称">{currentRecord.channelName}</Descriptions.Item>
            <Descriptions.Item label="结转类型">
              <Tag color={transferTypeColorMap[currentRecord.transferType]}>{transferTypeMap[currentRecord.transferType]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="结转金额">{currentRecord.transferAmount !== undefined && currentRecord.transferAmount !== null ? Number(currentRecord.transferAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="转出账户">{currentRecord.fromAccountName}</Descriptions.Item>
            <Descriptions.Item label="转入账户">{currentRecord.toAccountName}</Descriptions.Item>
            <Descriptions.Item label="结转状态">
              <Tag color={transferStatusColorMap[currentRecord.transferStatus]}>{transferStatusMap[currentRecord.transferStatus]}</Tag>
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

export default BankTransfer;

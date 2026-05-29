import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined } from '@ant-design/icons';
import { accountApi } from '../api/accountsApi';

const { Option } = Select;

const accountTypeMap = {
  CHANNEL_CLEARING: '渠道清算', PENDING_SETTLEMENT: '待结算', CUSTOMER: '客户',
  BANK_DEPOSIT: '银存', CLEARED: '已清算', INCOME: '收入', EXPENSE: '支出',
};
const accountTypeColorMap = {
  CHANNEL_CLEARING: 'cyan', PENDING_SETTLEMENT: 'orange', CUSTOMER: 'geekblue',
  BANK_DEPOSIT: 'green', CLEARED: 'blue', INCOME: 'lime', EXPENSE: 'red',
};
const balanceDirectionMap = { DEBIT: '借方', CREDIT: '贷方' };

const AccountManage = () => {
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
      const req = {
        pageNum: params.pageNum || 1,
        pageSize: params.pageSize || 10,
        query: { ...searchValues, ...params.query }
      };
      const res = await accountApi.pageList(req);
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
      const res = await accountApi.create(values);
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
      const res = await accountApi.update({ ...values, id: currentEditRecord.id });
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
      const res = await accountApi.delete(id);
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
      const res = await accountApi.batchDelete(selectedRowKeys);
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
      const res = await accountApi.batchEnable(selectedRowKeys);
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
      const res = await accountApi.batchDisable(selectedRowKeys);
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
      const res = await accountApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const columns = [
    { title: '账户编号', dataIndex: 'accountNo', key: 'accountNo', width: 130 },
    { title: '账户名称', dataIndex: 'accountName', key: 'accountName', width: 120 },
    {
      title: '账户类型', dataIndex: 'accountType', key: 'accountType', width: 110,
      render: (val) => <Tag color={accountTypeColorMap[val]}>{accountTypeMap[val] || val}</Tag>,
    },
    { title: '渠道编码', dataIndex: 'channelCode', key: 'channelCode', width: 110 },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId', width: 110 },
    { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 110 },
    {
      title: '余额', dataIndex: 'balance', key: 'balance', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '冻结金额', dataIndex: 'frozenAmount', key: 'frozenAmount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '余额方向', dataIndex: 'balanceDirection', key: 'balanceDirection', width: 90,
      render: (val) => balanceDirectionMap[val] || val,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status) => <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>,
    },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    { title: '更新人ID', dataIndex: 'updateUserId', key: 'updateUserId', width: 120 },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 180 },
    {
      title: '操作', key: 'action', width: 220, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>详情</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
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
      <Form.Item name="accountNo" label="账户编号" rules={[{ required: true, message: '请输入账户编号' }]}>
        <Input placeholder="请输入账户编号" />
      </Form.Item>
      <Form.Item name="accountName" label="账户名称" rules={[{ required: true, message: '请输入账户名称' }]}>
        <Input placeholder="请输入账户名称" />
      </Form.Item>
      <Form.Item name="accountType" label="账户类型" rules={[{ required: true, message: '请选择账户类型' }]}>
        <Select placeholder="请选择账户类型">
          <Option value="CHANNEL_CLEARING">渠道清算</Option>
          <Option value="PENDING_SETTLEMENT">待结算</Option>
          <Option value="CUSTOMER">客户</Option>
          <Option value="BANK_DEPOSIT">银存</Option>
          <Option value="CLEARED">已清算</Option>
          <Option value="INCOME">收入</Option>
          <Option value="EXPENSE">支出</Option>
        </Select>
      </Form.Item>
      <Form.Item name="channelCode" label="渠道编码">
        <Input placeholder="请输入渠道编码" />
      </Form.Item>
      <Form.Item name="customerId" label="客户ID">
        <Input placeholder="请输入客户ID" />
      </Form.Item>
      <Form.Item name="customerName" label="客户名称">
        <Input placeholder="请输入客户名称" />
      </Form.Item>
      <Form.Item name="balanceDirection" label="余额方向">
        <Select placeholder="请选择余额方向">
          <Option value="DEBIT">借方</Option>
          <Option value="CREDIT">贷方</Option>
        </Select>
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态">
          <Option value={1}>启用</Option>
          <Option value={0}>禁用</Option>
        </Select>
      </Form.Item>
      <Form.Item name="description" label="描述">
        <Input.TextArea rows={3} placeholder="请输入描述" />
      </Form.Item>
    </>
  );

  return (
    <div style={{ padding: 24 }}>
      <h2>账户管理</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="accountNo">
          <Input placeholder="账户编号" />
        </Form.Item>
        <Form.Item name="accountName">
          <Input placeholder="账户名称" />
        </Form.Item>
        <Form.Item name="accountType">
          <Select placeholder="账户类型" style={{ width: 120 }} allowClear>
            <Option value="CHANNEL_CLEARING">渠道清算</Option>
            <Option value="PENDING_SETTLEMENT">待结算</Option>
            <Option value="CUSTOMER">客户</Option>
            <Option value="BANK_DEPOSIT">银存</Option>
            <Option value="CLEARED">已清算</Option>
            <Option value="INCOME">收入</Option>
            <Option value="EXPENSE">支出</Option>
          </Select>
        </Form.Item>
        <Form.Item name="channelCode">
          <Input placeholder="渠道编码" />
        </Form.Item>
        <Form.Item name="customerId">
          <Input placeholder="客户ID" />
        </Form.Item>
        <Form.Item name="status">
          <Select placeholder="状态" style={{ width: 100 }} allowClear>
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
        scroll={{ x: 2200 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />

      <Modal
        title="新建账户"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑账户"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="账户详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={700}
        destroyOnClose
      >
        {currentRecord && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="账户编号">{currentRecord.accountNo}</Descriptions.Item>
            <Descriptions.Item label="账户名称">{currentRecord.accountName}</Descriptions.Item>
            <Descriptions.Item label="账户类型">
              <Tag color={accountTypeColorMap[currentRecord.accountType]}>{accountTypeMap[currentRecord.accountType]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="渠道编码">{currentRecord.channelCode}</Descriptions.Item>
            <Descriptions.Item label="客户ID">{currentRecord.customerId}</Descriptions.Item>
            <Descriptions.Item label="客户名称">{currentRecord.customerName}</Descriptions.Item>
            <Descriptions.Item label="余额">{currentRecord.balance !== undefined && currentRecord.balance !== null ? Number(currentRecord.balance).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="冻结金额">{currentRecord.frozenAmount !== undefined && currentRecord.frozenAmount !== null ? Number(currentRecord.frozenAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="余额方向">{balanceDirectionMap[currentRecord.balanceDirection]}</Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={currentRecord.status === 1 ? 'green' : 'red'}>{currentRecord.status === 1 ? '启用' : '禁用'}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="描述" span={2}>{currentRecord.description}</Descriptions.Item>
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

export default AccountManage;

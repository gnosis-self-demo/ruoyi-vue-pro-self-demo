import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions, DatePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { settlementApi } from '../api/accountsApi';

const { Option } = Select;
const { RangePicker } = DatePicker;

const settlementTypeMap = { CUSTOMER: '客户结算' };
const settlementTypeColorMap = { CUSTOMER: 'purple' };
const settlementStatusMap = { PENDING: '待结算', SETTLED: '已结算', FAILED: '失败' };
const settlementStatusColorMap = { PENDING: 'orange', SETTLED: 'green', FAILED: 'red' };

const CustomerSettlement = () => {
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
      const res = await settlementApi.pageList(req);
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
      const res = await settlementApi.create(values);
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
      const res = await settlementApi.update({ ...values, id: currentEditRecord.id });
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
      const res = await settlementApi.delete(id);
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
      const res = await settlementApi.batchDelete(selectedRowKeys);
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
      const res = await settlementApi.batchEnable(selectedRowKeys);
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
      const res = await settlementApi.batchDisable(selectedRowKeys);
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
      const res = await settlementApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const handleExecuteSettlement = async (record) => {
    try {
      const res = await settlementApi.executeSettlement(record.id);
      if (res.code === 200) {
        message.success('执行结算成功');
        fetchData();
      } else {
        message.error(res.msg || '执行结算失败');
      }
    } catch (error) {
      message.error('执行结算失败');
    }
  };

  const columns = [
    { title: '结算单号', dataIndex: 'settlementNo', key: 'settlementNo', width: 150 },
    { title: '结算日期', dataIndex: 'settlementDate', key: 'settlementDate', width: 120 },
    { title: '客户ID', dataIndex: 'customerId', key: 'customerId', width: 110 },
    { title: '客户名称', dataIndex: 'customerName', key: 'customerName', width: 120 },
    {
      title: '结算类型', dataIndex: 'settlementType', key: 'settlementType', width: 110,
      render: (val) => <Tag color={settlementTypeColorMap[val]}>{settlementTypeMap[val] || val}</Tag>,
    },
    {
      title: '结算金额', dataIndex: 'settlementAmount', key: 'settlementAmount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '手续费', dataIndex: 'feeAmount', key: 'feeAmount', width: 100,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '实际金额', dataIndex: 'actualAmount', key: 'actualAmount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '转出账户', dataIndex: 'fromAccountName', key: 'fromAccountName', width: 120 },
    { title: '转入账户', dataIndex: 'toAccountName', key: 'toAccountName', width: 120 },
    {
      title: '结算状态', dataIndex: 'settlementStatus', key: 'settlementStatus', width: 100,
      render: (val) => <Tag color={settlementStatusColorMap[val]}>{settlementStatusMap[val] || val}</Tag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 300, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>详情</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)} disabled={record.settlementStatus !== 'PENDING'}>编辑</Button>
          <Popconfirm title="确认执行结算?" onConfirm={() => handleExecuteSettlement(record)}>
            <Button type="link" size="small" icon={<ThunderboltOutlined />} disabled={record.settlementStatus !== 'PENDING'}>执行结算</Button>
          </Popconfirm>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />} disabled={record.settlementStatus !== 'PENDING'}>删除</Button>
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
      <Form.Item name="settlementDate" label="结算日期" rules={[{ required: true, message: '请输入结算日期' }]}>
        <Input placeholder="YYYY-MM-DD" />
      </Form.Item>
      <Form.Item name="customerId" label="客户ID" rules={[{ required: true, message: '请输入客户ID' }]}>
        <Input placeholder="请输入客户ID" />
      </Form.Item>
      <Form.Item name="customerName" label="客户名称">
        <Input placeholder="请输入客户名称" />
      </Form.Item>
      <Form.Item name="settlementType" label="结算类型" rules={[{ required: true, message: '请选择结算类型' }]}>
        <Select placeholder="请选择结算类型">
          <Option value="CUSTOMER">客户结算</Option>
        </Select>
      </Form.Item>
      <Form.Item name="settlementAmount" label="结算金额" rules={[{ required: true, message: '请输入结算金额' }]}>
        <Input placeholder="请输入结算金额" type="number" />
      </Form.Item>
      <Form.Item name="feeAmount" label="手续费">
        <Input placeholder="请输入手续费" type="number" />
      </Form.Item>
      <Form.Item name="actualAmount" label="实际金额">
        <Input placeholder="请输入实际金额" type="number" />
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
      <h2>客资结算</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="settlementNo">
          <Input placeholder="结算单号" />
        </Form.Item>
        <Form.Item name="customerId">
          <Input placeholder="客户ID" />
        </Form.Item>
        <Form.Item name="customerName">
          <Input placeholder="客户名称" />
        </Form.Item>
        <Form.Item name="settlementType">
          <Select placeholder="结算类型" style={{ width: 120 }} allowClear>
            <Option value="CUSTOMER">客户结算</Option>
          </Select>
        </Form.Item>
        <Form.Item name="settlementStatus">
          <Select placeholder="结算状态" style={{ width: 110 }} allowClear>
            <Option value="PENDING">待结算</Option>
            <Option value="SETTLED">已结算</Option>
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
        scroll={{ x: 2400 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />

      <Modal
        title="新建结算"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑结算"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="结算详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={700}
        destroyOnClose
      >
        {currentRecord && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="结算单号">{currentRecord.settlementNo}</Descriptions.Item>
            <Descriptions.Item label="结算日期">{currentRecord.settlementDate}</Descriptions.Item>
            <Descriptions.Item label="客户ID">{currentRecord.customerId}</Descriptions.Item>
            <Descriptions.Item label="客户名称">{currentRecord.customerName}</Descriptions.Item>
            <Descriptions.Item label="结算类型">
              <Tag color={settlementTypeColorMap[currentRecord.settlementType]}>{settlementTypeMap[currentRecord.settlementType]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="结算金额">{currentRecord.settlementAmount !== undefined && currentRecord.settlementAmount !== null ? Number(currentRecord.settlementAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="手续费">{currentRecord.feeAmount !== undefined && currentRecord.feeAmount !== null ? Number(currentRecord.feeAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="实际金额">{currentRecord.actualAmount !== undefined && currentRecord.actualAmount !== null ? Number(currentRecord.actualAmount).toFixed(2) : '0.00'}</Descriptions.Item>
            <Descriptions.Item label="转出账户">{currentRecord.fromAccountName}</Descriptions.Item>
            <Descriptions.Item label="转入账户">{currentRecord.toAccountName}</Descriptions.Item>
            <Descriptions.Item label="结算状态">
              <Tag color={settlementStatusColorMap[currentRecord.settlementStatus]}>{settlementStatusMap[currentRecord.settlementStatus]}</Tag>
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

export default CustomerSettlement;

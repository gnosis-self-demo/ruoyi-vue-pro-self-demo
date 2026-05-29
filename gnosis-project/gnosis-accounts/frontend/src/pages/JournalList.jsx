import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions, DatePicker } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined, CheckOutlined, UndoOutlined } from '@ant-design/icons';
import { journalApi, entryApi } from '../api/accountsApi';

const { Option } = Select;
const { RangePicker } = DatePicker;

const businessTypeMap = { COLLECTION: '收单', REFUND: '退款', PAYMENT: '付款' };
const businessTypeColorMap = { COLLECTION: 'green', REFUND: 'orange', PAYMENT: 'blue' };
const transactionTypeMap = { ONLINE: '联机交易', CHANNEL_CLEARING: '渠道清算', BANK_TRANSFER: '银存结转', CUSTOMER_SETTLEMENT: '客资结算' };
const transactionTypeColorMap = { ONLINE: 'blue', CHANNEL_CLEARING: 'cyan', BANK_TRANSFER: 'green', CUSTOMER_SETTLEMENT: 'purple' };
const statusMap = { DRAFT: '草稿', POSTED: '已入账', REVERSED: '已冲正' };
const statusColorMap = { DRAFT: 'blue', POSTED: 'green', REVERSED: 'red' };

const JournalList = () => {
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
  const [entryList, setEntryList] = useState([]);

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
      const res = await journalApi.pageList(req);
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
      const res = await journalApi.create(values);
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
      const res = await journalApi.update({ ...values, id: currentEditRecord.id });
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
      const res = await journalApi.delete(id);
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
      const res = await journalApi.batchDelete(selectedRowKeys);
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
      const res = await journalApi.batchEnable(selectedRowKeys);
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
      const res = await journalApi.batchDisable(selectedRowKeys);
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
      const res = await journalApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
        const entryRes = await entryApi.listByJournal(record.id);
        if (entryRes.code === 200) {
          setEntryList(entryRes.data || []);
        }
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const handlePost = async (record) => {
    try {
      const res = await journalApi.post(record.id);
      if (res.code === 200) {
        message.success('入账成功');
        fetchData();
      } else {
        message.error(res.msg || '入账失败');
      }
    } catch (error) {
      message.error('入账失败');
    }
  };

  const handleReverse = async (record) => {
    try {
      const res = await journalApi.reverse(record.id);
      if (res.code === 200) {
        message.success('冲正成功');
        fetchData();
      } else {
        message.error(res.msg || '冲正失败');
      }
    } catch (error) {
      message.error('冲正失败');
    }
  };

  const columns = [
    { title: '流水号', dataIndex: 'journalNo', key: 'journalNo', width: 150 },
    {
      title: '业务类型', dataIndex: 'businessType', key: 'businessType', width: 100,
      render: (val) => <Tag color={businessTypeColorMap[val]}>{businessTypeMap[val] || val}</Tag>,
    },
    { title: '业务ID', dataIndex: 'businessId', key: 'businessId', width: 120 },
    {
      title: '交易类型', dataIndex: 'transactionType', key: 'transactionType', width: 110,
      render: (val) => <Tag color={transactionTypeColorMap[val]}>{transactionTypeMap[val] || val}</Tag>,
    },
    {
      title: '借方合计', dataIndex: 'totalDebit', key: 'totalDebit', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '贷方合计', dataIndex: 'totalCredit', key: 'totalCredit', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '记账日期', dataIndex: 'accountingDate', key: 'accountingDate', width: 120 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 90,
      render: (val) => <Tag color={statusColorMap[val]}>{statusMap[val] || val}</Tag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
    {
      title: '操作', key: 'action', width: 320, fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleDetail(record)}>详情</Button>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)} disabled={record.status !== 'DRAFT'}>编辑</Button>
          <Popconfirm title="确认入账?" onConfirm={() => handlePost(record)}>
            <Button type="link" size="small" icon={<CheckOutlined />} disabled={record.status !== 'DRAFT'}>入账</Button>
          </Popconfirm>
          <Popconfirm title="确认冲正?" onConfirm={() => handleReverse(record)}>
            <Button type="link" size="small" danger icon={<UndoOutlined />} disabled={record.status !== 'POSTED'}>冲正</Button>
          </Popconfirm>
          <Popconfirm title="确认删除?" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />} disabled={record.status !== 'DRAFT'}>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const entryColumns = [
    { title: '账户编号', dataIndex: 'accountNo', key: 'accountNo', width: 130 },
    { title: '账户名称', dataIndex: 'accountName', key: 'accountName', width: 120 },
    { title: '科目编码', dataIndex: 'subjectCode', key: 'subjectCode', width: 110 },
    { title: '科目名称', dataIndex: 'subjectName', key: 'subjectName', width: 110 },
    {
      title: '方向', dataIndex: 'entryDirection', key: 'entryDirection', width: 80,
      render: (val) => <Tag color={val === 'DEBIT' ? 'red' : 'green'}>{val === 'DEBIT' ? '借' : '贷'}</Tag>,
    },
    {
      title: '金额', dataIndex: 'amount', key: 'amount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  const formItems = (
    <>
      <Form.Item name="businessType" label="业务类型" rules={[{ required: true, message: '请选择业务类型' }]}>
        <Select placeholder="请选择业务类型">
          <Option value="COLLECTION">收单</Option>
          <Option value="REFUND">退款</Option>
          <Option value="PAYMENT">付款</Option>
        </Select>
      </Form.Item>
      <Form.Item name="businessId" label="业务ID" rules={[{ required: true, message: '请输入业务ID' }]}>
        <Input placeholder="请输入业务ID" />
      </Form.Item>
      <Form.Item name="transactionType" label="交易类型" rules={[{ required: true, message: '请选择交易类型' }]}>
        <Select placeholder="请选择交易类型">
          <Option value="ONLINE">联机交易</Option>
          <Option value="CHANNEL_CLEARING">渠道清算</Option>
          <Option value="BANK_TRANSFER">银存结转</Option>
          <Option value="CUSTOMER_SETTLEMENT">客资结算</Option>
        </Select>
      </Form.Item>
      <Form.Item name="accountingDate" label="记账日期">
        <Input placeholder="YYYY-MM-DD" />
      </Form.Item>
      <Form.Item name="description" label="描述">
        <Input.TextArea rows={3} placeholder="请输入描述" />
      </Form.Item>
    </>
  );

  return (
    <div style={{ padding: 24 }}>
      <h2>会计流水</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="journalNo">
          <Input placeholder="流水号" />
        </Form.Item>
        <Form.Item name="businessType">
          <Select placeholder="业务类型" style={{ width: 110 }} allowClear>
            <Option value="COLLECTION">收单</Option>
            <Option value="REFUND">退款</Option>
            <Option value="PAYMENT">付款</Option>
          </Select>
        </Form.Item>
        <Form.Item name="transactionType">
          <Select placeholder="交易类型" style={{ width: 120 }} allowClear>
            <Option value="ONLINE">联机交易</Option>
            <Option value="CHANNEL_CLEARING">渠道清算</Option>
            <Option value="BANK_TRANSFER">银存结转</Option>
            <Option value="CUSTOMER_SETTLEMENT">客资结算</Option>
          </Select>
        </Form.Item>
        <Form.Item name="status">
          <Select placeholder="状态" style={{ width: 100 }} allowClear>
            <Option value="DRAFT">草稿</Option>
            <Option value="POSTED">已入账</Option>
            <Option value="REVERSED">已冲正</Option>
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
        title="新建流水"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑流水"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="流水详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); setEntryList([]); }}
        footer={null}
        width={1000}
        destroyOnClose
      >
        {currentRecord && (
          <>
            <Descriptions bordered column={2} style={{ marginBottom: 16 }}>
              <Descriptions.Item label="流水号">{currentRecord.journalNo}</Descriptions.Item>
              <Descriptions.Item label="业务类型">
                <Tag color={businessTypeColorMap[currentRecord.businessType]}>{businessTypeMap[currentRecord.businessType]}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="业务ID">{currentRecord.businessId}</Descriptions.Item>
              <Descriptions.Item label="交易类型">
                <Tag color={transactionTypeColorMap[currentRecord.transactionType]}>{transactionTypeMap[currentRecord.transactionType]}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="借方合计">{currentRecord.totalDebit !== undefined && currentRecord.totalDebit !== null ? Number(currentRecord.totalDebit).toFixed(2) : '0.00'}</Descriptions.Item>
              <Descriptions.Item label="贷方合计">{currentRecord.totalCredit !== undefined && currentRecord.totalCredit !== null ? Number(currentRecord.totalCredit).toFixed(2) : '0.00'}</Descriptions.Item>
              <Descriptions.Item label="记账日期">{currentRecord.accountingDate}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={statusColorMap[currentRecord.status]}>{statusMap[currentRecord.status]}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="描述" span={2}>{currentRecord.description}</Descriptions.Item>
              <Descriptions.Item label="创建人ID">{currentRecord.createUserId}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{currentRecord.createTime}</Descriptions.Item>
              <Descriptions.Item label="更新人ID">{currentRecord.updateUserId}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{currentRecord.updateTime}</Descriptions.Item>
            </Descriptions>
            <h4>分录明细</h4>
            <Table
              columns={entryColumns}
              dataSource={entryList}
              rowKey="id"
              size="small"
              pagination={false}
            />
          </>
        )}
      </Modal>
    </div>
  );
};

export default JournalList;

import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Modal, Form, message, Popconfirm, Tag, Descriptions } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined, PoweroffOutlined, CheckCircleOutlined, ExportOutlined, ImportOutlined } from '@ant-design/icons';
import { subjectApi } from '../api/accountsApi';

const { Option } = Select;

const subjectTypeMap = { ASSET: '资产', LIABILITY: '负债', INCOME: '收入', EXPENSE: '支出' };
const subjectTypeColorMap = { ASSET: 'blue', LIABILITY: 'orange', INCOME: 'green', EXPENSE: 'red' };
const subjectCategoryMap = { CHANNEL: '渠道清算', TRANSITION: '交易过渡', CUSTOMER: '客户负债', FEE: '收入费用' };
const subjectCategoryColorMap = { CHANNEL: 'cyan', TRANSITION: 'purple', CUSTOMER: 'geekblue', FEE: 'magenta' };
const balanceDirectionMap = { DEBIT: '借方', CREDIT: '贷方' };

const SubjectManage = () => {
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
      const res = await subjectApi.pageList(req);
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
      const res = await subjectApi.create(values);
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
      const res = await subjectApi.update({ ...values, id: currentEditRecord.id });
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
      const res = await subjectApi.delete(id);
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
      const res = await subjectApi.batchDelete(selectedRowKeys);
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
      const res = await subjectApi.batchEnable(selectedRowKeys);
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
      const res = await subjectApi.batchDisable(selectedRowKeys);
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
      const res = await subjectApi.detail(record.id);
      if (res.code === 200) {
        setCurrentRecord(res.data);
        setDetailModalVisible(true);
      }
    } catch (error) {
      message.error('获取详情失败');
    }
  };

  const columns = [
    { title: '科目编码', dataIndex: 'subjectCode', key: 'subjectCode', width: 120 },
    { title: '科目名称', dataIndex: 'subjectName', key: 'subjectName', width: 120 },
    {
      title: '科目类型', dataIndex: 'subjectType', key: 'subjectType', width: 100,
      render: (val) => <Tag color={subjectTypeColorMap[val]}>{subjectTypeMap[val] || val}</Tag>,
    },
    {
      title: '科目分类', dataIndex: 'subjectCategory', key: 'subjectCategory', width: 110,
      render: (val) => <Tag color={subjectCategoryColorMap[val]}>{subjectCategoryMap[val] || val}</Tag>,
    },
    {
      title: '余额方向', dataIndex: 'balanceDirection', key: 'balanceDirection', width: 90,
      render: (val) => balanceDirectionMap[val] || val,
    },
    { title: '层级', dataIndex: 'level', key: 'level', width: 70 },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 80,
      render: (status) => <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>,
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
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
      <Form.Item name="subjectCode" label="科目编码" rules={[{ required: true, message: '请输入科目编码' }]}>
        <Input placeholder="请输入科目编码" />
      </Form.Item>
      <Form.Item name="subjectName" label="科目名称" rules={[{ required: true, message: '请输入科目名称' }]}>
        <Input placeholder="请输入科目名称" />
      </Form.Item>
      <Form.Item name="subjectType" label="科目类型" rules={[{ required: true, message: '请选择科目类型' }]}>
        <Select placeholder="请选择科目类型">
          <Option value="ASSET">资产</Option>
          <Option value="LIABILITY">负债</Option>
          <Option value="INCOME">收入</Option>
          <Option value="EXPENSE">支出</Option>
        </Select>
      </Form.Item>
      <Form.Item name="subjectCategory" label="科目分类" rules={[{ required: true, message: '请选择科目分类' }]}>
        <Select placeholder="请选择科目分类">
          <Option value="CHANNEL">渠道清算</Option>
          <Option value="TRANSITION">交易过渡</Option>
          <Option value="CUSTOMER">客户负债</Option>
          <Option value="FEE">收入费用</Option>
        </Select>
      </Form.Item>
      <Form.Item name="balanceDirection" label="余额方向" rules={[{ required: true, message: '请选择余额方向' }]}>
        <Select placeholder="请选择余额方向">
          <Option value="DEBIT">借方</Option>
          <Option value="CREDIT">贷方</Option>
        </Select>
      </Form.Item>
      <Form.Item name="level" label="层级">
        <Input placeholder="请输入层级" type="number" />
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
      <h2>科目管理</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="subjectCode">
          <Input placeholder="科目编码" />
        </Form.Item>
        <Form.Item name="subjectName">
          <Input placeholder="科目名称" />
        </Form.Item>
        <Form.Item name="subjectType">
          <Select placeholder="科目类型" style={{ width: 120 }} allowClear>
            <Option value="ASSET">资产</Option>
            <Option value="LIABILITY">负债</Option>
            <Option value="INCOME">收入</Option>
            <Option value="EXPENSE">支出</Option>
          </Select>
        </Form.Item>
        <Form.Item name="subjectCategory">
          <Select placeholder="科目分类" style={{ width: 120 }} allowClear>
            <Option value="CHANNEL">渠道清算</Option>
            <Option value="TRANSITION">交易过渡</Option>
            <Option value="CUSTOMER">客户负债</Option>
            <Option value="FEE">收入费用</Option>
          </Select>
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
        title="新建科目"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="编辑科目"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => { setEditModalVisible(false); setCurrentEditRecord(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">{formItems}</Form>
      </Modal>

      <Modal
        title="科目详情"
        open={detailModalVisible}
        onCancel={() => { setDetailModalVisible(false); setCurrentRecord(null); }}
        footer={null}
        width={700}
        destroyOnClose
      >
        {currentRecord && (
          <Descriptions bordered column={2}>
            <Descriptions.Item label="科目编码">{currentRecord.subjectCode}</Descriptions.Item>
            <Descriptions.Item label="科目名称">{currentRecord.subjectName}</Descriptions.Item>
            <Descriptions.Item label="科目类型">
              <Tag color={subjectTypeColorMap[currentRecord.subjectType]}>{subjectTypeMap[currentRecord.subjectType]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="科目分类">
              <Tag color={subjectCategoryColorMap[currentRecord.subjectCategory]}>{subjectCategoryMap[currentRecord.subjectCategory]}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="余额方向">{balanceDirectionMap[currentRecord.balanceDirection]}</Descriptions.Item>
            <Descriptions.Item label="层级">{currentRecord.level}</Descriptions.Item>
            <Descriptions.Item label="状态">
              <Tag color={currentRecord.status === 1 ? 'green' : 'red'}>{currentRecord.status === 1 ? '启用' : '禁用'}</Tag>
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

export default SubjectManage;

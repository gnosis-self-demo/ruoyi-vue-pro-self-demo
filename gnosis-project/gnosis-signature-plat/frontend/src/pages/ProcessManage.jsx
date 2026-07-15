import React, { useState, useEffect } from 'react';
import {
  Table, Form, Button, Space, Modal, Tag, Popconfirm, message, Card,
  Input, Select, InputNumber, DatePicker, Upload, Row, Col,
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, EyeOutlined,
  DownloadOutlined, UploadOutlined, CheckOutlined, CloseOutlined,
  SwapOutlined, TeamOutlined, ApartmentOutlined,
} from '@ant-design/icons';
import { processApi } from '../api/signatureApi';
import dayjs from 'dayjs';

const { TextArea } = Input;
const { Option } = Select;

// 流程类型映射
const processTypeMap = {
  SEQUENTIAL: { color: 'blue', text: '顺序签' },
  COUNTER: { color: 'orange', text: '会签' },
  PARALLEL: { color: 'green', text: '并行签' },
};

// 状态映射
const statusMap = {
  0: { color: 'default', text: '禁用' },
  1: { color: 'success', text: '启用' },
};

const ProcessManage = () => {
  const [form] = Form.useForm();
  const [modalForm] = Form.useForm();
  const [detailForm] = Form.useForm();

  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  const [modalVisible, setModalVisible] = useState(false);
  const [modalType, setModalType] = useState('create'); // create / edit / detail
  const [currentRecord, setCurrentRecord] = useState(null);

  // 初始化加载数据
  useEffect(() => {
    fetchList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // 获取列表数据
  const fetchList = async (params = {}) => {
    setLoading(true);
    try {
      const searchFields = form.getFieldsValue();
      const requestData = {
        pageNum: params.current || pagination.current,
        pageSize: params.pageSize || pagination.pageSize,
        query: searchFields,
      };
      const res = await processApi.pageList(requestData);
      if (res && res.data) {
        setDataSource(res.data.list || res.data.records || []);
        setPagination((prev) => ({
          ...prev,
          total: res.data.total || 0,
          current: params.current || prev.current,
          pageSize: params.pageSize || prev.pageSize,
        }));
      }
    } catch (error) {
      message.error('获取列表数据失败');
    } finally {
      setLoading(false);
    }
  };

  // 搜索
  const handleSearch = () => {
    fetchList({ current: 1, pageSize: pagination.pageSize });
  };

  // 重置搜索
  const handleReset = () => {
    form.resetFields();
    fetchList({ current: 1, pageSize: pagination.pageSize });
  };

  // 表格分页变化
  const handleTableChange = (pageInfo) => {
    fetchList({ current: pageInfo.current, pageSize: pageInfo.pageSize });
  };

  // 打开新建/编辑/详情弹窗
  const openModal = async (type, record) => {
    setModalType(type);
    setCurrentRecord(record || null);

    if (type === 'create') {
      modalForm.resetFields();
      modalForm.setFieldsValue({ signOrder: 1, status: 1 });
    } else if (type === 'edit' && record) {
      modalForm.setFieldsValue({ ...record });
    } else if (type === 'detail' && record) {
      detailForm.setFieldsValue({ ...record });
    }

    setModalVisible(true);
  };

  // 关闭弹窗
  const closeModal = () => {
    setModalVisible(false);
    setCurrentRecord(null);
  };

  // 提交表单（新建/编辑）
  const handleSubmit = async () => {
    try {
      const values = await modalForm.validateFields();
      if (modalType === 'create') {
        await processApi.create(values);
        message.success('创建成功');
      } else if (modalType === 'edit') {
        await processApi.update({ id: currentRecord.id, ...values });
        message.success('更新成功');
      }
      closeModal();
      fetchList();
    } catch (error) {
      if (error.errorFields) return;
      message.error('操作失败');
    }
  };

  // 删除
  const handleDelete = async (id) => {
    try {
      await processApi.delete(id);
      message.success('删除成功');
      fetchList();
    } catch (error) {
      message.error('删除失败');
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请至少选择一条记录');
      return;
    }
    Modal.confirm({
      title: '确认删除',
      content: `确认删除选中的 ${selectedRowKeys.length} 条记录？`,
      onOk: async () => {
        try {
          await processApi.batchDelete(selectedRowKeys);
          message.success('批量删除成功');
          setSelectedRowKeys([]);
          fetchList();
        } catch (error) {
          message.error('批量删除失败');
        }
      },
    });
  };

  // 批量启用
  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请至少选择一条记录');
      return;
    }
    try {
      await processApi.batchEnable(selectedRowKeys);
      message.success('批量启用成功');
      setSelectedRowKeys([]);
      fetchList();
    } catch (error) {
      message.error('批量启用失败');
    }
  };

  // 批量禁用
  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请至少选择一条记录');
      return;
    }
    try {
      await processApi.batchDisable(selectedRowKeys);
      message.success('批量禁用成功');
      setSelectedRowKeys([]);
      fetchList();
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  // 导出
  const handleExport = async () => {
    try {
      const response = await processApi.export(selectedRowKeys);
      const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'process_export.xlsx';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      message.success('导出成功');
    } catch (error) {
      console.error('导出失败:', error);
    }
  };

  // 导入
  const handleImport = (info) => {
    const { status, response } = info.file;
    if (status === 'done') {
      if (response && response.code === 200) {
        message.success('导入成功');
        fetchList();
      } else {
        message.error('导入失败');
      }
    } else if (status === 'error') {
      message.error('导入失败');
    }
  };

  // 行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  // 表格列定义
  const columns = [
    { title: '流程编码', dataIndex: 'processCode', key: 'processCode', width: 140 },
    { title: '流程名称', dataIndex: 'processName', key: 'processName', width: 180 },
    {
      title: '流程类型',
      dataIndex: 'processType',
      key: 'processType',
      width: 110,
      render: (val) => {
        const item = processTypeMap[val] || { color: 'default', text: val };
        return <Tag color={item.color}>{item.text}</Tag>;
      },
    },
    { title: '流程配置', dataIndex: 'processConfig', key: 'processConfig', ellipsis: true },
    { title: '签署顺序', dataIndex: 'signOrder', key: 'signOrder', width: 100 },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 90,
      render: (val) => {
        const item = statusMap[val] || { color: 'default', text: val };
        return <Tag color={item.color}>{item.text}</Tag>;
      },
    },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 120 },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 170,
      render: (val) => (val ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    { title: '更新人ID', dataIndex: 'updateUserId', key: 'updateUserId', width: 120 },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 170,
      render: (val) => (val ? dayjs(val).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => openModal('detail', record)}
          >
            详情
          </Button>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => openModal('edit', record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确认删除该记录？"
            onConfirm={() => handleDelete(record.id)}
            okText="确认"
            cancelText="取消"
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 搜索表单
  const searchForm = (
    <Form form={form} layout="inline">
      <Row gutter={[16, 16]} style={{ width: '100%' }}>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="processCode" label="流程编码">
            <Input placeholder="请输入流程编码" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="processName" label="流程名称">
            <Input placeholder="请输入流程名称" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="processType" label="流程类型">
            <Select placeholder="请选择流程类型" allowClear>
              {Object.entries(processTypeMap).map(([key, val]) => (
                <Option key={key} value={key}>
                  <Tag color={val.color}>{val.text}</Tag>
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="processConfig" label="流程配置">
            <Input placeholder="请输入流程配置" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="signOrder" label="签署顺序">
            <InputNumber placeholder="请输入签署顺序" style={{ width: '100%' }} />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="description" label="描述">
            <Input placeholder="请输入描述" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择状态" allowClear>
              {Object.entries(statusMap).map(([key, val]) => (
                <Option key={key} value={Number(key)}>
                  <Tag color={val.color}>{val.text}</Tag>
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="createUserId" label="创建人ID">
            <Input placeholder="请输入创建人ID" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="createTimeStart" label="创建时间起">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="createTimeEnd" label="创建时间止">
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Form.Item name="updateUserId" label="更新人ID">
            <Input placeholder="请输入更新人ID" allowClear />
          </Form.Item>
        </Col>
        <Col xs={24} sm={12} md={8} lg={6}>
          <Button type="primary" onClick={handleSearch}>
            查询
          </Button>
          <Button style={{ marginLeft: 8 }} onClick={handleReset}>
            重置
          </Button>
        </Col>
      </Row>
    </Form>
  );

  // 弹窗表单
  const renderModalForm = (formInstance) => (
    <Form form={formInstance} labelCol={{ span: 6 }} wrapperCol={{ span: 16 }}>
      <Form.Item
        name="processCode"
        label="流程编码"
        rules={[{ required: true, message: '请输入流程编码' }]}
      >
        <Input placeholder="请输入流程编码" disabled={modalType === 'detail'} />
      </Form.Item>
      <Form.Item
        name="processName"
        label="流程名称"
        rules={[{ required: true, message: '请输入流程名称' }]}
      >
        <Input placeholder="请输入流程名称" disabled={modalType === 'detail'} />
      </Form.Item>
      <Form.Item
        name="processType"
        label="流程类型"
        rules={[{ required: true, message: '请选择流程类型' }]}
      >
        <Select placeholder="请选择流程类型" disabled={modalType === 'detail'}>
          {Object.entries(processTypeMap).map(([key, val]) => (
            <Option key={key} value={key}>
              <Tag color={val.color}>{val.text}</Tag>
            </Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item name="processConfig" label="流程配置">
        <TextArea rows={3} placeholder="请输入流程配置" disabled={modalType === 'detail'} />
      </Form.Item>
      <Form.Item name="signOrder" label="签署顺序">
        <InputNumber style={{ width: '100%' }} placeholder="请输入签署顺序" disabled={modalType === 'detail'} />
      </Form.Item>
      <Form.Item name="description" label="描述">
        <TextArea rows={2} placeholder="请输入描述" disabled={modalType === 'detail'} />
      </Form.Item>
      <Form.Item
        name="status"
        label="状态"
        rules={[{ required: true, message: '请选择状态' }]}
      >
        <Select placeholder="请选择状态" disabled={modalType === 'detail'}>
          {Object.entries(statusMap).map(([key, val]) => (
            <Option key={key} value={Number(key)}>
              <Tag color={val.color}>{val.text}</Tag>
            </Option>
          ))}
        </Select>
      </Form.Item>
    </Form>
  );

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        {searchForm}
      </Card>

      <Card
        title="流程管理"
        extra={
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => openModal('create')}>
              新建
            </Button>
            <Button icon={<DeleteOutlined />} danger onClick={handleBatchDelete}>
              批量删除
            </Button>
            <Button icon={<CheckOutlined />} onClick={handleBatchEnable}>
              批量启用
            </Button>
            <Button icon={<CloseOutlined />} onClick={handleBatchDisable}>
              批量禁用
            </Button>
            <Button icon={<DownloadOutlined />} onClick={handleExport}>
              导出
            </Button>
            <Upload
              action="/signature/process/import"
              showUploadList={false}
              onChange={handleImport}
              accept=".xlsx,.xls"
            >
              <Button icon={<UploadOutlined />}>导入</Button>
            </Upload>
          </Space>
        }
      >
        <Table
          rowKey="id"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowSelection={rowSelection}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
          }}
          onChange={handleTableChange}
          scroll={{ x: 1600 }}
          bordered
          size="middle"
        />
      </Card>

      {/* 新建 / 编辑弹窗 */}
      <Modal
        title={{ create: '新建流程', edit: '编辑流程' }[modalType] || '流程'}
        open={modalVisible && modalType !== 'detail'}
        onOk={handleSubmit}
        onCancel={closeModal}
        width={640}
        destroyOnClose
      >
        {renderModalForm(modalForm)}
      </Modal>

      {/* 详情弹窗 */}
      <Modal
        title="流程详情"
        open={modalVisible && modalType === 'detail'}
        footer={[
          <Button key="close" onClick={closeModal}>
            关闭
          </Button>,
        ]}
        onCancel={closeModal}
        width={640}
        destroyOnClose
      >
        {renderModalForm(detailForm)}
      </Modal>
    </div>
  );
};

export default ProcessManage;

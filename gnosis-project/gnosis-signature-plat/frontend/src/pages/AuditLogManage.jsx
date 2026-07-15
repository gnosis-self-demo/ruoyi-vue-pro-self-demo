import React, { useState, useEffect } from 'react';
import {
  Table, Form, Button, Space, Modal, Tag, Popconfirm, message, Card,
  Input, Select, DatePicker, Upload, Row, Col,
} from 'antd';
import {
  PlusOutlined, EditOutlined, EyeOutlined, DeleteOutlined,
  UploadOutlined, DownloadOutlined, CheckCircleOutlined, StopOutlined,
} from '@ant-design/icons';
import { auditLogApi } from '../api/signatureApi';

const { Option } = Select;
const { TextArea } = Input;

// 操作结果映射: 0=失败, 1=成功
const OPERATION_RESULT_MAP = {
  0: { color: 'error', text: '失败' },
  1: { color: 'success', text: '成功' },
};

// 操作类型映射
const OPERATION_TYPE_MAP = {
  CREATE: { color: 'blue', text: '新增' },
  UPDATE: { color: 'orange', text: '修改' },
  DELETE: { color: 'red', text: '删除' },
  LOGIN: { color: 'green', text: '登录' },
  LOGOUT: { color: 'default', text: '登出' },
  EXPORT: { color: 'purple', text: '导出' },
  IMPORT: { color: 'cyan', text: '导入' },
};

// 状态映射
const STATUS_MAP = {
  0: { color: 'default', text: '禁用' },
  1: { color: 'success', text: '启用' },
};

const AuditLogManage = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [searchForm] = Form.useForm();
  const [modalForm] = Form.useForm();

  // 弹窗状态
  const [modalVisible, setModalVisible] = useState(false);
  const [modalType, setModalType] = useState('create'); // create / edit / detail
  const [modalData, setModalData] = useState(null);

  // 查询列表
  const fetchList = async (params = {}) => {
    setLoading(true);
    try {
      const response = await auditLogApi.pageList({
        pageNum,
        pageSize,
        query: params,
      });
      if (response.code === 200 && response.data) {
        setDataSource(response.data.list || []);
        setTotal(response.data.total || 0);
      }
    } catch (error) {
      console.error('查询失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchList();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageNum, pageSize]);

  // 搜索
  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    setPageNum(1);
    fetchList(values);
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields();
    setPageNum(1);
    fetchList();
  };

  // 打开弹窗
  const openModal = async (type, record = null) => {
    setModalType(type);
    setModalData(record);
    if (type === 'create') {
      modalForm.resetFields();
    } else if (record) {
      try {
        const response = await auditLogApi.detail(record.id);
        if (response.code === 200 && response.data) {
          modalForm.setFieldsValue(response.data);
        }
      } catch (error) {
        console.error('获取详情失败:', error);
      }
    }
    setModalVisible(true);
  };

  // 关闭弹窗
  const closeModal = () => {
    setModalVisible(false);
    setModalData(null);
    modalForm.resetFields();
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await modalForm.validateFields();
      if (modalType === 'create') {
        const response = await auditLogApi.create(values);
        if (response.code === 200) {
          message.success('创建成功');
          closeModal();
          fetchList();
        }
      } else if (modalType === 'edit') {
        const response = await auditLogApi.update({ id: modalData.id, ...values });
        if (response.code === 200) {
          message.success('更新成功');
          closeModal();
          fetchList();
        }
      }
    } catch (error) {
      console.error('提交失败:', error);
    }
  };

  // 单条删除
  const handleDelete = async (id) => {
    try {
      const response = await auditLogApi.batchDelete([id]);
      if (response.code === 200) {
        message.success('删除成功');
        fetchList();
      }
    } catch (error) {
      console.error('删除失败:', error);
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      const response = await auditLogApi.batchDelete(selectedRowKeys);
      if (response.code === 200) {
        message.success('批量删除成功');
        setSelectedRowKeys([]);
        fetchList();
      }
    } catch (error) {
      console.error('批量删除失败:', error);
    }
  };

  // 批量启用
  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的数据');
      return;
    }
    try {
      const response = await auditLogApi.batchEnable(selectedRowKeys);
      if (response.code === 200) {
        message.success('批量启用成功');
        setSelectedRowKeys([]);
        fetchList();
      }
    } catch (error) {
      console.error('批量启用失败:', error);
    }
  };

  // 批量禁用
  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的数据');
      return;
    }
    try {
      const response = await auditLogApi.batchDisable(selectedRowKeys);
      if (response.code === 200) {
        message.success('批量禁用成功');
        setSelectedRowKeys([]);
        fetchList();
      }
    } catch (error) {
      console.error('批量禁用失败:', error);
    }
  };

  // 导出
  const handleExport = async () => {
    try {
      const response = await auditLogApi.export(selectedRowKeys);
      const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'audit_log_export.xlsx';
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

  // 表格列定义
  const columns = [
    {
      title: '操作类型',
      dataIndex: 'operationType',
      key: 'operationType',
      width: 100,
      render: (type) => {
        const config = OPERATION_TYPE_MAP[type] || { color: 'default', text: type };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '操作模块',
      dataIndex: 'operationModule',
      key: 'operationModule',
      width: 120,
    },
    {
      title: '操作描述',
      dataIndex: 'operationDesc',
      key: 'operationDesc',
      width: 200,
      ellipsis: true,
    },
    {
      title: '操作IP',
      dataIndex: 'operationIp',
      key: 'operationIp',
      width: 140,
    },
    {
      title: '操作结果',
      dataIndex: 'operationResult',
      key: 'operationResult',
      width: 100,
      render: (result) => {
        const config = OPERATION_RESULT_MAP[result] !== undefined
          ? OPERATION_RESULT_MAP[result]
          : { color: 'default', text: result };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '错误信息',
      dataIndex: 'errorMsg',
      key: 'errorMsg',
      width: 180,
      ellipsis: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => {
        const config = STATUS_MAP[status] || { color: 'default', text: status };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
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
      width: 160,
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 180,
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
            title="确定要删除该数据吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
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

  // 表格行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  // 弹窗标题
  const getModalTitle = () => {
    const titles = { create: '新增审计日志', edit: '编辑审计日志', detail: '审计日志详情' };
    return titles[modalType] || '审计日志';
  };

  // 是否只读
  const isReadOnly = modalType === 'detail';

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Form form={searchForm} layout="inline">
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="operationType" label="操作类型">
                <Select placeholder="请选择操作类型" allowClear>
                  {Object.entries(OPERATION_TYPE_MAP).map(([key, val]) => (
                    <Option key={key} value={key}>{val.text}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="operationModule" label="操作模块">
                <Input placeholder="请输入操作模块" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="operationDesc" label="操作描述">
                <Input placeholder="请输入操作描述" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="operationIp" label="操作IP">
                <Input placeholder="请输入操作IP" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="operationResult" label="操作结果">
                <Select placeholder="请选择操作结果" allowClear>
                  <Option value={1}>成功</Option>
                  <Option value={0}>失败</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="errorMsg" label="错误信息">
                <Input placeholder="请输入错误信息" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="createUserId" label="创建人ID">
                <Input placeholder="请输入创建人ID" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item name="createTime" label="创建时间">
                <DatePicker style={{ width: '100%' }} placeholder="请选择创建时间" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={6}>
              <Form.Item>
                <Space>
                  <Button type="primary" onClick={handleSearch}>查询</Button>
                  <Button onClick={handleReset}>重置</Button>
                </Space>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Card>

      <Card>
        <Space style={{ marginBottom: 16 }} wrap>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => openModal('create')}>
            新增
          </Button>
          <Popconfirm
            title="确定要删除选中的数据吗？"
            onConfirm={handleBatchDelete}
            okText="确定"
            cancelText="取消"
          >
            <Button danger icon={<DeleteOutlined />} disabled={selectedRowKeys.length === 0}>
              批量删除
            </Button>
          </Popconfirm>
          <Button icon={<CheckCircleOutlined />} onClick={handleBatchEnable} disabled={selectedRowKeys.length === 0}>
            批量启用
          </Button>
          <Button icon={<StopOutlined />} onClick={handleBatchDisable} disabled={selectedRowKeys.length === 0}>
            批量禁用
          </Button>
          <Upload
            action="/signature/audit-log/import"
            showUploadList={false}
            onChange={handleImport}
            accept=".xlsx,.xls"
          >
            <Button icon={<UploadOutlined />}>导入</Button>
          </Upload>
          <Button icon={<DownloadOutlined />} onClick={handleExport}>
            导出
          </Button>
        </Space>

        <Table
          rowKey="id"
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowSelection={rowSelection}
          scroll={{ x: 1800 }}
          pagination={{
            current: pageNum,
            pageSize: pageSize,
            total: total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, size) => {
              setPageNum(page);
              setPageSize(size);
            },
          }}
        />
      </Card>

      <Modal
        title={getModalTitle()}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={closeModal}
        width={700}
        destroyOnClose
        footer={isReadOnly ? [
          <Button key="close" onClick={closeModal}>关闭</Button>,
        ] : [
          <Button key="cancel" onClick={closeModal}>取消</Button>,
          <Button key="submit" type="primary" onClick={handleSubmit}>确定</Button>,
        ]}
      >
        <Form form={modalForm} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="operationType"
                label="操作类型"
                rules={!isReadOnly ? [{ required: true, message: '请选择操作类型' }] : []}
              >
                <Select placeholder="请选择操作类型" disabled={isReadOnly}>
                  {Object.entries(OPERATION_TYPE_MAP).map(([key, val]) => (
                    <Option key={key} value={key}>{val.text}</Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="operationModule"
                label="操作模块"
                rules={!isReadOnly ? [{ required: true, message: '请输入操作模块' }] : []}
              >
                <Input placeholder="请输入操作模块" disabled={isReadOnly} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="operationIp" label="操作IP">
                <Input placeholder="请输入操作IP" disabled={isReadOnly} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="operationResult"
                label="操作结果"
                initialValue={1}
              >
                <Select disabled={isReadOnly}>
                  <Option value={1}>成功</Option>
                  <Option value={0}>失败</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="operationDesc"
            label="操作描述"
            rules={!isReadOnly ? [{ required: true, message: '请输入操作描述' }] : []}
          >
            <TextArea rows={3} placeholder="请输入操作描述" disabled={isReadOnly} />
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="status"
                label="状态"
                initialValue={1}
              >
                <Select disabled={isReadOnly}>
                  <Option value={1}>启用</Option>
                  <Option value={0}>禁用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="errorMsg" label="错误信息">
                <Input placeholder="请输入错误信息" disabled={isReadOnly} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="createUserId" label="创建人ID">
                <Input placeholder="创建人ID" disabled />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="createTime" label="创建时间">
                <DatePicker style={{ width: '100%' }} disabled />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
};

export default AuditLogManage;

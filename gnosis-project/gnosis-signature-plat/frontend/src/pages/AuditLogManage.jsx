import React, { useState, useEffect } from 'react';
import {
  Table, Form, Button, Space, Modal, Tag, Popconfirm, message, Card,
  Input, Select, DatePicker, Upload, Row, Col,
} from 'antd';
import {
  EyeOutlined, DeleteOutlined, DownloadOutlined,
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

const AuditLogManage = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [searchForm] = Form.useForm();

  // 详情弹窗状态
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailData, setDetailData] = useState(null);

  // 查询列表
  const fetchList = async (params = {}) => {
    setLoading(true);
    try {
      const response = await auditLogApi.pageList({
        pageNum,
        pageSize,
        ...params,
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

  // 查看详情
  const handleDetail = async (record) => {
    try {
      const response = await auditLogApi.detail(record.id);
      if (response.code === 200 && response.data) {
        setDetailData(response.data);
        setDetailVisible(true);
      }
    } catch (error) {
      console.error('获取详情失败:', error);
    }
  };

  // 关闭详情弹窗
  const closeDetail = () => {
    setDetailVisible(false);
    setDetailData(null);
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

  // 导出
  const handleExport = async () => {
    try {
      const response = await auditLogApi.export(selectedRowKeys);
      if (response.code === 200) {
        message.success('导出成功');
      }
    } catch (error) {
      console.error('导出失败:', error);
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
      width: 100,
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleDetail(record)}
          >
            详情
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

  // 表格行选择
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

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
          scroll={{ x: 1500 }}
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

      {/* 详情弹窗 */}
      <Modal
        title="审计日志详情"
        open={detailVisible}
        onCancel={closeDetail}
        width={700}
        destroyOnClose
        footer={[
          <Button key="close" onClick={closeDetail}>关闭</Button>,
        ]}
      >
        {detailData && (
          <Form layout="vertical">
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="操作类型">
                  <Tag color={OPERATION_TYPE_MAP[detailData.operationType]?.color || 'default'}>
                    {OPERATION_TYPE_MAP[detailData.operationType]?.text || detailData.operationType}
                  </Tag>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="操作模块">
                  <span>{detailData.operationModule || '-'}</span>
                </Form.Item>
              </Col>
            </Row>
            <Form.Item label="操作描述">
              <span>{detailData.operationDesc || '-'}</span>
            </Form.Item>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="操作IP">
                  <span>{detailData.operationIp || '-'}</span>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="操作结果">
                  <Tag color={OPERATION_RESULT_MAP[detailData.operationResult]?.color || 'default'}>
                    {OPERATION_RESULT_MAP[detailData.operationResult]?.text || detailData.operationResult}
                  </Tag>
                </Form.Item>
              </Col>
            </Row>
            <Form.Item label="错误信息">
              <span style={{ color: detailData.errorMsg ? '#ff4d4f' : 'inherit' }}>
                {detailData.errorMsg || '-'}
              </span>
            </Form.Item>
            <Row gutter={16}>
              <Col span={12}>
                <Form.Item label="创建人ID">
                  <span>{detailData.createUserId || '-'}</span>
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item label="创建时间">
                  <span>{detailData.createTime || '-'}</span>
                </Form.Item>
              </Col>
            </Row>
          </Form>
        )}
      </Modal>
    </div>
  );
};

export default AuditLogManage;

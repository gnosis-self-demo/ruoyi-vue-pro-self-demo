import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Tag, Drawer, Descriptions } from 'antd';
import { validationLogApi } from '../services/apiService';

const { Option } = Select;

const ValidationLogManagement = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailRecord, setDetailRecord] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [searchParams, setSearchParams] = useState({});

  const fetchLogs = async (page = 1, pageSize = 10, params = {}) => {
    setLoading(true);
    try {
      const result = await validationLogApi.getPage({
        page, pageSize,
        flowId: params.flowId || undefined,
        requestId: params.requestId || undefined,
        modeType: params.modeType || undefined,
        isActive: params.isActive !== undefined ? params.isActive : undefined
      });
      setLogs(result.data.records || []);
      setPagination({
        current: result.data.page,
        pageSize: result.data.pageSize,
        total: result.data.total
      });
    } catch (error) {
      message.error('获取校验日志列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const showDetail = (record) => {
    setDetailRecord(record);
    setDetailVisible(true);
  };

  const handleDelete = async (logId) => {
    try {
      await validationLogApi.delete(logId);
      message.success('删除成功');
      fetchLogs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的日志');
      return;
    }
    try {
      await validationLogApi.batchDelete(selectedRowKeys);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      fetchLogs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量删除失败');
    }
  };

  const handleBatchActivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的日志');
      return;
    }
    try {
      await validationLogApi.batchActivate(selectedRowKeys);
      message.success('批量启用成功');
      setSelectedRowKeys([]);
      fetchLogs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量启用失败');
    }
  };

  const handleBatchDeactivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的日志');
      return;
    }
    try {
      await validationLogApi.batchDeactivate(selectedRowKeys);
      message.success('批量禁用成功');
      setSelectedRowKeys([]);
      fetchLogs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleExport = async () => {
    try {
      const result = await validationLogApi.exportData(searchParams);
      const url = window.URL.createObjectURL(new Blob([result]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'validation_logs.json');
      document.body.appendChild(link);
      link.click();
      link.remove();
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    }
  };

  const handleSearch = (values) => {
    setSearchParams(values);
    fetchLogs(1, pagination.pageSize, values);
  };

  const handleTableChange = (pag) => {
    fetchLogs(pag.current, pag.pageSize, searchParams);
  };

  const columns = [
    { title: '日志ID', dataIndex: 'logId', key: 'logId', width: 80 },
    { title: '流程ID', dataIndex: 'flowId', key: 'flowId', width: 160 },
    { title: '请求ID', dataIndex: 'requestId', key: 'requestId', width: 120 },
    {
      title: '校验模式', dataIndex: 'modeType', key: 'modeType', width: 100,
      render: (mode) => {
        const modeMap = { 'FLOW': '流程模式', 'HANDLER': '处理器模式', 'HYBRID': '混合模式', 'SYSTEM': '系统' };
        return <Tag>{modeMap[mode] || mode}</Tag>;
      }
    },
    {
      title: '错误信息', dataIndex: 'errorMsg', key: 'errorMsg', width: 200, ellipsis: true,
      render: (val) => val || '-'
    },
    {
      title: '状态', dataIndex: 'isActive', key: 'isActive', width: 80,
      render: (val) => <Tag color={val ? 'green' : 'red'}>{val ? '启用' : '禁用'}</Tag>
    },
    { title: '创建人ID', dataIndex: 'createUserId', key: 'createUserId', width: 100 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
    { title: '更新人ID', dataIndex: 'updateUserId', key: 'updateUserId', width: 100 },
    { title: '更新时间', dataIndex: 'updateTime', key: 'updateTime', width: 160 },
    {
      title: '操作', key: 'action', width: 150, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => showDetail(record)}>详情</Button>
          <Button type="link" size="small" danger onClick={() => handleDelete(record.logId)}>删除</Button>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (keys) => setSelectedRowKeys(keys),
  };

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>校验日志管理</h2>
        <Space>
          <Button danger onClick={handleBatchDelete}>批量删除</Button>
          <Button onClick={handleBatchActivate}>批量启用</Button>
          <Button onClick={handleBatchDeactivate}>批量禁用</Button>
          <Button onClick={handleExport}>导出</Button>
        </Space>
      </div>

      <Form layout="inline" style={{ marginBottom: 16 }} onFinish={handleSearch}>
        <Form.Item name="flowId"><Input placeholder="流程ID" allowClear /></Form.Item>
        <Form.Item name="requestId"><Input placeholder="请求ID" allowClear /></Form.Item>
        <Form.Item name="modeType">
          <Select placeholder="校验模式" allowClear style={{ width: 120 }}>
            <Option value="FLOW">流程模式</Option>
            <Option value="HANDLER">处理器模式</Option>
            <Option value="HYBRID">混合模式</Option>
          </Select>
        </Form.Item>
        <Form.Item name="isActive">
          <Select placeholder="状态" allowClear style={{ width: 100 }}>
            <Option value={true}>启用</Option>
            <Option value={false}>禁用</Option>
          </Select>
        </Form.Item>
        <Form.Item><Button type="primary" htmlType="submit">查询</Button></Form.Item>
        <Form.Item><Button onClick={() => { setSearchParams({}); fetchLogs(1, pagination.pageSize, {}); }}>重置</Button></Form.Item>
      </Form>

      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={logs}
        loading={loading}
        rowKey="logId"
        scroll={{ x: 1400 }}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`
        }}
        onChange={handleTableChange}
      />

      <Drawer
        title="校验日志详情"
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
        width={600}
      >
        {detailRecord && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="日志ID">{detailRecord.logId}</Descriptions.Item>
            <Descriptions.Item label="流程ID">{detailRecord.flowId}</Descriptions.Item>
            <Descriptions.Item label="请求ID">{detailRecord.requestId}</Descriptions.Item>
            <Descriptions.Item label="校验模式">{detailRecord.modeType}</Descriptions.Item>
            <Descriptions.Item label="失败节点">{detailRecord.failedNode || '-'}</Descriptions.Item>
            <Descriptions.Item label="错误信息">{detailRecord.errorMsg || '-'}</Descriptions.Item>
            <Descriptions.Item label="状态">{detailRecord.isActive ? '启用' : '禁用'}</Descriptions.Item>
            <Descriptions.Item label="入参快照">
              <pre style={{ maxHeight: 300, overflow: 'auto', fontSize: 12 }}>
                {detailRecord.inputSnapshot
                  ? (() => { try { return JSON.stringify(JSON.parse(detailRecord.inputSnapshot), null, 2); } catch { return detailRecord.inputSnapshot; } })()
                  : '-'}
              </pre>
            </Descriptions.Item>
            <Descriptions.Item label="创建人ID">{detailRecord.createUserId}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{detailRecord.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新人ID">{detailRecord.updateUserId}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{detailRecord.updateTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </div>
  );
};

export default ValidationLogManagement;

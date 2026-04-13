import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Tag, Drawer, Descriptions, Upload } from 'antd';
import { flowConfigApi, businessTypeApi } from '../services/apiService';

const { Option } = Select;
const { TextArea } = Input;

const FlowConfigManagement = () => {
  const [flowConfigs, setFlowConfigs] = useState([]);
  const [businessTypes, setBusinessTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailRecord, setDetailRecord] = useState(null);
  const [form] = Form.useForm();
  const [editingFlowId, setEditingFlowId] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [searchParams, setSearchParams] = useState({});

  const fetchFlowConfigs = async (page = 1, pageSize = 10, params = {}) => {
    setLoading(true);
    try {
      const result = await flowConfigApi.getPage({
        page, pageSize,
        flowId: params.flowId || undefined,
        flowName: params.flowName || undefined,
        businessType: params.businessType || undefined,
        modeType: params.modeType || undefined,
        isActive: params.isActive !== undefined ? params.isActive : undefined
      });
      setFlowConfigs(result.data.records || []);
      setPagination({
        current: result.data.page,
        pageSize: result.data.pageSize,
        total: result.data.total
      });
    } catch (error) {
      message.error('获取流程配置列表失败');
    } finally {
      setLoading(false);
    }
  };

  const fetchBusinessTypes = async () => {
    try {
      const result = await businessTypeApi.getActiveList();
      setBusinessTypes(result.data || []);
    } catch (error) {
      console.error('Error fetching business types:', error);
    }
  };

  useEffect(() => {
    fetchFlowConfigs();
    fetchBusinessTypes();
  }, []);

  const openModal = (record = null) => {
    if (record) {
      setEditingFlowId(record.flowId);
      form.setFieldsValue({
        ...record,
        isActive: record.isActive
      });
    } else {
      setEditingFlowId(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingFlowId(null);
  };

  const showDetail = (record) => {
    setDetailRecord(record);
    setDetailVisible(true);
  };

  const handleSubmit = async (values) => {
    try {
      const data = {
        ...values,
        isActive: values.isActive !== undefined ? values.isActive : true,
        createUserId: 'admin',
        updateUserId: 'admin'
      };
      if (editingFlowId) {
        data.flowId = editingFlowId;
        await flowConfigApi.update(data);
        message.success('更新成功');
      } else {
        await flowConfigApi.create(data);
        message.success('新增成功');
      }
      closeModal();
      fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error(editingFlowId ? '更新失败' : '新增失败');
    }
  };

  const handleDelete = async (flowId) => {
    try {
      await flowConfigApi.delete(flowId);
      message.success('删除成功');
      fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchDelete(selectedRowKeys);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量删除失败');
    }
  };

  const handleBatchActivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchActivate(selectedRowKeys);
      message.success('批量启用成功');
      setSelectedRowKeys([]);
      fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量启用失败');
    }
  };

  const handleBatchDeactivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchDeactivate(selectedRowKeys);
      message.success('批量禁用成功');
      setSelectedRowKeys([]);
      fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleExport = async () => {
    try {
      const result = await flowConfigApi.exportData(searchParams);
      const url = window.URL.createObjectURL(new Blob([result]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'validation_flows.json');
      document.body.appendChild(link);
      link.click();
      link.remove();
      message.success('导出成功');
    } catch (error) {
      message.error('导出失败');
    }
  };

  const handleImport = (file) => {
    const reader = new FileReader();
    reader.onload = async (e) => {
      try {
        const data = JSON.parse(e.target.result);
        await flowConfigApi.importData(data);
        message.success('导入成功');
        fetchFlowConfigs(pagination.current, pagination.pageSize, searchParams);
      } catch (error) {
        message.error('导入失败');
      }
    };
    reader.readAsText(file);
    return false;
  };

  const handleSearch = (values) => {
    setSearchParams(values);
    fetchFlowConfigs(1, pagination.pageSize, values);
  };

  const handleTableChange = (pag) => {
    fetchFlowConfigs(pag.current, pag.pageSize, searchParams);
  };

  const columns = [
    { title: '流程ID', dataIndex: 'flowId', key: 'flowId', width: 160 },
    { title: '流程名称', dataIndex: 'flowName', key: 'flowName', width: 180 },
    {
      title: '业务类型', dataIndex: 'businessType', key: 'businessType', width: 120,
      render: (val) => val || '-'
    },
    {
      title: '校验模式', dataIndex: 'modeType', key: 'modeType', width: 100,
      render: (mode) => {
        const modeMap = { 'FLOW': '流程模式', 'HANDLER': '处理器模式', 'HYBRID': '混合模式' };
        const colorMap = { 'FLOW': 'blue', 'HANDLER': 'green', 'HYBRID': 'orange' };
        return <Tag color={colorMap[mode]}>{modeMap[mode] || mode}</Tag>;
      }
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
      title: '操作', key: 'action', width: 200, fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => showDetail(record)}>详情</Button>
          <Button type="link" size="small" onClick={() => openModal(record)}>编辑</Button>
          <Button type="link" size="small" danger onClick={() => handleDelete(record.flowId)}>删除</Button>
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
        <h2>流程配置管理</h2>
        <Space>
          <Button type="primary" onClick={() => openModal()}>新增</Button>
          <Button danger onClick={handleBatchDelete}>批量删除</Button>
          <Button onClick={handleBatchActivate}>批量启用</Button>
          <Button onClick={handleBatchDeactivate}>批量禁用</Button>
          <Button onClick={handleExport}>导出</Button>
          <Upload beforeUpload={handleImport} showUploadList={false} accept=".json">
            <Button>导入</Button>
          </Upload>
        </Space>
      </div>

      <Form layout="inline" style={{ marginBottom: 16 }} onFinish={handleSearch}>
        <Form.Item name="flowId"><Input placeholder="流程ID" allowClear /></Form.Item>
        <Form.Item name="flowName"><Input placeholder="流程名称" allowClear /></Form.Item>
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
        <Form.Item><Button onClick={() => { setSearchParams({}); fetchFlowConfigs(1, pagination.pageSize, {}); }}>重置</Button></Form.Item>
      </Form>

      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={flowConfigs}
        loading={loading}
        rowKey="flowId"
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

      <Modal
        title={editingFlowId ? '编辑流程配置' : '新增流程配置'}
        open={modalVisible}
        onCancel={closeModal}
        onOk={() => form.submit()}
        width={640}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item name="flowId" label="流程ID" rules={[{ required: true, message: '请输入流程ID' }]}>
            <Input placeholder="请输入流程ID" disabled={!!editingFlowId} />
          </Form.Item>
          <Form.Item name="flowName" label="流程名称" rules={[{ required: true, message: '请输入流程名称' }]}>
            <Input placeholder="请输入流程名称" />
          </Form.Item>
          <Form.Item name="businessType" label="业务类型">
            <Select placeholder="请选择业务类型" allowClear>
              {businessTypes.map(bt => <Option key={bt.code} value={bt.code}>{bt.name}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="modeType" label="校验模式" rules={[{ required: true, message: '请选择校验模式' }]}>
            <Select placeholder="请选择校验模式">
              <Option value="FLOW">流程模式</Option>
              <Option value="HANDLER">处理器模式</Option>
              <Option value="HYBRID">混合模式</Option>
            </Select>
          </Form.Item>
          <Form.Item name="elExpression" label="LiteFlow EL表达式">
            <TextArea rows={3} placeholder="FLOW/HYBRID模式必填，如: THEN(parse_param, check_format)" />
          </Form.Item>
          <Form.Item name="handlerCode" label="处理器编码">
            <Input placeholder="HANDLER/HYBRID模式必填，如: OrderBusinessHandler" />
          </Form.Item>
          <Form.Item name="componentConfig" label="组件配置(JSON)">
            <TextArea rows={6} placeholder='请输入组件配置JSON' />
          </Form.Item>
          <Form.Item name="isActive" label="状态" valuePropName="checked">
            <Select placeholder="请选择状态">
              <Option value={true}>启用</Option>
              <Option value={false}>禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="流程配置详情"
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
        width={600}
      >
        {detailRecord && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="流程ID">{detailRecord.flowId}</Descriptions.Item>
            <Descriptions.Item label="流程名称">{detailRecord.flowName}</Descriptions.Item>
            <Descriptions.Item label="业务类型">{detailRecord.businessType}</Descriptions.Item>
            <Descriptions.Item label="校验模式">{detailRecord.modeType}</Descriptions.Item>
            <Descriptions.Item label="EL表达式">{detailRecord.elExpression}</Descriptions.Item>
            <Descriptions.Item label="处理器编码">{detailRecord.handlerCode}</Descriptions.Item>
            <Descriptions.Item label="状态">{detailRecord.isActive ? '启用' : '禁用'}</Descriptions.Item>
            <Descriptions.Item label="版本">{detailRecord.version}</Descriptions.Item>
            <Descriptions.Item label="组件配置">
              <pre style={{ maxHeight: 300, overflow: 'auto', fontSize: 12 }}>
                {detailRecord.componentConfig ? JSON.stringify(JSON.parse(detailRecord.componentConfig), null, 2) : '-'}
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

export default FlowConfigManagement;

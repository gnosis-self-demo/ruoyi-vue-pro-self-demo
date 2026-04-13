import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Tag, Drawer, Descriptions, Upload } from 'antd';
import { businessTypeApi } from '../services/apiService';

const { Option } = Select;

const BusinessTypeManagement = () => {
  const [businessTypes, setBusinessTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailRecord, setDetailRecord] = useState(null);
  const [form] = Form.useForm();
  const [editingCode, setEditingCode] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [searchParams, setSearchParams] = useState({});

  const fetchBusinessTypes = async (page = 1, pageSize = 10, params = {}) => {
    setLoading(true);
    try {
      const result = await businessTypeApi.getPage({
        page, pageSize,
        code: params.code || undefined,
        name: params.name || undefined,
        isActive: params.isActive !== undefined ? params.isActive : undefined
      });
      setBusinessTypes(result.data.records || []);
      setPagination({
        current: result.data.page,
        pageSize: result.data.pageSize,
        total: result.data.total
      });
    } catch (error) {
      message.error('获取业务类型列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBusinessTypes();
  }, []);

  const openModal = (record = null) => {
    if (record) {
      setEditingCode(record.code);
      form.setFieldsValue(record);
    } else {
      setEditingCode(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingCode(null);
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
      if (editingCode) {
        data.code = editingCode;
        await businessTypeApi.update(data);
        message.success('更新成功');
      } else {
        await businessTypeApi.create(data);
        message.success('新增成功');
      }
      closeModal();
      fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error(editingCode ? '更新失败' : '新增失败');
    }
  };

  const handleDelete = async (code) => {
    try {
      await businessTypeApi.delete(code);
      message.success('删除成功');
      fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的业务类型');
      return;
    }
    try {
      await businessTypeApi.batchDelete(selectedRowKeys);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量删除失败');
    }
  };

  const handleBatchActivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的业务类型');
      return;
    }
    try {
      await businessTypeApi.batchActivate(selectedRowKeys);
      message.success('批量启用成功');
      setSelectedRowKeys([]);
      fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量启用失败');
    }
  };

  const handleBatchDeactivate = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的业务类型');
      return;
    }
    try {
      await businessTypeApi.batchDeactivate(selectedRowKeys);
      message.success('批量禁用成功');
      setSelectedRowKeys([]);
      fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
    } catch (error) {
      message.error('批量禁用失败');
    }
  };

  const handleExport = async () => {
    try {
      const result = await businessTypeApi.exportData();
      const url = window.URL.createObjectURL(new Blob([result]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'business_types.json');
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
        await businessTypeApi.importData(data);
        message.success('导入成功');
        fetchBusinessTypes(pagination.current, pagination.pageSize, searchParams);
      } catch (error) {
        message.error('导入失败');
      }
    };
    reader.readAsText(file);
    return false;
  };

  const handleSearch = (values) => {
    setSearchParams(values);
    fetchBusinessTypes(1, pagination.pageSize, values);
  };

  const handleTableChange = (pag) => {
    fetchBusinessTypes(pag.current, pag.pageSize, searchParams);
  };

  const columns = [
    { title: '编码', dataIndex: 'code', key: 'code', width: 140 },
    { title: '名称', dataIndex: 'name', key: 'name', width: 160 },
    { title: '描述', dataIndex: 'description', key: 'description', width: 200, ellipsis: true },
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
          <Button type="link" size="small" danger onClick={() => handleDelete(record.code)}>删除</Button>
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
        <h2>业务类型管理</h2>
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
        <Form.Item name="code"><Input placeholder="编码" allowClear /></Form.Item>
        <Form.Item name="name"><Input placeholder="名称" allowClear /></Form.Item>
        <Form.Item name="isActive">
          <Select placeholder="状态" allowClear style={{ width: 100 }}>
            <Option value={true}>启用</Option>
            <Option value={false}>禁用</Option>
          </Select>
        </Form.Item>
        <Form.Item><Button type="primary" htmlType="submit">查询</Button></Form.Item>
        <Form.Item><Button onClick={() => { setSearchParams({}); fetchBusinessTypes(1, pagination.pageSize, {}); }}>重置</Button></Form.Item>
      </Form>

      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={businessTypes}
        loading={loading}
        rowKey="code"
        scroll={{ x: 1200 }}
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
        title={editingCode ? '编辑业务类型' : '新增业务类型'}
        open={modalVisible}
        onCancel={closeModal}
        onOk={() => form.submit()}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item name="code" label="编码" rules={[{ required: true, message: '请输入编码' }]}>
            <Input placeholder="请输入编码" disabled={!!editingCode} />
          </Form.Item>
          <Form.Item name="name" label="名称" rules={[{ required: true, message: '请输入名称' }]}>
            <Input placeholder="请输入名称" />
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>
          <Form.Item name="isActive" label="状态">
            <Select placeholder="请选择状态">
              <Option value={true}>启用</Option>
              <Option value={false}>禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title="业务类型详情"
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
        width={500}
      >
        {detailRecord && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="编码">{detailRecord.code}</Descriptions.Item>
            <Descriptions.Item label="名称">{detailRecord.name}</Descriptions.Item>
            <Descriptions.Item label="描述">{detailRecord.description}</Descriptions.Item>
            <Descriptions.Item label="状态">{detailRecord.isActive ? '启用' : '禁用'}</Descriptions.Item>
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

export default BusinessTypeManagement;

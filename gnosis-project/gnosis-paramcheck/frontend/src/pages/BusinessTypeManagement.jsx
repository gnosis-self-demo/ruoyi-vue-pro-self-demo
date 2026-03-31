import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Space } from 'antd';
import { businessTypeApi } from '../services/apiService';

const BusinessTypeManagement = () => {
  const [businessTypes, setBusinessTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingId, setEditingId] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  // 获取业务类型列表
  const fetchBusinessTypes = async () => {
    setLoading(true);
    try {
      const response = await businessTypeApi.getBusinessTypes();
      setBusinessTypes(response.data);
    } catch (error) {
      message.error('获取业务类型列表失败');
      console.error('Error fetching business types:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBusinessTypes();
  }, []);

  // 打开新增/编辑弹窗
  const openModal = (record = null) => {
    if (record) {
      setEditingId(record.id);
      form.setFieldsValue(record);
    } else {
      setEditingId(null);
      form.resetFields();
    }
    setModalVisible(true);
  };

  // 关闭弹窗
  const closeModal = () => {
    setModalVisible(false);
    form.resetFields();
    setEditingId(null);
  };

  // 提交表单
  const handleSubmit = async (values) => {
    try {
      if (editingId) {
        await businessTypeApi.updateBusinessType(editingId, values);
        message.success('更新成功');
      } else {
        await businessTypeApi.createBusinessType(values);
        message.success('新增成功');
      }
      closeModal();
      fetchBusinessTypes();
    } catch (error) {
      message.error(editingId ? '更新失败' : '新增失败');
      console.error('Error submitting form:', error);
    }
  };

  // 删除业务类型
  const handleDelete = async (id) => {
    try {
      await businessTypeApi.deleteBusinessType(id);
      message.success('删除成功');
      fetchBusinessTypes();
    } catch (error) {
      message.error('删除失败');
      console.error('Error deleting business type:', error);
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的业务类型');
      return;
    }
    try {
      await businessTypeApi.batchDeleteBusinessTypes(selectedRowKeys);
      message.success('批量删除成功');
      fetchBusinessTypes();
      setSelectedRowKeys([]);
    } catch (error) {
      message.error('批量删除失败');
      console.error('Error batch deleting business types:', error);
    }
  };

  // 表格列配置
  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
    },
    {
      title: '业务类型名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '业务类型编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '创建人ID',
      dataIndex: 'createUserId',
      key: 'createUserId',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
    },
    {
      title: '更新人ID',
      dataIndex: 'updateUserId',
      key: 'updateUserId',
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" onClick={() => openModal(record)}>编辑</Button>
          <Button type="link" danger onClick={() => handleDelete(record.id)}>删除</Button>
        </Space>
      ),
    },
  ];

  // 表格选择配置
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
        </Space>
      </div>
      
      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={businessTypes}
        loading={loading}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingId ? '编辑业务类型' : '新增业务类型'}
        open={modalVisible}
        onCancel={closeModal}
        onOk={() => form.submit()}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item
            name="name"
            label="业务类型名称"
            rules={[{ required: true, message: '请输入业务类型名称' }]}
          >
            <Input placeholder="请输入业务类型名称" />
          </Form.Item>
          <Form.Item
            name="code"
            label="业务类型编码"
            rules={[{ required: true, message: '请输入业务类型编码' }]}
          >
            <Input placeholder="请输入业务类型编码" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input.TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default BusinessTypeManagement;
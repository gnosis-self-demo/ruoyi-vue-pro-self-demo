import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Switch } from 'antd';
import { flowConfigApi } from '../services/apiService';

const { Option } = Select;

const FlowConfigManagement = () => {
  const [flowConfigs, setFlowConfigs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingId, setEditingId] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  // 获取流程配置列表
  const fetchFlowConfigs = async () => {
    setLoading(true);
    try {
      const response = await flowConfigApi.getFlowConfigs();
      setFlowConfigs(response.data);
    } catch (error) {
      message.error('获取流程配置列表失败');
      console.error('Error fetching flow configs:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFlowConfigs();
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
        await flowConfigApi.updateFlowConfig(editingId, values);
        message.success('更新成功');
      } else {
        await flowConfigApi.createFlowConfig(values);
        message.success('新增成功');
      }
      closeModal();
      fetchFlowConfigs();
    } catch (error) {
      message.error(editingId ? '更新失败' : '新增失败');
      console.error('Error submitting form:', error);
    }
  };

  // 删除流程配置
  const handleDelete = async (id) => {
    try {
      await flowConfigApi.deleteFlowConfig(id);
      message.success('删除成功');
      fetchFlowConfigs();
    } catch (error) {
      message.error('删除失败');
      console.error('Error deleting flow config:', error);
    }
  };

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchDeleteFlowConfigs(selectedRowKeys);
      message.success('批量删除成功');
      fetchFlowConfigs();
      setSelectedRowKeys([]);
    } catch (error) {
      message.error('批量删除失败');
      console.error('Error batch deleting flow configs:', error);
    }
  };

  // 批量启用
  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchEnableFlowConfigs(selectedRowKeys);
      message.success('批量启用成功');
      fetchFlowConfigs();
      setSelectedRowKeys([]);
    } catch (error) {
      message.error('批量启用失败');
      console.error('Error batch enabling flow configs:', error);
    }
  };

  // 批量禁用
  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的流程配置');
      return;
    }
    try {
      await flowConfigApi.batchDisableFlowConfigs(selectedRowKeys);
      message.success('批量禁用成功');
      fetchFlowConfigs();
      setSelectedRowKeys([]);
    } catch (error) {
      message.error('批量禁用失败');
      console.error('Error batch disabling flow configs:', error);
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
      title: '流程名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '流程编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '业务类型',
      dataIndex: 'businessType',
      key: 'businessType',
    },
    {
      title: '校验模式',
      dataIndex: 'validationMode',
      key: 'validationMode',
      render: (mode) => {
        const modeMap = {
          'FLOW': '流程模式',
          'HANDLER': '处理器模式',
          'HYBRID': '混合模式'
        };
        return modeMap[mode] || mode;
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => {
        return status === 1 ? '启用' : '禁用';
      }
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
        <h2>流程配置管理</h2>
        <Space>
          <Button type="primary" onClick={() => openModal()}>新增</Button>
          <Button danger onClick={handleBatchDelete}>批量删除</Button>
          <Button onClick={handleBatchEnable}>批量启用</Button>
          <Button onClick={handleBatchDisable}>批量禁用</Button>
        </Space>
      </div>
      
      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={flowConfigs}
        loading={loading}
        rowKey="id"
        pagination={{ pageSize: 10 }}
      />

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editingId ? '编辑流程配置' : '新增流程配置'}
        open={modalVisible}
        onCancel={closeModal}
        onOk={() => form.submit()}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item
            name="name"
            label="流程名称"
            rules={[{ required: true, message: '请输入流程名称' }]}
          >
            <Input placeholder="请输入流程名称" />
          </Form.Item>
          <Form.Item
            name="code"
            label="流程编码"
            rules={[{ required: true, message: '请输入流程编码' }]}
          >
            <Input placeholder="请输入流程编码" />
          </Form.Item>
          <Form.Item
            name="businessType"
            label="业务类型"
            rules={[{ required: true, message: '请选择业务类型' }]}
          >
            <Input placeholder="请输入业务类型" />
          </Form.Item>
          <Form.Item
            name="validationMode"
            label="校验模式"
            rules={[{ required: true, message: '请选择校验模式' }]}
          >
            <Select placeholder="请选择校验模式">
              <Option value="FLOW">流程模式</Option>
              <Option value="HANDLER">处理器模式</Option>
              <Option value="HYBRID">混合模式</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="configContent"
            label="配置内容"
            rules={[{ required: true, message: '请输入配置内容' }]}
          >
            <Input.TextArea rows={6} placeholder="请输入配置内容" />
          </Form.Item>
          <Form.Item
            name="status"
            label="状态"
            valuePropName="checked"
          >
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default FlowConfigManagement;
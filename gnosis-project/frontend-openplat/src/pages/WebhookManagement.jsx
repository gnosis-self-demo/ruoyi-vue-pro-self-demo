import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, InputNumber, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined } from '@ant-design/icons'
import {
  getWebhookConfigList,
  saveWebhookConfig,
  updateWebhookConfig,
  deleteWebhookConfig,
  batchDeleteWebhookConfigs,
  batchEnableWebhookConfigs,
  batchDisableWebhookConfigs
} from '../services/webhookService'

const { Option } = Select

const WebhookManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [form] = Form.useForm()
  const [searchForm, setSearchForm] = useState({})

  useEffect(() => {
    fetchData()
  }, [searchForm])

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getWebhookConfigList(searchForm)
      setData(res.data || [])
    } catch (error) {
      message.error('获取数据失败')
    }
    setLoading(false)
  }

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await deleteWebhookConfig(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingRecord) {
        await updateWebhookConfig({ ...editingRecord, ...values })
        message.success('修改成功')
      } else {
        await saveWebhookConfig(values)
        message.success('新增成功')
      }
      setModalVisible(false)
      fetchData()
    } catch (error) {
      message.error('操作失败')
    }
  }

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择数据')
      return
    }
    try {
      await batchDeleteWebhookConfigs(selectedRowKeys)
      message.success('批量删除成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量删除失败')
    }
  }

  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择数据')
      return
    }
    try {
      await batchEnableWebhookConfigs(selectedRowKeys)
      message.success('批量启用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量启用失败')
    }
  }

  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择数据')
      return
    }
    try {
      await batchDisableWebhookConfigs(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量禁用失败')
    }
  }

  const columns = [
    {
      title: 'Webhook 名称',
      dataIndex: 'webhookName',
      key: 'webhookName'
    },
    {
      title: '事件类型',
      dataIndex: 'eventType',
      key: 'eventType'
    },
    {
      title: '回调地址',
      dataIndex: 'callbackUrl',
      key: 'callbackUrl',
      ellipsis: true
    },
    {
      title: '重试次数',
      dataIndex: 'retryTimes',
      key: 'retryTimes'
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status) => status === 'ENABLED' ? '启用' : '禁用'
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true
    },
    {
      title: '创建人 ID',
      dataIndex: 'createUserId',
      key: 'createUserId'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>编辑</Button>
          <Popconfirm title="确定删除吗？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  const rowSelection = {
    selectedRowKeys,
    onChange: setSelectedRowKeys
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="请输入 Webhook 名称"
          onChange={(e) => setSearchForm({ ...searchForm, webhookName: e.target.value })}
          allowClear
          style={{ width: 200 }}
        />
        <Input
          placeholder="请输入事件类型"
          onChange={(e) => setSearchForm({ ...searchForm, eventType: e.target.value })}
          allowClear
          style={{ width: 200 }}
        />
        <Button type="primary" onClick={fetchData}>查询</Button>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>新增</Button>
        <Button danger icon={<DeleteOutlined />} onClick={handleBatchDelete} disabled={selectedRowKeys.length === 0}>批量删除</Button>
        <Button icon={<CheckCircleOutlined />} onClick={handleBatchEnable} disabled={selectedRowKeys.length === 0}>批量启用</Button>
        <Button icon={<StopOutlined />} onClick={handleBatchDisable} disabled={selectedRowKeys.length === 0}>批量禁用</Button>
      </Space>

      <Table
        loading={loading}
        columns={columns}
        dataSource={data}
        rowKey="id"
        rowSelection={rowSelection}
        pagination={{ pageSize: 10 }}
        scroll={{ x: 1500 }}
      />

      <Modal
        title={editingRecord ? '编辑 Webhook 配置' : '新增 Webhook 配置'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="webhookName" label="Webhook 名称" rules={[{ required: true, message: '请输入 Webhook 名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="eventType" label="事件类型" rules={[{ required: true, message: '请输入事件类型' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="callbackUrl" label="回调地址" rules={[{ required: true, message: '请输入回调地址' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="secret" label="签名密钥">
            <Input.Password />
          </Form.Item>
          <Form.Item name="retryTimes" label="重试次数">
            <InputNumber min={0} max={10} />
          </Form.Item>
          <Form.Item name="retryInterval" label="重试间隔（秒）">
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="timeout" label="超时时间（秒）">
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="status" label="状态">
            <Select>
              <Option value="ENABLED">启用</Option>
              <Option value="DISABLED">禁用</Option>
            </Select>
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea rows={4} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default WebhookManagement

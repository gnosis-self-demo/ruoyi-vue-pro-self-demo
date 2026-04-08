import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined } from '@ant-design/icons'
import {
  getSystemList,
  saveSystem,
  updateSystem,
  deleteSystem,
  batchDeleteSystems,
  batchEnableSystems,
  batchDisableSystems,
  getSystemApis
} from '../services/systemService'

const { Option } = Select

const SystemManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [apiModalVisible, setApiModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [selectedSystemId, setSelectedSystemId] = useState(null)
  const [systemApis, setSystemApis] = useState([])
  const [apiLoading, setApiLoading] = useState(false)
  const [form] = Form.useForm()
  const [searchForm, setSearchForm] = useState({})

  useEffect(() => {
    fetchData()
  }, [searchForm])

  const fetchData = async () => {
    setLoading(true)
    try {
      const result = await getSystemList(searchForm)
      setData(result || [])
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
      await deleteSystem(id)
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
        await updateSystem({ ...editingRecord, ...values })
        message.success('修改成功')
      } else {
        await saveSystem(values)
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
      await batchDeleteSystems(selectedRowKeys)
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
      await batchEnableSystems(selectedRowKeys)
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
      await batchDisableSystems(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量禁用失败')
    }
  }
  
  const handleViewApis = async (systemId) => {
    setSelectedSystemId(systemId)
    setApiLoading(true)
    try {
      const result = await getSystemApis(systemId)
      setSystemApis(result || [])
      setApiModalVisible(true)
    } catch (error) {
      message.error('获取 API 列表失败')
    }
    setApiLoading(false)
  }

  const columns = [
    {
      title: '系统编码',
      dataIndex: 'systemCode',
      key: 'systemCode'
    },
    {
      title: '系统名称',
      dataIndex: 'systemName',
      key: 'systemName'
    },
    {
      title: '系统类型',
      dataIndex: 'systemType',
      key: 'systemType'
    },
    {
      title: 'App ID',
      dataIndex: 'appId',
      key: 'appId'
    },
    {
      title: '负责人',
      dataIndex: 'principal',
      key: 'principal'
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
      key: 'description'
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
          <Button type="link" onClick={() => handleViewApis(record.id)}>查看 API</Button>
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
          placeholder="请输入系统编码"
          onChange={(e) => setSearchForm({ ...searchForm, systemCode: e.target.value })}
          allowClear
          style={{ width: 200 }}
        />
        <Input
          placeholder="请输入系统名称"
          onChange={(e) => setSearchForm({ ...searchForm, systemName: e.target.value })}
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
      />

      <Modal
        title={editingRecord ? '编辑系统' : '新增系统'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="systemCode" label="系统编码" rules={[{ required: true, message: '请输入系统编码' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="systemName" label="系统名称" rules={[{ required: true, message: '请输入系统名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="systemType" label="系统类型" rules={[{ required: true, message: '请选择系统类型' }]}>
            <Select>
              <Option value="INTERNAL">内部系统</Option>
              <Option value="EXTERNAL">外部系统</Option>
            </Select>
          </Form.Item>
          <Form.Item name="appId" label="App ID">
            <Input />
          </Form.Item>
          <Form.Item name="appSecret" label="App Secret">
            <Input.Password />
          </Form.Item>
          <Form.Item name="principal" label="负责人">
            <Input />
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

      <Modal
        title="系统关联 API 列表"
        open={apiModalVisible}
        onCancel={() => setApiModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setApiModalVisible(false)}>关闭</Button>
        ]}
        width={800}
      >
        <Table
          loading={apiLoading}
          columns={[
            {
              title: 'API 编码',
              dataIndex: 'apiCode',
              key: 'apiCode'
            },
            {
              title: 'API 名称',
              dataIndex: 'apiName',
              key: 'apiName'
            },
            {
              title: 'API 路径',
              dataIndex: 'apiPath',
              key: 'apiPath'
            },
            {
              title: '请求方法',
              dataIndex: 'apiMethod',
              key: 'apiMethod'
            },
            {
              title: '需要认证',
              dataIndex: 'needAuth',
              key: 'needAuth',
              render: (val) => val ? '是' : '否'
            },
            {
              title: '防重放',
              dataIndex: 'needAntiReplay',
              key: 'needAntiReplay',
              render: (val) => val ? '是' : '否'
            },
            {
              title: '限流阈值',
              dataIndex: 'rateLimit',
              key: 'rateLimit'
            },
            {
              title: '状态',
              dataIndex: 'status',
              key: 'status',
              render: (status) => status === 'ENABLED' ? '启用' : '禁用'
            }
          ]}
          dataSource={systemApis}
          rowKey="id"
          pagination={{ pageSize: 10 }}
          scroll={{ x: 1000 }}
        />
      </Modal>
    </div>
  )
}

export default SystemManagement

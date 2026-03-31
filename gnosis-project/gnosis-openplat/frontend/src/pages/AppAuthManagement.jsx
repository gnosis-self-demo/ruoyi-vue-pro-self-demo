import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined } from '@ant-design/icons'
import {
  getAppAuthList,
  saveAppAuth,
  updateAppAuth,
  deleteAppAuth,
  batchDeleteAppAuths,
  batchEnableAppAuths,
  batchDisableAppAuths,
  getSystemList
} from '../services/appAuthService'

const { Option } = Select

const AppAuthManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [form] = Form.useForm()
  const [searchForm, setSearchForm] = useState({})
  const [systems, setSystems] = useState([])

  useEffect(() => {
    fetchData()
    fetchSystems()
  }, [searchForm])

  const fetchData = async () => {
    setLoading(true)
    try {
      const res = await getAppAuthList(searchForm)
      setData(res.data || [])
    } catch (error) {
      message.error('获取数据失败')
    }
    setLoading(false)
  }

  const fetchSystems = async () => {
    try {
      const res = await getSystemList({ status: 'ENABLED' })
      setSystems(res.data || [])
    } catch (error) {
      console.error('获取系统列表失败', error)
    }
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
      await deleteAppAuth(id)
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
        await updateAppAuth({ ...editingRecord, ...values })
        message.success('修改成功')
      } else {
        await saveAppAuth(values)
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
      await batchDeleteAppAuths(selectedRowKeys)
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
      await batchEnableAppAuths(selectedRowKeys)
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
      await batchDisableAppAuths(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量禁用失败')
    }
  }

  const columns = [
    {
      title: '应用 ID',
      dataIndex: 'appId',
      key: 'appId'
    },
    {
      title: '应用密钥',
      dataIndex: 'appSecret',
      key: 'appSecret',
      render: (text) => '******'
    },
    {
      title: '关联系统',
      dataIndex: ['system', 'systemName'],
      key: 'systemName'
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
          placeholder="请输入应用 ID"
          onChange={(e) => setSearchForm({ ...searchForm, appId: e.target.value })}
          allowClear
          style={{ width: 200 }}
        />
        <Select
          placeholder="选择系统"
          onChange={(value) => setSearchForm({ ...searchForm, systemId: value })}
          allowClear
          style={{ width: 200 }}
        >
          {systems.map(sys => (
            <Option key={sys.id} value={sys.id}>{sys.systemName}</Option>
          ))}
        </Select>
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
        title={editingRecord ? '编辑应用认证' : '新增应用认证'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="appId" label="应用 ID" rules={[{ required: true, message: '请输入应用 ID' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="appSecret" label="应用密钥" rules={[{ required: true, message: '请输入应用密钥' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="systemId" label="关联系统" rules={[{ required: true, message: '请选择关联系统' }]}>
            <Select>
              {systems.map(sys => (
                <Option key={sys.id} value={sys.id}>{sys.systemName}</Option>
              ))}
            </Select>
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

export default AppAuthManagement

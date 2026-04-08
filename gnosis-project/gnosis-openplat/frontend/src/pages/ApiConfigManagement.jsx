import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, Switch, InputNumber, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined } from '@ant-design/icons'
import {
  getApiConfigList,
  saveApiConfig,
  updateApiConfig,
  deleteApiConfig,
  batchDeleteApiConfigs,
  batchEnableApiConfigs,
  batchDisableApiConfigs,
  relateSystemsToApi
} from '../services/apiConfigService'
import {
  getSystemList
} from '../services/systemService'

const { Option } = Select

const ApiConfigManagement = () => {
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
      const result = await getApiConfigList(searchForm)
      setData(result || [])
    } catch (error) {
      message.error('获取数据失败')
    }
    setLoading(false)
  }

  const fetchSystems = async () => {
    try {
      const result = await getSystemList({ status: 'ENABLED' })
      setSystems(result || [])
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
    const formValues = { ...record }
    if (record.systems && record.systems.length > 0) {
      formValues.systemIds = record.systems.map(sys => sys.id)
    } else if (record.systemId) {
      formValues.systemIds = [record.systemId]
    } else {
      formValues.systemIds = []
    }
    form.setFieldsValue(formValues)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await deleteApiConfig(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const { systemIds, ...apiConfigValues } = values
      
      if (editingRecord) {
        await updateApiConfig({ ...editingRecord, ...apiConfigValues })
        await relateSystemsToApi(editingRecord.id, systemIds)
        message.success('修改成功')
      } else {
        const savedApiConfig = await saveApiConfig(apiConfigValues)
        await relateSystemsToApi(savedApiConfig.id, systemIds)
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
      await batchDeleteApiConfigs(selectedRowKeys)
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
      await batchEnableApiConfigs(selectedRowKeys)
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
      await batchDisableApiConfigs(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      message.error('批量禁用失败')
    }
  }

  const columns = [
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
      title: '关联系统',
      key: 'systemName',
      render: (_, record) => {
        if (record.systems && record.systems.length > 0) {
          return record.systems.map(sys => sys.systemName).join(', ')
        } else if (record.system && record.system.systemName) {
          return record.system.systemName
        }
        return '-'
      }
    },
    {
      title: '示例链接',
      dataIndex: 'exampleUrl',
      key: 'exampleUrl',
      render: (text) => text ? <a href={text} target="_blank" rel="noreferrer">查看示例</a> : '-'
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
          placeholder="请输入 API 编码"
          onChange={(e) => setSearchForm({ ...searchForm, apiCode: e.target.value })}
          allowClear
          style={{ width: 200 }}
        />
        <Input
          placeholder="请输入 API 名称"
          onChange={(e) => setSearchForm({ ...searchForm, apiName: e.target.value })}
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
        scroll={{ x: 1500 }}
      />

      <Modal
        title={editingRecord ? '编辑 API 配置' : '新增 API 配置'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="apiCode" label="API 编码" rules={[{ required: true, message: '请输入 API 编码' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="apiName" label="API 名称" rules={[{ required: true, message: '请输入 API 名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="apiPath" label="API 路径" rules={[{ required: true, message: '请输入 API 路径' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="apiMethod" label="请求方法" rules={[{ required: true, message: '请选择请求方法' }]}>
            <Select>
              <Option value="GET">GET</Option>
              <Option value="POST">POST</Option>
              <Option value="PUT">PUT</Option>
              <Option value="DELETE">DELETE</Option>
            </Select>
          </Form.Item>
          <Form.Item name="systemIds" label="关联系统" rules={[{ required: true, message: '请选择关联系统' }]}>
            <Select mode="multiple" placeholder="请选择关联系统">
              {systems.map(sys => (
                <Option key={sys.id} value={sys.id}>{sys.systemName}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="exampleUrl" label="示例文档链接">
            <Input />
          </Form.Item>
          <Form.Item name="needAuth" label="是否需要认证" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="needAntiReplay" label="是否需要防重放" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="rateLimit" label="限流阈值（次/分钟）">
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

export default ApiConfigManagement

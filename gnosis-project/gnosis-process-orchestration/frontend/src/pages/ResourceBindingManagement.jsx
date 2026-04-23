import React, { useState, useEffect, useRef } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Upload, Popconfirm, Divider, Tag, Row, Col, Select, InputNumber
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  ImportOutlined, ExportOutlined, ReloadOutlined,
  CheckCircleOutlined, CloseCircleOutlined
} from '@ant-design/icons'
import {
  getList,
  getDetail,
  save,
  update,
  remove,
  batchDelete,
  batchEnable,
  batchDisable,
  importData,
  exportData
} from '../api/resourceBindingApi'

const { Option } = Select
const { TextArea } = Input

/**
 * 业务资源绑定管理页面
 */
const ResourceBindingManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  })
  const [searchForm] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [form] = Form.useForm()
  const uploadRef = useRef(null)

  // 加载数据
  const loadData = async (params = {}) => {
    setLoading(true)
    try {
      const searchValues = searchForm.getFieldsValue()
      const queryParams = {
        ...searchValues,
        pageNum: params.current || 1,
        pageSize: params.pageSize || 10
      }

      const response = await getList(queryParams)
      setData(response.data?.records || response.data || [])
      setTotal(response.data?.total || 0)
      setPagination({
        current: response.data?.pageNum || 1,
        size: response.data?.pageSize || 10
      })
    } catch (error) {
      console.error('加载数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  // 搜索
  const handleSearch = () => {
    loadData({ current: 1, pageSize: 10 })
  }

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields()
    loadData({ current: 1, pageSize: 10 })
  }

  // 分页变化
  const handleTableChange = (pag) => {
    loadData({
      current: pag.current,
      pageSize: pag.pageSize
    })
  }

  // 新建
  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  // 编辑
  const handleEdit = async (record) => {
    try {
      const response = await getDetail({ id: record.id })
      const detail = response.data
      setEditingRecord(detail)
      form.setFieldsValue(detail)
      setModalVisible(true)
    } catch (error) {
      console.error('获取详情失败:', error)
    }
  }

  // 删除
  const handleDelete = async (record) => {
    try {
      await remove({ id: record.id })
      message.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }

  // 批量删除
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据')
      return
    }

    try {
      await batchDelete({ ids: selectedRowKeys })
      message.success('批量删除成功')
      setSelectedRowKeys([])
      loadData()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }

  // 批量启用
  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的数据')
      return
    }

    try {
      await batchEnable({ ids: selectedRowKeys })
      message.success('批量启用成功')
      setSelectedRowKeys([])
      loadData()
    } catch (error) {
      console.error('批量启用失败:', error)
    }
  }

  // 批量禁用
  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的数据')
      return
    }

    try {
      await batchDisable({ ids: selectedRowKeys })
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      loadData()
    } catch (error) {
      console.error('批量禁用失败:', error)
    }
  }

  // 提交表单
  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()

      if (editingRecord) {
        await update({ ...values, id: editingRecord.id })
        message.success('更新成功')
      } else {
        await save(values)
        message.success('创建成功')
      }

      setModalVisible(false)
      loadData()
    } catch (error) {
      console.error('保存失败:', error)
    }
  }

  // 导入
  const handleImport = async (file) => {
    try {
      await importData(file)
      message.success('导入成功')
      loadData()
    } catch (error) {
      console.error('导入失败:', error)
    }
    return false
  }

  // 导出
  const handleExport = async () => {
    try {
      const searchValues = searchForm.getFieldsValue()
      const blob = await exportData(searchValues)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `业务资源绑定_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 资源类型映射
  const resourceTypeMap = {
    template: '模板',
    document: '文档',
    form: '表单',
    config: '配置'
  }

  // 绑定类型映射
  const bindingTypeMap = {
    primary: '主要',
    secondary: '次要',
    reference: '引用'
  }

  // 表格列定义
  const columns = [
    {
      title: '业务配置ID',
      dataIndex: 'businessConfigId',
      key: 'businessConfigId',
      width: 150
    },
    {
      title: '资源类型',
      dataIndex: 'resourceType',
      key: 'resourceType',
      width: 120,
      render: (val) => resourceTypeMap[val] || val
    },
    {
      title: '资源ID',
      dataIndex: 'resourceId',
      key: 'resourceId',
      width: 150
    },
    {
      title: '资源名称',
      dataIndex: 'resourceName',
      key: 'resourceName',
      width: 180
    },
    {
      title: '绑定类型',
      dataIndex: 'bindingType',
      key: 'bindingType',
      width: 120,
      render: (val) => bindingTypeMap[val] || val
    },
    {
      title: '绑定优先级',
      dataIndex: 'bindingPriority',
      key: 'bindingPriority',
      width: 120,
      sorter: true
    },
    {
      title: '是否启用',
      dataIndex: 'isActive',
      key: 'isActive',
      width: 100,
      render: (val) => (
        <Tag color={val ? 'green' : 'red'}>
          {val ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '创建人ID',
      dataIndex: 'createUserId',
      key: 'createUserId',
      width: 120
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
      sorter: true
    },
    {
      title: '更新人ID',
      dataIndex: 'updateUserId',
      key: 'updateUserId',
      width: 120
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 160,
      sorter: true
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record)}
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ]

  // 行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: setSelectedRowKeys
  }

  return (
    <div className="page-container">
      {/* 搜索表单 */}
      <Form form={searchForm} className="search-form" layout="inline">
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="业务配置ID" name="businessConfigId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="资源类型" name="resourceType">
              <Select placeholder="请选择" allowClear>
                <Option value="template">模板</Option>
                <Option value="document">文档</Option>
                <Option value="form">表单</Option>
                <Option value="config">配置</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="绑定类型" name="bindingType">
              <Select placeholder="请选择" allowClear>
                <Option value="primary">主要</Option>
                <Option value="secondary">次要</Option>
                <Option value="reference">引用</Option>
              </Select>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="是否启用" name="isActive">
              <Select placeholder="请选择" allowClear>
                <Option value={true}>启用</Option>
                <Option value={false}>禁用</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="创建人ID" name="createUserId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item>
              <Space>
                <Button type="primary" onClick={handleSearch}>
                  查询
                </Button>
                <Button onClick={handleReset}>
                  重置
                </Button>
              </Space>
            </Form.Item>
          </Col>
        </Row>
      </Form>

      {/* 工具栏 */}
      <div className="table-toolbar">
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新建
          </Button>
          <Button icon={<ImportOutlined />}>
            <Upload
              showUploadList={false}
              beforeUpload={handleImport}
              accept=".xlsx,.xls"
            >
              导入
            </Upload>
          </Button>
          <Button icon={<ExportOutlined />} onClick={handleExport}>
            导出
          </Button>
          <Divider type="vertical" />
          <Button
            icon={<CheckCircleOutlined />}
            onClick={handleBatchEnable}
            disabled={selectedRowKeys.length === 0}
          >
            批量启用
          </Button>
          <Button
            icon={<CloseCircleOutlined />}
            onClick={handleBatchDisable}
            disabled={selectedRowKeys.length === 0}
          >
            批量禁用
          </Button>
          <Button
            danger
            icon={<DeleteOutlined />}
            onClick={handleBatchDelete}
            disabled={selectedRowKeys.length === 0}
          >
            批量删除
          </Button>
        </Space>
        <Button icon={<ReloadOutlined />} onClick={() => loadData()}>
          刷新
        </Button>
      </div>

      {/* 数据表格 */}
      <Table
        rowKey="id"
        columns={columns}
        dataSource={data}
        loading={loading}
        rowSelection={rowSelection}
        pagination={{
          ...pagination,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`
        }}
        onChange={handleTableChange}
        scroll={{ x: 1900 }}
      />

      {/* 新建/编辑弹窗 */}
      <Modal
        title={editingRecord ? '编辑业务资源绑定' : '新建业务资源绑定'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={700}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="业务配置ID"
                name="businessConfigId"
                rules={[{ required: true, message: '请输入业务配置ID' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="资源类型"
                name="resourceType"
                rules={[{ required: true, message: '请选择资源类型' }]}
              >
                <Select placeholder="请选择">
                  <Option value="template">模板</Option>
                  <Option value="document">文档</Option>
                  <Option value="form">表单</Option>
                  <Option value="config">配置</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="资源ID"
                name="resourceId"
                rules={[{ required: true, message: '请输入资源ID' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="资源名称"
                name="resourceName"
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="绑定类型"
                name="bindingType"
                rules={[{ required: true, message: '请选择绑定类型' }]}
              >
                <Select placeholder="请选择">
                  <Option value="primary">主要</Option>
                  <Option value="secondary">次要</Option>
                  <Option value="reference">引用</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="绑定优先级"
                name="bindingPriority"
                initialValue={0}
              >
                <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="是否启用"
                name="isActive"
                initialValue={true}
              >
                <Select>
                  <Option value={true}>启用</Option>
                  <Option value={false}>禁用</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="元数据"
            name="metadata"
          >
            <TextArea rows={4} placeholder="请输入元数据" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ResourceBindingManagement

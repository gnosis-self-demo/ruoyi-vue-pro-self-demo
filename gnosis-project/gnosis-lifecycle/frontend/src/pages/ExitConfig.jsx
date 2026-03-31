import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Popconfirm, Tag, Row, Col, Select, Switch, Divider, Upload
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  ImportOutlined, ExportOutlined, ReloadOutlined,
  CheckCircleOutlined, CloseCircleOutlined
} from '@ant-design/icons'
import {
  getFinalStateList,
  createFinalState,
  updateFinalState,
  deleteFinalState,
  batchDeleteFinalStates,
  batchEnableFinalStates,
  batchDisableFinalStates,
  importFinalStates,
  exportFinalStates,
  getDownstreamList,
  createDownstream,
  updateDownstream,
  deleteDownstream,
  batchDeleteDownstreams,
  batchEnableDownstreams,
  batchDisableDownstreams,
  importDownstreams,
  exportDownstreams
} from '../api/exitApi'

const { Option } = Select

/**
 * 出口配置页面
 */
const ExitConfig = () => {
  const [loading, setLoading] = useState(false)
  const [finalStateData, setFinalStateData] = useState([])
  const [downstreamData, setDownstreamData] = useState([])
  const [total, setTotal] = useState(0)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  })
  const [searchForm] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [modalType, setModalType] = useState('finalState') // 'finalState' or 'downstream'
  const [editingRecord, setEditingRecord] = useState(null)
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const [form] = Form.useForm()

  // 加载终态数据
  const loadFinalStateData = async (params = {}) => {
    setLoading(true)
    try {
      const searchValues = searchForm.getFieldsValue()
      const queryParams = {
        ...searchValues,
        pageNum: params.current || 1,
        pageSize: params.pageSize || 10
      }
      
      const response = await getFinalStateList(queryParams)
      setFinalStateData(response.data?.records || response.data || [])
      setTotal(response.data?.total || 0)
      setPagination({
        current: response.data?.current || 1,
        pageSize: response.data?.size || 10
      })
    } catch (error) {
      console.error('加载数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 加载下游数据
  const loadDownstreamData = async (params = {}) => {
    setLoading(true)
    try {
      const response = await getDownstreamList(params)
      setDownstreamData(response.data?.records || response.data || [])
    } catch (error) {
      console.error('加载数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadFinalStateData()
    loadDownstreamData()
  }, [])

  // 搜索
  const handleSearch = () => {
    loadFinalStateData({ current: 1, pageSize: 10 })
  }

  // 重置搜索
  const handleReset = () => {
    searchForm.resetFields()
    loadFinalStateData({ current: 1, pageSize: 10 })
  }

  // 分页变化
  const handleTableChange = (pag) => {
    loadFinalStateData({
      current: pag.current,
      pageSize: pag.pageSize
    })
  }

  // 新建终态
  const handleAddFinalState = () => {
    setModalType('finalState')
    setEditingRecord(null)
    form.resetFields()
    form.setFieldsValue({
      status: 1
    })
    setModalVisible(true)
  }

  // 编辑终态
  const handleEditFinalState = async (record) => {
    setModalType('finalState')
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  // 删除终态
  const handleDeleteFinalState = async (record) => {
    try {
      await deleteFinalState(record.id)
      message.success('删除成功')
      loadFinalStateData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }

  // 批量删除终态
  const handleBatchDeleteFinalStates = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据')
      return
    }

    try {
      await batchDeleteFinalStates(selectedRowKeys)
      message.success('批量删除成功')
      setSelectedRowKeys([])
      loadFinalStateData()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }

  // 批量启用终态
  const handleBatchEnableFinalStates = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的数据')
      return
    }

    try {
      await batchEnableFinalStates(selectedRowKeys)
      message.success('批量启用成功')
      setSelectedRowKeys([])
      loadFinalStateData()
    } catch (error) {
      console.error('批量启用失败:', error)
    }
  }

  // 批量禁用终态
  const handleBatchDisableFinalStates = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的数据')
      return
    }

    try {
      await batchDisableFinalStates(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      loadFinalStateData()
    } catch (error) {
      console.error('批量禁用失败:', error)
    }
  }

  // 新建下游
  const handleAddDownstream = () => {
    setModalType('downstream')
    setEditingRecord(null)
    form.resetFields()
    form.setFieldsValue({
      status: 1
    })
    setModalVisible(true)
  }

  // 编辑下游
  const handleEditDownstream = async (record) => {
    setModalType('downstream')
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  // 删除下游
  const handleDeleteDownstream = async (record) => {
    try {
      await deleteDownstream(record.id)
      message.success('删除成功')
      loadDownstreamData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }

  // 提交表单
  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      
      if (modalType === 'finalState') {
        if (editingRecord) {
          await updateFinalState(editingRecord.id, values)
          message.success('更新成功')
        } else {
          await createFinalState(values)
          message.success('创建成功')
        }
        loadFinalStateData()
      } else {
        if (editingRecord) {
          await updateDownstream(editingRecord.id, values)
          message.success('更新成功')
        } else {
          await createDownstream(values)
          message.success('创建成功')
        }
        loadDownstreamData()
      }
      
      setModalVisible(false)
    } catch (error) {
      console.error('保存失败:', error)
    }
  }

  // 导入终态
  const handleImportFinalStates = async (file) => {
    try {
      await importFinalStates(file)
      message.success('导入成功')
      loadFinalStateData()
    } catch (error) {
      console.error('导入失败:', error)
    }
    return false
  }

  // 导出终态
  const handleExportFinalStates = async () => {
    try {
      const searchValues = searchForm.getFieldsValue()
      const blob = await exportFinalStates(searchValues)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `终态配置_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 导入下游
  const handleImportDownstreams = async (file) => {
    try {
      await importDownstreams(file)
      message.success('导入成功')
      loadDownstreamData()
    } catch (error) {
      console.error('导入失败:', error)
    }
    return false
  }

  // 导出下游
  const handleExportDownstreams = async () => {
    try {
      const blob = await exportDownstreams({})
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `下游配置_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 终态表格列定义
  const finalStateColumns = [
    {
      title: '终态编码',
      dataIndex: 'stateCode',
      key: 'stateCode',
      width: 150
    },
    {
      title: '终态名称',
      dataIndex: 'stateName',
      key: 'stateName',
      width: 150
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 250,
      ellipsis: true
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '创建人 ID',
      dataIndex: 'createUserId',
      key: 'createUserId',
      width: 120
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160
    },
    {
      title: '更新人 ID',
      dataIndex: 'updateUserId',
      key: 'updateUserId',
      width: 120
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 160
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditFinalState(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDeleteFinalState(record)}
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

  // 下游表格列定义
  const downstreamColumns = [
    {
      title: '下游编码',
      dataIndex: 'downstreamCode',
      key: 'downstreamCode',
      width: 150
    },
    {
      title: '下游名称',
      dataIndex: 'downstreamName',
      key: 'downstreamName',
      width: 150
    },
    {
      title: '触发终态',
      dataIndex: 'finalStateCode',
      key: 'finalStateCode',
      width: 120
    },
    {
      title: '触发方式',
      dataIndex: 'triggerType',
      key: 'triggerType',
      width: 100
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '创建人 ID',
      dataIndex: 'createUserId',
      key: 'createUserId',
      width: 120
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160
    },
    {
      title: '更新人 ID',
      dataIndex: 'updateUserId',
      key: 'updateUserId',
      width: 120
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 160
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEditDownstream(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDeleteDownstream(record)}
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
      {/* 终态配置区域 */}
      <div style={{ marginBottom: 24 }}>
        <h3>终态配置</h3>
        
        {/* 搜索表单 */}
        <Form form={searchForm} className="search-form" layout="inline">
          <Row gutter={16}>
            <Col span={6}>
              <Form.Item label="终态编码" name="stateCode">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label="终态名称" name="stateName">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label="状态" name="status">
                <Select placeholder="请选择" allowClear>
                  <Option value={1}>启用</Option>
                  <Option value={0}>禁用</Option>
                </Select>
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
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAddFinalState}>
              新建终态
            </Button>
            <Button icon={<ImportOutlined />}>
              <Upload
                showUploadList={false}
                beforeUpload={handleImportFinalStates}
                accept=".xlsx,.xls"
              >
                导入
              </Upload>
            </Button>
            <Button icon={<ExportOutlined />} onClick={handleExportFinalStates}>
              导出
            </Button>
            <Divider type="vertical" />
            <Button
              icon={<CheckCircleOutlined />}
              onClick={handleBatchEnableFinalStates}
              disabled={selectedRowKeys.length === 0}
            >
              批量启用
            </Button>
            <Button
              icon={<CloseCircleOutlined />}
              onClick={handleBatchDisableFinalStates}
              disabled={selectedRowKeys.length === 0}
            >
              批量禁用
            </Button>
            <Button
              danger
              icon={<DeleteOutlined />}
              onClick={handleBatchDeleteFinalStates}
              disabled={selectedRowKeys.length === 0}
            >
              批量删除
            </Button>
          </Space>
          <Button icon={<ReloadOutlined />} onClick={() => loadFinalStateData()}>
            刷新
          </Button>
        </div>

        {/* 数据表格 */}
        <Table
          rowKey="id"
          columns={finalStateColumns}
          dataSource={finalStateData}
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
          scroll={{ x: 1500 }}
        />
      </div>

      {/* 下游配置区域 */}
      <div>
        <h3>下游触发配置</h3>
        
        {/* 工具栏 */}
        <div className="table-toolbar">
          <Space>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAddDownstream}>
              新建下游配置
            </Button>
            <Button icon={<ImportOutlined />}>
              <Upload
                showUploadList={false}
                beforeUpload={handleImportDownstreams}
                accept=".xlsx,.xls"
              >
                导入
              </Upload>
            </Button>
            <Button icon={<ExportOutlined />} onClick={handleExportDownstreams}>
              导出
            </Button>
          </Space>
          <Button icon={<ReloadOutlined />} onClick={() => loadDownstreamData()}>
            刷新
          </Button>
        </div>

        {/* 数据表格 */}
        <Table
          rowKey="id"
          columns={downstreamColumns}
          dataSource={downstreamData}
          loading={loading}
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`
          }}
          scroll={{ x: 1500 }}
        />
      </div>

      {/* 新建/编辑弹窗 */}
      <Modal
        title={modalType === 'finalState' ? 
          (editingRecord ? '编辑终态' : '新建终态') : 
          (editingRecord ? '编辑下游配置' : '新建下游配置')}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          {modalType === 'finalState' ? (
            <>
              <Form.Item
                label="终态编码"
                name="stateCode"
                rules={[{ required: true, message: '请输入终态编码' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <Form.Item
                label="终态名称"
                name="stateName"
                rules={[{ required: true, message: '请输入终态名称' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <Form.Item
                label="描述"
                name="description"
              >
                <Input.TextArea rows={4} placeholder="请输入描述" />
              </Form.Item>
              <Form.Item
                label="状态"
                name="status"
                valuePropName="checked"
                initialValue={true}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用" />
              </Form.Item>
            </>
          ) : (
            <>
              <Form.Item
                label="下游编码"
                name="downstreamCode"
                rules={[{ required: true, message: '请输入下游编码' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <Form.Item
                label="下游名称"
                name="downstreamName"
                rules={[{ required: true, message: '请输入下游名称' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
              <Form.Item
                label="触发终态"
                name="finalStateCode"
                rules={[{ required: true, message: '请选择触发终态' }]}
              >
                <Select placeholder="请选择">
                  {finalStateData.map(item => (
                    <Option key={item.id} value={item.stateCode}>
                      {item.stateName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item
                label="触发方式"
                name="triggerType"
                rules={[{ required: true, message: '请选择触发方式' }]}
              >
                <Select placeholder="请选择">
                  <Option value="AUTO">自动触发</Option>
                  <Option value="MANUAL">手动触发</Option>
                </Select>
              </Form.Item>
              <Form.Item
                label="状态"
                name="status"
                valuePropName="checked"
                initialValue={true}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用" />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  )
}

export default ExitConfig

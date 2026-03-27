import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Popconfirm, Tag, Row, Col, Select, Divider, Switch, Upload
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  ImportOutlined, ExportOutlined, ReloadOutlined,
  CheckCircleOutlined, CloseCircleOutlined
} from '@ant-design/icons'
import {
  getRouteRuleList,
  getRouteRuleDetail,
  createRouteRule,
  updateRouteRule,
  deleteRouteRule,
  batchEnableRules,
  batchDisableRules,
  batchDeleteRules,
  importRouteRules,
  exportRouteRules
} from '../api/ruleApi'
import ConditionExpressionEditor from '../components/ConditionExpressionEditor'
import RouteRuleTester from '../components/RouteRuleTester'

const { Option } = Select

/**
 * 路由规则配置页面
 */
const RouteRuleConfig = () => {
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
  const [testModalVisible, setTestModalVisible] = useState(false)
  const [metadataFields, setMetadataFields] = useState([])

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
      
      const response = await getRouteRuleList(queryParams)
      setData(response.data?.records || response.data || [])
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
    form.setFieldsValue({
      status: 1,
      priority: 0
    })
    setModalVisible(true)
  }

  // 编辑
  const handleEdit = async (record) => {
    try {
      const response = await getRouteRuleDetail(record.id)
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
      await deleteRouteRule(record.id)
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
      await batchDeleteRules(selectedRowKeys)
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
      await batchEnableRules(selectedRowKeys)
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
      await batchDisableRules(selectedRowKeys)
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
        await updateRouteRule(editingRecord.id, values)
        message.success('更新成功')
      } else {
        await createRouteRule(values)
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
      await importRouteRules(file)
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
      const blob = await exportRouteRules(searchValues)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `路由规则_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 打开测试沙箱
  const handleOpenTest = () => {
    setTestModalVisible(true)
  }

  // 表格列定义
  const columns = [
    {
      title: '规则编码',
      dataIndex: 'ruleCode',
      key: 'ruleCode',
      width: 150
    },
    {
      title: '规则名称',
      dataIndex: 'ruleName',
      key: 'ruleName',
      width: 200
    },
    {
      title: '源状态',
      dataIndex: 'sourceState',
      key: 'sourceState',
      width: 100
    },
    {
      title: '目标状态',
      dataIndex: 'targetState',
      key: 'targetState',
      width: 100
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      key: 'priority',
      width: 80,
      sorter: true
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
      width: 250,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small" direction="vertical">
          <Space size="small">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
            <Button
              type="link"
              size="small"
              onClick={() => handleOpenTest()}
            >
              测试
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
            <Form.Item label="规则编码" name="ruleCode">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="规则名称" name="ruleName">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="源状态" name="sourceState">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="目标状态" name="targetState">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
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
          <Button
            type="primary"
            onClick={handleOpenTest}
          >
            规则测试
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
        scroll={{ x: 1600 }}
      />

      {/* 新建/编辑弹窗 */}
      <Modal
        title={editingRecord ? '编辑路由规则' : '新建路由规则'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={800}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="规则编码"
                name="ruleCode"
                rules={[{ required: true, message: '请输入规则编码' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="规则名称"
                name="ruleName"
                rules={[{ required: true, message: '请输入规则名称' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="源状态"
                name="sourceState"
                rules={[{ required: true, message: '请输入源状态' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="目标状态"
                name="targetState"
                rules={[{ required: true, message: '请输入目标状态' }]}
              >
                <Input placeholder="请输入" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="优先级"
                name="priority"
                rules={[{ required: true, message: '请输入优先级' }]}
              >
                <Input type="number" placeholder="数字越小优先级越高" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="状态"
                name="status"
                valuePropName="checked"
                initialValue={true}
              >
                <Switch checkedChildren="启用" unCheckedChildren="禁用" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="描述"
            name="description"
          >
            <Input.TextArea rows={3} placeholder="请输入描述" />
          </Form.Item>

          <Divider />

          <ConditionExpressionEditor
            value={form.getFieldValue('conditions') || []}
            onChange={(val) => form.setFieldsValue({ conditions: val })}
            metadataFields={metadataFields}
          />
        </Form>
      </Modal>

      {/* 测试沙箱弹窗 */}
      <Modal
        title="规则测试沙箱"
        open={testModalVisible}
        onCancel={() => setTestModalVisible(false)}
        width={1000}
        footer={null}
      >
        <RouteRuleTester
          rules={data}
          metadataFields={metadataFields}
        />
      </Modal>
    </div>
  )
}

export default RouteRuleConfig

import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Popconfirm, Tag, Row, Col, Select, Divider
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  ImportOutlined, ExportOutlined, ReloadOutlined
} from '@ant-design/icons'
import {
  getMetadataDictionary
} from '../api/eventApi'

const { Option } = Select

/**
 * 元数据字典页面
 */
const MetadataDictionary = () => {
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
  const [form] = Form.useForm()

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
      
      // TODO: 调用实际 API 获取元数据字典
      // 这里使用模拟数据
      const mockData = [
        {
          id: 1,
          fieldName: 'orderId',
          fieldLabel: '订单 ID',
          fieldType: 'STRING',
          required: true,
          defaultValue: '',
          description: '订单唯一标识',
          createUserId: 'admin',
          createTime: '2024-01-01 10:00:00',
          updateUserId: 'admin',
          updateTime: '2024-01-01 10:00:00'
        },
        {
          id: 2,
          fieldName: 'amount',
          fieldLabel: '订单金额',
          fieldType: 'NUMBER',
          required: true,
          defaultValue: '0',
          description: '订单交易金额',
          createUserId: 'admin',
          createTime: '2024-01-01 10:00:00',
          updateUserId: 'admin',
          updateTime: '2024-01-01 10:00:00'
        }
      ]
      
      setData(mockData)
      setTotal(mockData.length)
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
      required: false
    })
    setModalVisible(true)
  }

  // 编辑
  const handleEdit = async (record) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  // 删除
  const handleDelete = async (record) => {
    try {
      // TODO: 调用删除 API
      message.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }

  // 提交表单
  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      
      if (editingRecord) {
        // TODO: 调用更新 API
        message.success('更新成功')
      } else {
        // TODO: 调用创建 API
        message.success('创建成功')
      }
      
      setModalVisible(false)
      loadData()
    } catch (error) {
      console.error('保存失败:', error)
    }
  }

  // 字段类型选项
  const fieldTypeOptions = [
    { label: '字符串', value: 'STRING' },
    { label: '数字', value: 'NUMBER' },
    { label: '布尔值', value: 'BOOLEAN' },
    { label: '日期', value: 'DATE' },
    { label: '日期时间', value: 'DATETIME' },
    { label: 'JSON 对象', value: 'JSON' },
    { label: '数组', value: 'ARRAY' }
  ]

  // 表格列定义
  const columns = [
    {
      title: '字段名',
      dataIndex: 'fieldName',
      key: 'fieldName',
      width: 150
    },
    {
      title: '字段标签',
      dataIndex: 'fieldLabel',
      key: 'fieldLabel',
      width: 150
    },
    {
      title: '字段类型',
      dataIndex: 'fieldType',
      key: 'fieldType',
      width: 100,
      render: (type) => <Tag>{type}</Tag>
    },
    {
      title: '必填',
      dataIndex: 'required',
      key: 'required',
      width: 60,
      render: (required) => (
        <Tag color={required ? 'red' : 'default'}>
          {required ? '是' : '否'}
        </Tag>
      )
    },
    {
      title: '默认值',
      dataIndex: 'defaultValue',
      key: 'defaultValue',
      width: 120,
      ellipsis: true
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 200,
      ellipsis: true
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
      width: 160,
      sorter: true
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
      width: 160,
      sorter: true
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

  return (
    <div className="page-container">
      {/* 搜索表单 */}
      <Form form={searchForm} className="search-form" layout="inline">
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="字段名" name="fieldName">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="字段标签" name="fieldLabel">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="字段类型" name="fieldType">
              <Select placeholder="请选择" allowClear>
                {fieldTypeOptions.map(opt => (
                  <Option key={opt.value} value={opt.value}>
                    {opt.label}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="是否必填" name="required">
              <Select placeholder="请选择" allowClear>
                <Option value={true}>是</Option>
                <Option value={false}>否</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="创建人 ID" name="createUserId">
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
            新建字段
          </Button>
          <Button icon={<ImportOutlined />}>
            导入
          </Button>
          <Button icon={<ExportOutlined />}>
            导出
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

      {/* 新建/编辑弹窗 */}
      <Modal
        title={editingRecord ? '编辑元数据字段' : '新建元数据字段'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={700}
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="字段名"
                name="fieldName"
                rules={[{ required: true, message: '请输入字段名' }]}
              >
                <Input placeholder="请输入英文字段名" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="字段标签"
                name="fieldLabel"
                rules={[{ required: true, message: '请输入字段标签' }]}
              >
                <Input placeholder="请输入中文标签" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="字段类型"
                name="fieldType"
                rules={[{ required: true, message: '请选择字段类型' }]}
              >
                <Select placeholder="请选择">
                  {fieldTypeOptions.map(opt => (
                    <Option key={opt.value} value={opt.value}>
                      {opt.label}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="默认值"
                name="defaultValue"
              >
                <Input placeholder="请输入默认值" />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="是否必填"
                name="required"
                valuePropName="checked"
              >
                <Select>
                  <Option value={true}>是</Option>
                  <Option value={false}>否</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            label="描述"
            name="description"
          >
            <Input.TextArea rows={4} placeholder="请输入字段描述" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default MetadataDictionary

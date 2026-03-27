import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Popconfirm, Tag, Row, Col, Card
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  ImportOutlined, ExportOutlined, ReloadOutlined
} from '@ant-design/icons'
import StateDiagramCanvas from '../components/StateDiagramCanvas'

/**
 * 状态模型配置页面
 */
const StateModelConfig = () => {
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
  
  // 状态节点和边
  const [nodes, setNodes] = useState([])
  const [edges, setEdges] = useState([])

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
      
      // TODO: 替换为实际 API
      setData([])
      setTotal(0)
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

  // 处理节点添加
  const handleNodeAdd = (node) => {
    setNodes([...nodes, node])
  }

  // 处理节点删除
  const handleNodeDelete = (node) => {
    setNodes(nodes.filter(n => n.id !== node.id))
    setEdges(edges.filter(e => e.source !== node.id && e.target !== node.id))
  }

  // 处理节点更新
  const handleNodeUpdate = (updatedNode) => {
    setNodes(nodes.map(n => n.id === updatedNode.id ? updatedNode : n))
  }

  // 表格列定义
  const columns = [
    {
      title: '状态编码',
      dataIndex: 'stateCode',
      key: 'stateCode',
      width: 150
    },
    {
      title: '状态名称',
      dataIndex: 'stateName',
      key: 'stateName',
      width: 150
    },
    {
      title: '状态类型',
      dataIndex: 'stateType',
      key: 'stateType',
      width: 100,
      render: (type) => {
        const colorMap = {
          start: 'green',
          normal: 'blue',
          final: 'red'
        }
        return <Tag color={colorMap[type] || 'default'}>{type}</Tag>
      }
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      width: 250,
      ellipsis: true
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 80,
      sorter: true
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
      {/* 状态图绘制区域 */}
      <StateDiagramCanvas
        nodes={nodes}
        edges={edges}
        onNodeAdd={handleNodeAdd}
        onNodeDelete={handleNodeDelete}
        onNodeUpdate={handleNodeUpdate}
      />

      {/* 搜索表单 */}
      <Card className="search-form" style={{ marginTop: 16 }}>
        <Form form={searchForm} layout="inline">
          <Row gutter={16}>
            <Col span={6}>
              <Form.Item label="状态编码" name="stateCode">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label="状态名称" name="stateName">
                <Input placeholder="请输入" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item label="状态类型" name="stateType">
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
      </Card>

      {/* 工具栏 */}
      <div className="table-toolbar">
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新建状态
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
        title={editingRecord ? '编辑状态' : '新建状态'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="状态编码"
            name="stateCode"
            rules={[{ required: true, message: '请输入状态编码' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="状态名称"
            name="stateName"
            rules={[{ required: true, message: '请输入状态名称' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="状态类型"
            name="stateType"
            rules={[{ required: true, message: '请选择状态类型' }]}
          >
            <Input placeholder="start/normal/final" />
          </Form.Item>
          <Form.Item
            label="描述"
            name="description"
          >
            <Input.TextArea rows={4} placeholder="请输入描述" />
          </Form.Item>
          <Form.Item
            label="排序"
            name="sort"
            initialValue={0}
          >
            <Input type="number" placeholder="请输入排序" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default StateModelConfig

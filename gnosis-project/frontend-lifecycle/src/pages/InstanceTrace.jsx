import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Tag, Row, Col, Drawer, Timeline, Card, Statistic
} from 'antd'
import {
  SearchOutlined, ReloadOutlined, EyeOutlined,
  LineChartOutlined, ClockCircleOutlined
} from '@ant-design/icons'
import {
  getInstanceTrace,
  getTraceDashboard
} from '../api/governanceApi'
import TraceTimeline from '../components/TraceTimeline'

/**
 * 实例追踪页面
 */
const InstanceTrace = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  })
  const [searchForm] = Form.useForm()
  const [traceVisible, setTraceVisible] = useState(false)
  const [currentRecord, setCurrentRecord] = useState(null)
  const [traceEvents, setTraceEvents] = useState([])
  const [dashboardData, setDashboardData] = useState({
    totalInstances: 0,
    runningInstances: 0,
    completedInstances: 0,
    errorInstances: 0
  })

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
      
      // TODO: 调用实际 API
      setData([])
      setTotal(0)
    } catch (error) {
      console.error('加载数据失败:', error)
    } finally {
      setLoading(false)
    }
  }

  // 加载看板数据
  const loadDashboard = async () => {
    try {
      const response = await getTraceDashboard({})
      setDashboardData(response.data || dashboardData)
    } catch (error) {
      console.error('加载看板失败:', error)
    }
  }

  useEffect(() => {
    loadData()
    loadDashboard()
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

  // 查看追踪
  const handleViewTrace = async (record) => {
    try {
      const response = await getInstanceTrace(record.instanceId)
      setTraceEvents(response.data?.events || [])
      setCurrentRecord(record)
      setTraceVisible(true)
    } catch (error) {
      console.error('获取追踪失败:', error)
    }
  }

  // 表格列定义
  const columns = [
    {
      title: '实例 ID',
      dataIndex: 'instanceId',
      key: 'instanceId',
      width: 180,
      fixed: 'left'
    },
    {
      title: '业务类型',
      dataIndex: 'businessType',
      key: 'businessType',
      width: 120
    },
    {
      title: '当前状态',
      dataIndex: 'currentState',
      key: 'currentState',
      width: 100,
      render: (state) => <Tag color="processing">{state}</Tag>
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => {
        const colorMap = {
          RUNNING: 'blue',
          COMPLETED: 'green',
          ERROR: 'red',
          CANCELLED: 'default'
        }
        return <Tag color={colorMap[status] || 'default'}>{status}</Tag>
      }
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
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Button
          type="link"
          size="small"
          icon={<EyeOutlined />}
          onClick={() => handleViewTrace(record)}
        >
          追踪路径
        </Button>
      )
    }
  ]

  return (
    <div className="page-container">
      {/* 统计看板 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="总实例数"
              value={dashboardData.totalInstances}
              prefix={<LineChartOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="运行中"
              value={dashboardData.runningInstances}
              valueStyle={{ color: '#1890ff' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已完成"
              value={dashboardData.completedInstances}
              valueStyle={{ color: '#52c41a' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="异常"
              value={dashboardData.errorInstances}
              valueStyle={{ color: '#ff4d4f' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 搜索表单 */}
      <Form form={searchForm} className="search-form" layout="inline">
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="实例 ID" name="instanceId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="业务类型" name="businessType">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="当前状态" name="currentState">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="状态" name="status">
              <Input placeholder="请输入" allowClear />
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
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
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
          <Button type="primary" icon={<LineChartOutlined />}>
            流转路径分析
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

      {/* 追踪弹窗 */}
      <Drawer
        title={`实例追踪 - ${currentRecord?.instanceId || ''}`}
        placement="right"
        width={900}
        open={traceVisible}
        onClose={() => setTraceVisible(false)}
      >
        <TraceTimeline events={traceEvents} />
      </Drawer>
    </div>
  )
}

export default InstanceTrace

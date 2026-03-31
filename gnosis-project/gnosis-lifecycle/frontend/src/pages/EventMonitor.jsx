import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Modal, message,
  Tag, Row, Col, Descriptions, Drawer
} from 'antd'
import {
  SearchOutlined, ReloadOutlined, ExportOutlined,
  EyeOutlined
} from '@ant-design/icons'
import {
  getEventList,
  getEventDetail,
  exportEvents
} from '../api/eventApi'
import TraceTimeline from '../components/TraceTimeline'

/**
 * 事件监控页面
 */
const EventMonitor = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  })
  const [searchForm] = Form.useForm()
  const [detailVisible, setDetailVisible] = useState(false)
  const [currentRecord, setCurrentRecord] = useState(null)
  const [eventDetail, setEventDetail] = useState(null)
  const [traceVisible, setTraceVisible] = useState(false)
  const [traceEvents, setTraceEvents] = useState([])

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
      
      const response = await getEventList(queryParams)
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

  // 查看详情
  const handleViewDetail = async (record) => {
    try {
      const response = await getEventDetail(record.id)
      setEventDetail(response.data)
      setDetailVisible(true)
    } catch (error) {
      console.error('获取详情失败:', error)
    }
  }

  // 查看追踪
  const handleViewTrace = async (record) => {
    try {
      // TODO: 调用获取追踪 API
      setTraceEvents([
        {
          id: 1,
          eventType: 'CREATE',
          timestamp: '2024-01-01 10:00:00',
          currentState: 'INIT',
          eventData: { orderId: '12345' }
        },
        {
          id: 2,
          eventType: 'TRANSITION',
          timestamp: '2024-01-01 10:05:00',
          previousState: 'INIT',
          currentState: 'PROCESSING',
          eventData: { handler: 'user1' }
        }
      ])
      setTraceVisible(true)
    } catch (error) {
      console.error('获取追踪失败:', error)
    }
  }

  // 导出
  const handleExport = async () => {
    try {
      const searchValues = searchForm.getFieldsValue()
      const blob = await exportEvents(searchValues)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `事件记录_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 表格列定义
  const columns = [
    {
      title: '事件 ID',
      dataIndex: 'eventId',
      key: 'eventId',
      width: 150
    },
    {
      title: '业务类型',
      dataIndex: 'businessType',
      key: 'businessType',
      width: 120
    },
    {
      title: '事件类型',
      dataIndex: 'eventType',
      key: 'eventType',
      width: 120,
      render: (type) => {
        const colorMap = {
          CREATE: 'blue',
          TRANSITION: 'cyan',
          COMPLETE: 'green',
          ERROR: 'red'
        }
        return <Tag color={colorMap[type] || 'default'}>{type}</Tag>
      }
    },
    {
      title: '实例 ID',
      dataIndex: 'instanceId',
      key: 'instanceId',
      width: 150
    },
    {
      title: '当前状态',
      dataIndex: 'currentState',
      key: 'currentState',
      width: 100
    },
    {
      title: '上一状态',
      dataIndex: 'previousState',
      key: 'previousState',
      width: 100
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
        <Space size="small" direction="vertical">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => handleViewDetail(record)}
          >
            详情
          </Button>
          <Button
            type="link"
            size="small"
            onClick={() => handleViewTrace(record)}
          >
            追踪
          </Button>
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
            <Form.Item label="事件 ID" name="eventId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="业务类型" name="businessType">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="事件类型" name="eventType">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="实例 ID" name="instanceId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="当前状态" name="currentState">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
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
          <Button icon={<ExportOutlined />} onClick={handleExport}>
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

      {/* 详情弹窗 */}
      <Drawer
        title="事件详情"
        placement="right"
        width={800}
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
      >
        {eventDetail && (
          <Descriptions column={2} bordered size="small">
            <Descriptions.Item label="事件 ID">{eventDetail.eventId}</Descriptions.Item>
            <Descriptions.Item label="业务类型">{eventDetail.businessType}</Descriptions.Item>
            <Descriptions.Item label="事件类型">
              <Tag>{eventDetail.eventType}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="实例 ID">{eventDetail.instanceId}</Descriptions.Item>
            <Descriptions.Item label="当前状态">{eventDetail.currentState}</Descriptions.Item>
            <Descriptions.Item label="上一状态">{eventDetail.previousState}</Descriptions.Item>
            <Descriptions.Item label="创建人 ID">{eventDetail.createUserId}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{eventDetail.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新人 ID">{eventDetail.updateUserId}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{eventDetail.updateTime}</Descriptions.Item>
            <Descriptions.Item label="错误信息" span={2}>
              {eventDetail.errorMessage || '无'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>

      {/* 追踪弹窗 */}
      <Drawer
        title="实例追踪"
        placement="right"
        width={800}
        open={traceVisible}
        onClose={() => setTraceVisible(false)}
      >
        <TraceTimeline events={traceEvents} />
      </Drawer>
    </div>
  )
}

export default EventMonitor

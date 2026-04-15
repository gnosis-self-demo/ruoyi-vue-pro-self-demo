import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Input, Form, Tag, Row, Col, Select, message
} from 'antd'
import {
  ExportOutlined, ReloadOutlined
} from '@ant-design/icons'
import {
  getList,
  getDetail,
  exportData
} from '../api/transitionFlowApi'

const { Option } = Select

/**
 * 流程流转日志管理页面（只读）
 */
const TransitionFlowManagement = () => {
  const [loading, setLoading] = useState(false)
  const [data, setData] = useState([])
  const [total, setTotal] = useState(0)
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10
  })
  const [searchForm] = Form.useForm()
  const [detailVisible, setDetailVisible] = useState(false)
  const [detailData, setDetailData] = useState(null)

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
  const handleDetail = async (record) => {
    try {
      const response = await getDetail({ id: record.id })
      setDetailData(response.data)
      setDetailVisible(true)
    } catch (error) {
      console.error('获取详情失败:', error)
    }
  }

  // 导出
  const handleExport = async () => {
    try {
      const searchValues = searchForm.getFieldsValue()
      const blob = await exportData(searchValues)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `流转日志_${Date.now()}.xlsx`
      link.click()
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error('导出失败:', error)
    }
  }

  // 流转类型映射
  const transitionTypeMap = {
    auto: '自动',
    manual: '手动'
  }

  // 流转结果映射
  const transitionResultMap = {
    success: '成功',
    failed: '失败',
    pending: '待处理'
  }

  // 流转结果颜色映射
  const transitionResultColorMap = {
    success: 'green',
    failed: 'red',
    pending: 'orange'
  }

  // 流转类型颜色映射
  const transitionTypeColorMap = {
    auto: 'blue',
    manual: 'green'
  }

  // 表格列定义
  const columns = [
    {
      title: '流程实例ID',
      dataIndex: 'processInstanceId',
      key: 'processInstanceId',
      width: 150
    },
    {
      title: '流程定义Key',
      dataIndex: 'processDefKey',
      key: 'processDefKey',
      width: 150
    },
    {
      title: '节点定义Key',
      dataIndex: 'nodeDefKey',
      key: 'nodeDefKey',
      width: 150
    },
    {
      title: '业务描述',
      dataIndex: 'businessDescription',
      key: 'businessDescription',
      width: 160,
      ellipsis: true
    },
    {
      title: '业务ID',
      dataIndex: 'businessId',
      key: 'businessId',
      width: 130
    },
    {
      title: '来源节点',
      dataIndex: 'fromNode',
      key: 'fromNode',
      width: 120
    },
    {
      title: '目标节点',
      dataIndex: 'toNode',
      key: 'toNode',
      width: 120
    },
    {
      title: '流转类型',
      dataIndex: 'transitionType',
      key: 'transitionType',
      width: 100,
      render: (val) => (
        <Tag color={transitionTypeColorMap[val] || 'default'}>
          {transitionTypeMap[val] || val}
        </Tag>
      )
    },
    {
      title: '流转结果',
      dataIndex: 'transitionResult',
      key: 'transitionResult',
      width: 100,
      render: (val) => (
        <Tag color={transitionResultColorMap[val] || 'default'}>
          {transitionResultMap[val] || val}
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
      width: 80,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            onClick={() => handleDetail(record)}
          >
            详情
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
            <Form.Item label="流程实例ID" name="processInstanceId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="业务ID" name="businessId">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="流程定义Key" name="processDefKey">
              <Input placeholder="请输入" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item label="流转结果" name="transitionResult">
              <Select placeholder="请选择" allowClear>
                <Option value="success">成功</Option>
                <Option value="failed">失败</Option>
                <Option value="pending">待处理</Option>
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={16}>
          <Col span={6}>
            <Form.Item label="流转类型" name="transitionType">
              <Select placeholder="请选择" allowClear>
                <Option value="auto">自动</Option>
                <Option value="manual">手动</Option>
              </Select>
            </Form.Item>
          </Col>
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
        scroll={{ x: 2000 }}
      />

      {/* 详情弹窗 */}
      {detailVisible && (
        <div className="detail-modal-overlay" onClick={() => setDetailVisible(false)}>
          <div className="detail-modal" onClick={(e) => e.stopPropagation()}>
            <div className="detail-modal-header">
              <h3>流转日志详情</h3>
              <Button type="link" onClick={() => setDetailVisible(false)}>关闭</Button>
            </div>
            <div className="detail-modal-body">
              <Row gutter={[16, 16]}>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">流程实例ID：</span>
                    <span>{detailData?.processInstanceId}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">流程定义Key：</span>
                    <span>{detailData?.processDefKey}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">节点定义Key：</span>
                    <span>{detailData?.nodeDefKey}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">业务描述：</span>
                    <span>{detailData?.businessDescription}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">业务ID：</span>
                    <span>{detailData?.businessId}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">来源节点：</span>
                    <span>{detailData?.fromNode}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">目标节点：</span>
                    <span>{detailData?.toNode}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">流转类型：</span>
                    <Tag color={transitionTypeColorMap[detailData?.transitionType] || 'default'}>
                      {transitionTypeMap[detailData?.transitionType] || detailData?.transitionType}
                    </Tag>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">流转结果：</span>
                    <Tag color={transitionResultColorMap[detailData?.transitionResult] || 'default'}>
                      {transitionResultMap[detailData?.transitionResult] || detailData?.transitionResult}
                    </Tag>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">创建人ID：</span>
                    <span>{detailData?.createUserId}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">创建时间：</span>
                    <span>{detailData?.createTime}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">更新人ID：</span>
                    <span>{detailData?.updateUserId}</span>
                  </div>
                </Col>
                <Col span={12}>
                  <div className="detail-item">
                    <span className="detail-label">更新时间：</span>
                    <span>{detailData?.updateTime}</span>
                  </div>
                </Col>
              </Row>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TransitionFlowManagement

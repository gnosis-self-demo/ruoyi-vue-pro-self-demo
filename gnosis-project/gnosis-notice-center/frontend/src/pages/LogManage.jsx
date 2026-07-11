import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Form, Input, Select, Modal, Tag, message, Descriptions, DatePicker
} from 'antd'
import { ReloadOutlined, EyeOutlined } from '@ant-design/icons'
import { logApi } from '../api/logApi'
import dayjs from 'dayjs'

const { Option } = Select
const { RangePicker } = DatePicker

const LogManage = () => {
  const [loading, setLoading] = useState(false)
  const [dataSource, setDataSource] = useState([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [queryForm] = Form.useForm()
  const [detailVisible, setDetailVisible] = useState(false)
  const [detailRecord, setDetailRecord] = useState(null)

  const noticeTypeOptions = [
    { value: 'SMS', label: '短信' },
    { value: 'EMAIL', label: '邮件' },
    { value: 'INBOX', label: '站内信' },
    { value: 'WECHAT', label: '微信' },
  ]

  const statusOptions = [
    { value: 0, label: '待发送', color: 'default' },
    { value: 1, label: '发送中', color: 'processing' },
    { value: 2, label: '成功', color: 'success' },
    { value: 3, label: '失败', color: 'error' },
  ]

  const fetchData = async () => {
    setLoading(true)
    try {
      const values = queryForm.getFieldsValue()
      const params = { ...values, pageNum, pageSize }
      if (values.timeRange && values.timeRange.length === 2) {
        params.startTime = values.timeRange[0].format('YYYY-MM-DD HH:mm:ss')
        params.endTime = values.timeRange[1].format('YYYY-MM-DD HH:mm:ss')
        delete params.timeRange
      }
      const res = await logApi.pageList(params)
      setDataSource(res.data.list || [])
      setTotal(res.data.total || 0)
    } catch (error) {
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchData()
  }, [pageNum, pageSize])

  const handleSearch = () => {
    setPageNum(0)
    fetchData()
  }

  const handleReset = () => {
    queryForm.resetFields()
    setPageNum(0)
    fetchData()
  }

  const handleViewDetail = (record) => {
    setDetailRecord(record)
    setDetailVisible(true)
  }

  const handleRetry = async (id) => {
    try {
      await logApi.retry(id)
      message.success('重试成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const getNoticeTypeText = (type) => {
    const map = { SMS: '短信', EMAIL: '邮件', INBOX: '站内信', WECHAT: '微信' }
    return map[type] || type
  }

  const getNoticeTypeColor = (type) => {
    const map = { SMS: 'blue', EMAIL: 'green', INBOX: 'orange', WECHAT: 'cyan' }
    return map[type] || 'default'
  }

  const getStatusColor = (status) => {
    const map = { 0: 'default', 1: 'processing', 2: 'success', 3: 'error' }
    return map[status] || 'default'
  }

  const getStatusText = (status) => {
    const map = { 0: '待发送', 1: '发送中', 2: '成功', 3: '失败' }
    return map[status] || '未知'
  }

  const columns = [
    { title: '模板编码', dataIndex: 'templateCode', key: 'templateCode', width: 150 },
    {
      title: '通知类型',
      dataIndex: 'noticeType',
      key: 'noticeType',
      width: 100,
      render: (type) => <Tag color={getNoticeTypeColor(type)}>{getNoticeTypeText(type)}</Tag>,
    },
    { title: '接收人', dataIndex: 'receiver', key: 'receiver', width: 150 },
    { title: '消息主题', dataIndex: 'subject', key: 'subject', ellipsis: true },
    {
      title: '发送状态',
      dataIndex: 'sendStatus',
      key: 'sendStatus',
      width: 100,
      render: (status) => <Tag color={getStatusColor(status)}>{getStatusText(status)}</Tag>,
    },
    { title: '重试次数', dataIndex: 'retryCount', key: 'retryCount', width: 80 },
    {
      title: '发送时间',
      dataIndex: 'sendTime',
      key: 'sendTime',
      width: 160,
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    { title: '创建人', dataIndex: 'createUserId', key: 'createUserId', width: 100 },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 160,
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {record.sendStatus === 3 && (
            <Button type="link" size="small" icon={<ReloadOutlined />} onClick={() => handleRetry(record.id)}>
              重试
            </Button>
          )}
        </Space>
      ),
    },
  ]

  return (
    <div>
      <Form form={queryForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="templateCode">
          <Input placeholder="模板编码" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="noticeType">
          <Select placeholder="通知类型" style={{ width: 120 }} allowClear>
            {noticeTypeOptions.map((opt) => (
              <Option key={opt.value} value={opt.value}>{opt.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item name="receiver">
          <Input placeholder="接收人" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="sendStatus">
          <Select placeholder="发送状态" style={{ width: 120 }} allowClear>
            {statusOptions.map((opt) => (
              <Option key={opt.value} value={opt.value}>{opt.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item name="timeRange">
          <RangePicker showTime format="YYYY-MM-DD HH:mm:ss" />
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" onClick={handleSearch}>查询</Button>
            <Button onClick={handleReset}>重置</Button>
          </Space>
        </Form.Item>
      </Form>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        scroll={{ x: 1400 }}
        pagination={{
          current: pageNum + 1,
          pageSize,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => {
            setPageNum(page - 1)
            setPageSize(size)
          },
        }}
      />

      <Modal
        title="日志详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
      >
        {detailRecord && (
          <Descriptions column={2} bordered>
            <Descriptions.Item label="模板编码">{detailRecord.templateCode}</Descriptions.Item>
            <Descriptions.Item label="通知类型">
              <Tag color={getNoticeTypeColor(detailRecord.noticeType)}>
                {getNoticeTypeText(detailRecord.noticeType)}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="接收人" span={2}>{detailRecord.receiver}</Descriptions.Item>
            <Descriptions.Item label="消息主题" span={2}>{detailRecord.subject}</Descriptions.Item>
            <Descriptions.Item label="发送状态">
              <Tag color={getStatusColor(detailRecord.sendStatus)}>
                {getStatusText(detailRecord.sendStatus)}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="重试次数">
              {detailRecord.retryCount} / {detailRecord.maxRetry}
            </Descriptions.Item>
            <Descriptions.Item label="请求ID">{detailRecord.requestId || '-'}</Descriptions.Item>
            <Descriptions.Item label="发送时间">
              {detailRecord.sendTime ? dayjs(detailRecord.sendTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="发送内容" span={2}>
              <div style={{ maxHeight: 200, overflow: 'auto', whiteSpace: 'pre-wrap' }}>
                {detailRecord.content}
              </div>
            </Descriptions.Item>
            {detailRecord.errorMsg && (
              <Descriptions.Item label="错误信息" span={2}>
                <span style={{ color: 'red' }}>{detailRecord.errorMsg}</span>
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}

export default LogManage

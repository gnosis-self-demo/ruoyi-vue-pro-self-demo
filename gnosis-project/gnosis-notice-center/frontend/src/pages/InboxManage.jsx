import React, { useState, useEffect } from 'react'
import {
  Table, Button, Space, Form, Input, Select, Modal, Popconfirm, message, Tag, Descriptions, DatePicker
} from 'antd'
import { PlusOutlined, DeleteOutlined, CheckOutlined, EyeOutlined } from '@ant-design/icons'
import { inboxApi } from '../api/inboxApi'
import dayjs from 'dayjs'

const { Option } = Select
const { TextArea } = Input
const { RangePicker } = DatePicker

const InboxManage = () => {
  const [loading, setLoading] = useState(false)
  const [dataSource, setDataSource] = useState([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [queryForm] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [detailVisible, setDetailVisible] = useState(false)
  const [detailRecord, setDetailRecord] = useState(null)
  const [form] = Form.useForm()
  const [selectedRowKeys, setSelectedRowKeys] = useState([])

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
      const res = await inboxApi.pageList(params)
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

  const handleCreate = () => {
    form.resetFields()
    setModalVisible(true)
  }

  const handleViewDetail = (record) => {
    setDetailRecord(record)
    setDetailVisible(true)
    // 自动标记已读
    if (record.isRead === 0) {
      handleMarkRead(record.id)
    }
  }

  const handleMarkRead = async (id) => {
    try {
      await inboxApi.markRead(id)
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleBatchMarkRead = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要标记的数据')
      return
    }
    try {
      await inboxApi.batchMarkRead(selectedRowKeys)
      message.success('批量标记已读成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleDelete = async (id) => {
    try {
      await inboxApi.delete(id)
      message.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据')
      return
    }
    try {
      await inboxApi.batchDelete(selectedRowKeys)
      message.success('批量删除成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      const userIds = values.userIds.split(',').map((id) => id.trim()).filter(Boolean)
      await inboxApi.create({
        userIds,
        subject: values.subject,
        content: values.content,
      })
      message.success('发送成功')
      setModalVisible(false)
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const columns = [
    { title: '接收用户', dataIndex: 'userId', key: 'userId', width: 150 },
    { title: '消息主题', dataIndex: 'subject', key: 'subject', ellipsis: true },
    {
      title: '是否已读',
      dataIndex: 'isRead',
      key: 'isRead',
      width: 100,
      render: (isRead) => (
        <Tag color={isRead === 1 ? 'green' : 'orange'}>
          {isRead === 1 ? '已读' : '未读'}
        </Tag>
      ),
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
      title: '读取时间',
      dataIndex: 'readTime',
      key: 'readTime',
      width: 160,
      render: (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          {record.isRead === 0 && (
            <Button type="link" size="small" icon={<CheckOutlined />} onClick={() => handleMarkRead(record.id)}>
              标记已读
            </Button>
          )}
          <Popconfirm title="确定删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  const rowSelection = {
    selectedRowKeys,
    onChange: setSelectedRowKeys,
  }

  return (
    <div>
      <Form form={queryForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="userId">
          <Input placeholder="接收用户ID" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="subject">
          <Input placeholder="消息主题" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="isRead">
          <Select placeholder="是否已读" style={{ width: 120 }} allowClear>
            <Option value={1}>已读</Option>
            <Option value={0}>未读</Option>
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

      <Space style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
          发送站内信
        </Button>
        <Button icon={<CheckOutlined />} onClick={handleBatchMarkRead}>批量已读</Button>
        <Popconfirm title="确定批量删除？" onConfirm={handleBatchDelete}>
          <Button danger icon={<DeleteOutlined />}>批量删除</Button>
        </Popconfirm>
      </Space>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        rowSelection={rowSelection}
        scroll={{ x: 1200 }}
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
        title="发送站内信"
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="userIds"
            label="接收用户ID"
            rules={[{ required: true, message: '请输入接收用户ID' }]}
            extra="多个用户ID用逗号分隔"
          >
            <Input placeholder="请输入接收用户ID，多个用逗号分隔" />
          </Form.Item>
          <Form.Item
            name="subject"
            label="消息主题"
            rules={[{ required: true, message: '请输入消息主题' }]}
          >
            <Input placeholder="请输入消息主题" />
          </Form.Item>
          <Form.Item
            name="content"
            label="消息内容"
            rules={[{ required: true, message: '请输入消息内容' }]}
          >
            <TextArea rows={6} placeholder="请输入消息内容" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="站内信详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
      >
        {detailRecord && (
          <Descriptions column={2} bordered>
            <Descriptions.Item label="接收用户">{detailRecord.userId}</Descriptions.Item>
            <Descriptions.Item label="是否已读">
              <Tag color={detailRecord.isRead === 1 ? 'green' : 'orange'}>
                {detailRecord.isRead === 1 ? '已读' : '未读'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="消息主题" span={2}>{detailRecord.subject}</Descriptions.Item>
            <Descriptions.Item label="消息内容" span={2}>
              <div style={{ maxHeight: 200, overflow: 'auto', whiteSpace: 'pre-wrap' }}>
                {detailRecord.content}
              </div>
            </Descriptions.Item>
            <Descriptions.Item label="创建人">{detailRecord.createUserId}</Descriptions.Item>
            <Descriptions.Item label="读取时间">
              {detailRecord.readTime ? dayjs(detailRecord.readTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>
            <Descriptions.Item label="创建时间" span={2}>
              {detailRecord.createTime ? dayjs(detailRecord.createTime).format('YYYY-MM-DD HH:mm:ss') : '-'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}

export default InboxManage

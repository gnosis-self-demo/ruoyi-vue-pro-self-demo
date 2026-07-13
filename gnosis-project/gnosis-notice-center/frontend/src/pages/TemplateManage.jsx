import React, { useState, useEffect, useRef } from 'react'
import {
  Table, Button, Space, Form, Input, Select, Modal, Popconfirm, message, Tag, Upload
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined, CheckCircleOutlined, StopOutlined,
  ExportOutlined, ImportOutlined, DownloadOutlined
} from '@ant-design/icons'
import { templateApi } from '../api/templateApi'
import dayjs from 'dayjs'

const { Option } = Select
const { TextArea } = Input

const TemplateManage = () => {
  const [loading, setLoading] = useState(false)
  const [dataSource, setDataSource] = useState([])
  const [total, setTotal] = useState(0)
  const [pageNum, setPageNum] = useState(0)
  const [pageSize, setPageSize] = useState(10)
  const [queryForm] = Form.useForm()
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState(null)
  const [form] = Form.useForm()
  const [selectedRowKeys, setSelectedRowKeys] = useState([])
  const importRef = useRef(null)

  const noticeTypeOptions = [
    { value: 'SMS', label: '短信' },
    { value: 'EMAIL', label: '邮件' },
    { value: 'INBOX', label: '站内信' },
    { value: 'WECHAT', label: '微信' },
  ]

  const templateTypeOptions = [
    { value: 'TEXT', label: '纯文本' },
    { value: 'RICH_TEXT', label: '富文本' },
    { value: 'HTML', label: 'HTML' },
  ]

  const fetchData = async () => {
    setLoading(true)
    try {
      const values = queryForm.getFieldsValue()
      const res = await templateApi.pageList({
        ...values,
        pageNum,
        pageSize,
      })
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
    setEditingRecord(null)
    form.resetFields()
    form.setFieldsValue({ status: 1, templateType: 'TEXT', noticeType: 'SMS' })
    setModalVisible(true)
  }

  const handleEdit = (record) => {
    setEditingRecord(record)
    form.setFieldsValue(record)
    setModalVisible(true)
  }

  const handleDelete = async (id) => {
    try {
      await templateApi.delete(id)
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
      await templateApi.batchDelete(selectedRowKeys)
      message.success('批量删除成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleBatchEnable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要启用的数据')
      return
    }
    try {
      await templateApi.batchEnable(selectedRowKeys)
      message.success('批量启用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  const handleBatchDisable = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要禁用的数据')
      return
    }
    try {
      await templateApi.batchDisable(selectedRowKeys)
      message.success('批量禁用成功')
      setSelectedRowKeys([])
      fetchData()
    } catch (error) {
      console.error(error)
    }
  }

  // 导出模板
  const handleExport = async () => {
    try {
      const values = queryForm.getFieldsValue()
      const res = await templateApi.export(values)
      const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `消息模板_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      message.success('导出成功')
    } catch (error) {
      console.error(error)
      message.error('导出失败')
    }
  }

  // 下载导入模板
  const handleDownloadTemplate = () => {
    const headers = ['模板编码', '模板名称', '模板类型', '通知类型', '消息主题', '模板内容', '备注']
    const example = [
      ['SMS_001', '验证码短信', 'TEXT', 'SMS', '验证码通知', '您的验证码是：${code}', '示例模板'],
      ['EMAIL_001', '订单通知', 'HTML', 'EMAIL', '订单创建通知', '<h1>您的订单已创建</h1>', '示例模板'],
    ]
    const csvContent = [headers.join(','), ...example.map(row => row.join(','))].join('\n')
    const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '消息模板导入模板.csv'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }

  // 导入模板
  const handleImport = async (file) => {
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await templateApi.import(formData)
      message.success(`导入成功，共导入 ${res.data} 条数据`)
      fetchData()
    } catch (error) {
      console.error(error)
      message.error('导入失败')
    }
    return false
  }

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields()
      if (editingRecord) {
        await templateApi.update({ ...values, id: editingRecord.id })
        message.success('更新成功')
      } else {
        await templateApi.create(values)
        message.success('创建成功')
      }
      setModalVisible(false)
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

  const columns = [
    { title: '模板编码', dataIndex: 'templateCode', key: 'templateCode', width: 150 },
    { title: '模板名称', dataIndex: 'templateName', key: 'templateName', width: 150 },
    {
      title: '通知类型',
      dataIndex: 'noticeType',
      key: 'noticeType',
      width: 100,
      render: (type) => <Tag color={getNoticeTypeColor(type)}>{getNoticeTypeText(type)}</Tag>,
    },
    {
      title: '模板类型',
      dataIndex: 'templateType',
      key: 'templateType',
      width: 100,
    },
    { title: '消息主题', dataIndex: 'subject', key: 'subject', ellipsis: true },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status) => (
        <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>
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
    { title: '更新人', dataIndex: 'updateUserId', key: 'updateUserId', width: 100 },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
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
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
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
        <Form.Item name="templateCode">
          <Input placeholder="模板编码" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="templateName">
          <Input placeholder="模板名称" style={{ width: 150 }} />
        </Form.Item>
        <Form.Item name="noticeType">
          <Select placeholder="通知类型" style={{ width: 120 }} allowClear>
            {noticeTypeOptions.map((opt) => (
              <Option key={opt.value} value={opt.value}>{opt.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item name="templateType">
          <Select placeholder="模板类型" style={{ width: 120 }} allowClear>
            {templateTypeOptions.map((opt) => (
              <Option key={opt.value} value={opt.value}>{opt.label}</Option>
            ))}
          </Select>
        </Form.Item>
        <Form.Item name="status">
          <Select placeholder="状态" style={{ width: 100 }} allowClear>
            <Option value={1}>启用</Option>
            <Option value={0}>禁用</Option>
          </Select>
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
          新建
        </Button>
        <Popconfirm title="确定批量删除？" onConfirm={handleBatchDelete}>
          <Button danger icon={<DeleteOutlined />}>批量删除</Button>
        </Popconfirm>
        <Button icon={<CheckCircleOutlined />} onClick={handleBatchEnable}>批量启用</Button>
        <Button icon={<StopOutlined />} onClick={handleBatchDisable}>批量禁用</Button>
        <Button icon={<ExportOutlined />} onClick={handleExport}>导出</Button>
        <Upload
          accept=".xlsx,.xls"
          showUploadList={false}
          beforeUpload={handleImport}
          ref={importRef}
        >
          <Button icon={<ImportOutlined />}>导入</Button>
        </Upload>
        <Button icon={<DownloadOutlined />} onClick={handleDownloadTemplate}>导入模板</Button>
      </Space>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        rowSelection={rowSelection}
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
        title={editingRecord ? '编辑模板' : '新建模板'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={700}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="templateCode"
            label="模板编码"
            rules={[{ required: true, message: '请输入模板编码' }]}
          >
            <Input placeholder="请输入模板编码" disabled={!!editingRecord} />
          </Form.Item>
          <Form.Item
            name="templateName"
            label="模板名称"
            rules={[{ required: true, message: '请输入模板名称' }]}
          >
            <Input placeholder="请输入模板名称" />
          </Form.Item>
          <Form.Item
            name="noticeType"
            label="通知类型"
            rules={[{ required: true, message: '请选择通知类型' }]}
          >
            <Select placeholder="请选择通知类型">
              {noticeTypeOptions.map((opt) => (
                <Option key={opt.value} value={opt.value}>{opt.label}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="templateType"
            label="模板类型"
            rules={[{ required: true, message: '请选择模板类型' }]}
          >
            <Select placeholder="请选择模板类型">
              {templateTypeOptions.map((opt) => (
                <Option key={opt.value} value={opt.value}>{opt.label}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="subject" label="消息主题">
            <Input placeholder="请输入消息主题" />
          </Form.Item>
          <Form.Item
            name="content"
            label="模板内容"
            rules={[{ required: true, message: '请输入模板内容' }]}
            extra="支持变量占位符格式：${variableName}"
          >
            <TextArea rows={6} placeholder="请输入模板内容" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
          <Form.Item name="status" label="状态" initialValue={1}>
            <Select>
              <Option value={1}>启用</Option>
              <Option value={0}>禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default TemplateManage

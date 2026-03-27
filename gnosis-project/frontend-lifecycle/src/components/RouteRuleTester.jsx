import React, { useState } from 'react'
import { Card, Form, Input, Button, Space, Table, Tag, message } from 'antd'
import { PlayCircleOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons'
import { testRouteRule } from '../api/eventApi'

/**
 * 规则测试器组件
 * 用于测试路由规则的匹配情况
 */
const RouteRuleTester = ({ rules = [], metadataFields = [] }) => {
  const [form] = Form.useForm()
  const [testing, setTesting] = useState(false)
  const [testResult, setTestResult] = useState(null)

  // 处理测试执行
  const handleTest = async () => {
    try {
      const values = await form.validateFields()
      setTesting(true)
      
      const response = await testRouteRule({
        eventData: values,
        rules: rules.map(r => ({
          id: r.id,
          name: r.name,
          conditions: r.conditions
        }))
      })

      setTestResult(response.data)
      message.success('测试完成')
    } catch (error) {
      console.error('测试失败:', error)
    } finally {
      setTesting(false)
    }
  }

  // 渲染测试结果
  const renderTestResult = () => {
    if (!testResult) return null

    const { matchedRules, unmatchedRules, targetState } = testResult

    return (
      <Card title="测试结果" size="small" style={{ marginTop: 16 }}>
        <Space direction="vertical" style={{ width: '100%' }} size="small">
          {matchedRules.length > 0 && (
            <div>
              <Tag icon={<CheckCircleOutlined />} color="success">
                匹配成功
              </Tag>
              <span style={{ marginLeft: 8 }}>
                匹配到 {matchedRules.length} 条规则
              </span>
              <div style={{ marginTop: 8 }}>
                {matchedRules.map((rule, index) => (
                  <Tag key={index} color="blue" style={{ marginBottom: 4 }}>
                    {rule.name}
                  </Tag>
                ))}
              </div>
            </div>
          )}

          {unmatchedRules.length > 0 && (
            <div>
              <Tag icon={<CloseCircleOutlined />} color="default">
                未匹配
              </Tag>
              <span style={{ marginLeft: 8 }}>
                未匹配到 {unmatchedRules.length} 条规则
              </span>
              <div style={{ marginTop: 8 }}>
                {unmatchedRules.map((rule, index) => (
                  <Tag key={index} color="default" style={{ marginBottom: 4 }}>
                    {rule.name}
                  </Tag>
                ))}
              </div>
            </div>
          )}

          {targetState && (
            <div>
              <strong>目标状态：</strong>
              <Tag color="processing">{targetState}</Tag>
            </div>
          )}
        </Space>
      </Card>
    )
  }

  // 测试数据输入表单列
  const testInputColumns = [
    {
      title: '字段名',
      dataIndex: 'name',
      key: 'name',
      width: 200
    },
    {
      title: '字段类型',
      dataIndex: 'type',
      key: 'type',
      width: 120,
      render: (type) => <Tag>{type}</Tag>
    },
    {
      title: '测试值',
      dataIndex: 'testValue',
      key: 'testValue',
      render: (_, record) => (
        <Form.Item
          name={[record.name]}
          noStyle
        >
          <Input placeholder="输入测试值" size="small" />
        </Form.Item>
      )
    }
  ]

  return (
    <Card 
      title="规则测试沙箱" 
      size="small"
      extra={
        <Button
          type="primary"
          icon={<PlayCircleOutlined />}
          onClick={handleTest}
          loading={testing}
          size="small"
        >
          执行测试
        </Button>
      }
    >
      <Form form={form} layout="vertical">
        <h4 style={{ marginBottom: 12 }}>测试数据输入</h4>
        <Table
          columns={testInputColumns}
          dataSource={metadataFields}
          rowKey="name"
          pagination={false}
          size="small"
          scroll={{ y: 300 }}
        />

        {renderTestResult()}
      </Form>
    </Card>
  )
}

export default RouteRuleTester

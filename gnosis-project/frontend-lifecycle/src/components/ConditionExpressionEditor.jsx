import React, { useState } from 'react'
import { Card, Form, Select, Input, InputNumber, Space, Button, Divider } from 'antd'
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons'

const { Option } = Select

/**
 * 条件表达式编辑器组件
 * 支持 AND/OR 逻辑组合的条件表达式编辑
 */
const ConditionExpressionEditor = ({ value = [], onChange, metadataFields = [] }) => {
  // 条件组结构：{ logic: 'AND' | 'OR', conditions: [] }
  const [conditionGroups, setConditionGroups] = useState(value.length > 0 ? value : [{
    logic: 'AND',
    conditions: []
  }])

  // 操作符选项
  const operators = [
    { label: '等于', value: '=' },
    { label: '不等于', value: '!=' },
    { label: '大于', value: '>' },
    { label: '大于等于', value: '>=' },
    { label: '小于', value: '<' },
    { label: '小于等于', value: '<=' },
    { label: '包含', value: 'in' },
    { label: '不包含', value: 'not in' },
    { label: '为空', value: 'is null' },
    { label: '不为空', value: 'is not null' },
    { label: '匹配正则', value: 'regex' }
  ]

  // 添加条件组
  const handleAddGroup = () => {
    const newGroups = [...conditionGroups, {
      logic: 'AND',
      conditions: []
    }]
    setConditionGroups(newGroups)
    onChange?.(newGroups)
  }

  // 删除条件组
  const handleDeleteGroup = (groupIndex) => {
    if (conditionGroups.length === 1) {
      // 至少保留一个组
      const newGroups = [{
        logic: 'AND',
        conditions: []
      }]
      setConditionGroups(newGroups)
      onChange?.(newGroups)
    } else {
      const newGroups = conditionGroups.filter((_, index) => index !== groupIndex)
      setConditionGroups(newGroups)
      onChange?.(newGroups)
    }
  }

  // 更新条件组逻辑
  const handleUpdateGroupLogic = (groupIndex, logic) => {
    const newGroups = [...conditionGroups]
    newGroups[groupIndex].logic = logic
    setConditionGroups(newGroups)
    onChange?.(newGroups)
  }

  // 添加条件
  const handleAddCondition = (groupIndex) => {
    const newGroups = [...conditionGroups]
    newGroups[groupIndex].conditions.push({
      field: '',
      operator: '=',
      value: ''
    })
    setConditionGroups(newGroups)
    onChange?.(newGroups)
  }

  // 删除条件
  const handleDeleteCondition = (groupIndex, conditionIndex) => {
    const newGroups = [...conditionGroups]
    newGroups[groupIndex].conditions = newGroups[groupIndex].conditions.filter(
      (_, index) => index !== conditionIndex
    )
    setConditionGroups(newGroups)
    onChange?.(newGroups)
  }

  // 更新条件
  const handleUpdateCondition = (groupIndex, conditionIndex, field, val) => {
    const newGroups = [...conditionGroups]
    newGroups[groupIndex].conditions[conditionIndex][field] = val
    setConditionGroups(newGroups)
    onChange?.(newGroups)
  }

  return (
    <Card title="条件表达式配置" size="small">
      {conditionGroups.map((group, groupIndex) => (
        <div key={groupIndex} style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: 8 }}>
            <span style={{ marginRight: 8 }}>条件组 {groupIndex + 1}:</span>
            <Select
              value={group.logic}
              onChange={(val) => handleUpdateGroupLogic(groupIndex, val)}
              style={{ width: 100, marginRight: 8 }}
              size="small"
            >
              <Option value="AND">AND (与)</Option>
              <Option value="OR">OR (或)</Option>
            </Select>
            <Button
              type="link"
              danger
              icon={<DeleteOutlined />}
              onClick={() => handleDeleteGroup(groupIndex)}
              size="small"
              disabled={conditionGroups.length === 1}
            >
              删除组
            </Button>
          </div>

          <div style={{ marginLeft: 24 }}>
            {group.conditions.map((condition, conditionIndex) => (
              <Space key={conditionIndex} style={{ marginBottom: 8 }} wrap>
                <span style={{ fontSize: 12, color: '#999' }}>
                  {conditionIndex > 0 ? (group.logic === 'AND' ? '且' : '或') : '如果'}
                </span>
                
                <Select
                  placeholder="选择字段"
                  value={condition.field}
                  onChange={(val) => handleUpdateCondition(groupIndex, conditionIndex, 'field', val)}
                  style={{ width: 180 }}
                  size="small"
                  allowClear
                >
                  {metadataFields.map((field) => (
                    <Option key={field.name} value={field.name}>
                      {field.label || field.name} ({field.type})
                    </Option>
                  ))}
                </Select>

                <Select
                  placeholder="操作符"
                  value={condition.operator}
                  onChange={(val) => handleUpdateCondition(groupIndex, conditionIndex, 'operator', val)}
                  style={{ width: 120 }}
                  size="small"
                  disabled={['is null', 'is not null'].includes(condition.operator)}
                >
                  {operators.map((op) => (
                    <Option key={op.value} value={op.value}>
                      {op.label}
                    </Option>
                  ))}
                </Select>

                {!['is null', 'is not null'].includes(condition.operator) && (
                  <Input
                    placeholder="输入值"
                    value={condition.value}
                    onChange={(e) => handleUpdateCondition(groupIndex, conditionIndex, 'value', e.target.value)}
                    style={{ width: 200 }}
                    size="small"
                  />
                )}

                <Button
                  type="link"
                  danger
                  icon={<DeleteOutlined />}
                  onClick={() => handleDeleteCondition(groupIndex, conditionIndex)}
                  size="small"
                />
              </Space>
            ))}

            <Button
              type="dashed"
              icon={<PlusOutlined />}
              onClick={() => handleAddCondition(groupIndex)}
              size="small"
              style={{ marginTop: 8 }}
            >
              添加条件
            </Button>
          </div>

          {groupIndex < conditionGroups.length - 1 && <Divider style={{ margin: '12px 0' }} />}
        </div>
      ))}

      <Divider />

      <Space>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleAddGroup}
          size="small"
        >
          添加条件组
        </Button>
      </Space>
    </Card>
  )
}

export default ConditionExpressionEditor

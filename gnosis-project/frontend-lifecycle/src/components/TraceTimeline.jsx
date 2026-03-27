import React from 'react'
import { Card, Timeline, Tag, Space, Descriptions, Tooltip } from 'antd'
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  SyncOutlined,
  ClockCircleOutlined
} from '@ant-design/icons'

/**
 * 追踪时间轴组件
 * 展示生命周期实例的流转历史
 */
const TraceTimeline = ({ events = [] }) => {
  // 渲染事件内容
  const renderEventContent = (event) => {
    const { eventType, eventData, currentState, previousState, errorMessage, timestamp } = event

    return (
      <div style={{ fontSize: 14 }}>
        <div style={{ marginBottom: 8 }}>
          <Tag color="blue">{eventType}</Tag>
          <span style={{ marginLeft: 8, color: '#999' }}>
            {timestamp}
          </span>
        </div>

        <div style={{ marginBottom: 8 }}>
          {previousState && (
            <span>
              从 <Tag color="default">{previousState}</Tag>
            </span>
          )}
          {currentState && (
            <span style={{ marginLeft: 8 }}>
              到 <Tag color="processing">{currentState}</Tag>
            </span>
          )}
        </div>

        {errorMessage ? (
          <div style={{ color: '#ff4d4f', fontSize: 12 }}>
            <CloseCircleOutlined /> 错误：{errorMessage}
          </div>
        ) : (
          <div style={{ color: '#52c41a', fontSize: 12 }}>
            <CheckCircleOutlined /> 执行成功
          </div>
        )}

        {eventData && Object.keys(eventData).length > 0 && (
          <div style={{ marginTop: 8, fontSize: 12 }}>
            <Descriptions
              title="事件数据"
              size="small"
              bordered
              column={2}
            >
              {Object.entries(eventData).map(([key, value]) => (
                <Descriptions.Item key={key} label={key}>
                  {typeof value === 'object' ? JSON.stringify(value) : String(value)}
                </Descriptions.Item>
              ))}
            </Descriptions>
          </div>
        )}
      </div>
    )
  }

  // 获取事件状态图标
  const getEventIcon = (event) => {
    if (event.errorMessage) {
      return <CloseCircleOutlined style={{ color: '#ff4d4f' }} />
    }
    
    switch (event.eventType) {
      case 'CREATE':
        return <ClockCircleOutlined style={{ color: '#1890ff' }} />
      case 'TRANSITION':
        return <SyncOutlined spin style={{ color: '#13c2c2' }} />
      case 'COMPLETE':
        return <CheckCircleOutlined style={{ color: '#52c41a' }} />
      default:
        return <ClockCircleOutlined style={{ color: '#722ed1' }} />
    }
  }

  // 获取事件颜色
  const getEventColor = (event) => {
    if (event.errorMessage) {
      return 'red'
    }
    
    switch (event.eventType) {
      case 'CREATE':
        return 'blue'
      case 'TRANSITION':
        return 'cyan'
      case 'COMPLETE':
        return 'green'
      default:
        return 'purple'
    }
  }

  // 按时间排序事件
  const sortedEvents = [...events].sort((a, b) => 
    new Date(b.timestamp) - new Date(a.timestamp)
  )

  return (
    <Card title="流转历史" size="small">
      <Timeline
        mode="left"
        items={sortedEvents.map((event, index) => ({
          key: event.id || index,
          color: getEventColor(event),
          dot: getEventIcon(event),
          children: renderEventContent(event)
        }))}
      />
    </Card>
  )
}

export default TraceTimeline

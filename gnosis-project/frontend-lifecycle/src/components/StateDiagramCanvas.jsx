import React, { useRef, useState, useCallback } from 'react'
import { Card, Button, Space, Tooltip } from 'antd'
import { PlusOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'

/**
 * 状态图绘制画布组件
 * 使用 SVG 绘制状态节点和流转关系
 */
const StateDiagramCanvas = ({ 
  nodes = [], 
  edges = [], 
  onNodeAdd, 
  onNodeDelete, 
  onNodeUpdate,
  onEdgeAdd,
  readOnly = false 
}) => {
  const svgRef = useRef(null)
  const [selectedNode, setSelectedNode] = useState(null)
  const [draggingNode, setDraggingNode] = useState(null)
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 })

  // 处理节点点击
  const handleNodeClick = useCallback((node, e) => {
    e.stopPropagation()
    if (!readOnly) {
      setSelectedNode(node)
    }
  }, [readOnly])

  // 处理节点拖拽开始
  const handleNodeDragStart = useCallback((node, e) => {
    if (readOnly) return
    e.stopPropagation()
    const rect = e.currentTarget.getBoundingClientRect()
    setDragOffset({
      x: e.clientX - rect.left,
      y: e.clientY - rect.top
    })
    setDraggingNode(node)
  }, [readOnly])

  // 处理画布鼠标移动
  const handleMouseMove = useCallback((e) => {
    if (!draggingNode || !svgRef.current) return
    
    const svgRect = svgRef.current.getBoundingClientRect()
    const newX = e.clientX - svgRect.left - dragOffset.x
    const newY = e.clientY - svgRect.top - dragOffset.y
    
    onNodeUpdate?.({
      ...draggingNode,
      x: Math.max(0, newX),
      y: Math.max(0, newY)
    })
  }, [draggingNode, dragOffset, onNodeUpdate])

  // 处理画布鼠标松开
  const handleMouseUp = useCallback(() => {
    setDraggingNode(null)
  }, [])

  // 处理画布点击
  const handleCanvasClick = useCallback((e) => {
    if (readOnly) return
    setSelectedNode(null)
    
    // 如果在空白处点击，可以添加新节点
    if (onNodeAdd && e.target.tagName === 'svg') {
      const svgRect = svgRef.current.getBoundingClientRect()
      onNodeAdd({
        id: `node_${Date.now()}`,
        x: e.clientX - svgRect.left,
        y: e.clientY - svgRect.top,
        label: '新状态',
        type: 'normal'
      })
    }
  }, [readOnly, onNodeAdd])

  // 渲染边（连接线）
  const renderEdges = () => {
    return edges.map((edge, index) => {
      const sourceNode = nodes.find(n => n.id === edge.source)
      const targetNode = nodes.find(n => n.id === edge.target)
      
      if (!sourceNode || !targetNode) return null

      // 计算连线路径（贝塞尔曲线）
      const startX = sourceNode.x + 100
      const startY = sourceNode.y + 30
      const endX = targetNode.x
      const endY = targetNode.y + 30
      
      const controlPoint1X = startX + (endX - startX) / 2
      const controlPoint1Y = startY
      const controlPoint2X = startX + (endX - startX) / 2
      const controlPoint2Y = endY

      const pathD = `M ${startX} ${startY} C ${controlPoint1X} ${controlPoint1Y}, ${controlPoint2X} ${controlPoint2Y}, ${endX} ${endY}`

      return (
        <g key={edge.id || index}>
          <path
            d={pathD}
            stroke="#999"
            strokeWidth="2"
            fill="none"
            markerEnd="url(#arrowhead)"
          />
          {edge.label && (
            <text
              x={(startX + endX) / 2}
              y={(startY + endY) / 2 - 5}
              textAnchor="middle"
              fontSize="12"
              fill="#666"
            >
              {edge.label}
            </text>
          )}
        </g>
      )
    })
  }

  // 渲染节点
  const renderNodes = () => {
    return nodes.map((node) => {
      const isSelected = selectedNode?.id === node.id
      const isFinalState = node.type === 'final'
      
      return (
        <g
          key={node.id}
          onClick={(e) => handleNodeClick(node, e)}
          onMouseDown={(e) => handleNodeDragStart(node, e)}
          style={{ cursor: readOnly ? 'default' : 'move' }}
        >
          {/* 节点背景 */}
          <rect
            x={node.x}
            y={node.y}
            width="100"
            height="60"
            rx={isFinalState ? 30 : 4}
            ry={isFinalState ? 30 : 4}
            fill={isFinalState ? '#ff4d4f' : '#1890ff'}
            stroke={isSelected ? '#faad14' : '#096dd9'}
            strokeWidth={isSelected ? 3 : 1}
            filter="drop-shadow(2px 2px 2px rgba(0,0,0,0.2))"
          />
          
          {/* 节点文本 */}
          <text
            x={node.x + 50}
            y={node.y + 35}
            textAnchor="middle"
            fill="#fff"
            fontSize="14"
            fontWeight="bold"
          >
            {node.label}
          </text>

          {/* 操作按钮（仅在选中且非只读时显示） */}
          {!readOnly && isSelected && (
            <g>
              <circle
                cx={node.x + 85}
                cy={node.y - 10}
                r="12"
                fill="#52c41a"
                onClick={(e) => {
                  e.stopPropagation()
                  // 可以添加编辑逻辑
                }}
                style={{ cursor: 'pointer' }}
              />
              <text
                x={node.x + 85}
                y={node.y - 6}
                textAnchor="middle"
                fill="#fff"
                fontSize="16"
              >
                ✎
              </text>
              
              <circle
                cx={node.x + 110}
                cy={node.y - 10}
                r="12"
                fill="#ff4d4f"
                onClick={(e) => {
                  e.stopPropagation()
                  onNodeDelete?.(node)
                }}
                style={{ cursor: 'pointer' }}
              />
              <text
                x={node.x + 110}
                y={node.y - 6}
                textAnchor="middle"
                fill="#fff"
                fontSize="16"
              >
                ×
              </text>
            </g>
          )}
        </g>
      )
    })
  }

  return (
    <Card
      title="状态模型图"
      extra={
        !readOnly && (
          <Space>
            <Tooltip title="添加起始状态">
              <Button 
                icon={<PlusOutlined />} 
                onClick={() => onNodeAdd?.({
                  id: `node_${Date.now()}`,
                  x: 50,
                  y: 50,
                  label: '起始状态',
                  type: 'start'
                })}
              >
                起始状态
              </Button>
            </Tooltip>
            <Tooltip title="添加普通状态">
              <Button 
                icon={<PlusOutlined />}
                onClick={() => onNodeAdd?.({
                  id: `node_${Date.now()}`,
                  x: 200,
                  y: 50,
                  label: '新状态',
                  type: 'normal'
                })}
              >
                普通状态
              </Button>
            </Tooltip>
            <Tooltip title="添加终态">
              <Button 
                icon={<PlusOutlined />}
                onClick={() => onNodeAdd?.({
                  id: `node_${Date.now()}`,
                  x: 350,
                  y: 50,
                  label: '终态',
                  type: 'final'
                })}
              >
                终态
              </Button>
            </Tooltip>
          </Space>
        )
      }
      bodyStyle={{ padding: 0 }}
    >
      <svg
        ref={svgRef}
        width="100%"
        height="600"
        onClick={handleCanvasClick}
        onMouseMove={handleMouseMove}
        onMouseUp={handleMouseUp}
        onMouseLeave={handleMouseUp}
        style={{ background: '#f5f5f5' }}
      >
        {/* 定义箭头 */}
        <defs>
          <marker
            id="arrowhead"
            markerWidth="10"
            markerHeight="7"
            refX="9"
            refY="3.5"
            orient="auto"
          >
            <polygon
              points="0 0, 10 3.5, 0 7"
              fill="#999"
            />
          </marker>
        </defs>
        
        {/* 绘制连接线 */}
        {renderEdges()}
        
        {/* 绘制节点 */}
        {renderNodes()}
      </svg>
    </Card>
  )
}

export default StateDiagramCanvas

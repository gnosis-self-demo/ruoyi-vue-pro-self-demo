import React from 'react'
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  AppstoreOutlined,
  LinkOutlined,
  ApartmentOutlined,
  ThunderboltOutlined,
  HistoryOutlined
} from '@ant-design/icons'
import BusinessConfigManagement from './pages/BusinessConfigManagement'
import ResourceBindingManagement from './pages/ResourceBindingManagement'
import OrchestrationConfigManagement from './pages/OrchestrationConfigManagement'
import EventConfigManagement from './pages/EventConfigManagement'
import TransitionFlowManagement from './pages/TransitionFlowManagement'

const { Header, Content, Sider } = Layout

const menuItems = [
  {
    key: '/business-config',
    icon: <AppstoreOutlined />,
    label: <Link to="/business-config">业务配置管理</Link>
  },
  {
    key: '/resource-binding',
    icon: <LinkOutlined />,
    label: <Link to="/resource-binding">资源绑定管理</Link>
  },
  {
    key: '/orchestration-config',
    icon: <ApartmentOutlined />,
    label: <Link to="/orchestration-config">编排配置管理</Link>
  },
  {
    key: '/event-config',
    icon: <ThunderboltOutlined />,
    label: <Link to="/event-config">事件配置管理</Link>
  },
  {
    key: '/transition-flow',
    icon: <HistoryOutlined />,
    label: <Link to="/transition-flow">流转日志管理</Link>
  }
]

const Navigation = () => {
  const location = useLocation()
  const selectedKey = location.pathname

  return (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={[selectedKey]}
      items={menuItems}
    />
  )
}

function App() {
  return (
    <BrowserRouter>
      <Layout style={{ minHeight: '100vh' }}>
        <Sider width={250} theme="dark">
          <div style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: '#001529',
            color: '#fff',
            fontSize: 18,
            fontWeight: 'bold'
          }}>
            流程编排管理
          </div>
          <Navigation />
        </Sider>
        <Layout>
          <Header style={{
            background: '#fff',
            padding: '0 24px',
            display: 'flex',
            alignItems: 'center',
            boxShadow: '0 1px 4px rgba(0,21,41,.08)'
          }}>
            <h2 style={{ margin: 0 }}>流程编排管理系统</h2>
          </Header>
          <Content style={{ margin: '24px 16px 0' }}>
            <div style={{
              padding: 24,
              background: '#fff',
              minHeight: 360
            }}>
              <Routes>
                <Route path="/" element={<BusinessConfigManagement />} />
                <Route path="/business-config" element={<BusinessConfigManagement />} />
                <Route path="/resource-binding" element={<ResourceBindingManagement />} />
                <Route path="/orchestration-config" element={<OrchestrationConfigManagement />} />
                <Route path="/event-config" element={<EventConfigManagement />} />
                <Route path="/transition-flow" element={<TransitionFlowManagement />} />
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </BrowserRouter>
  )
}

export default App

import React from 'react'
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  AppstoreOutlined,
  SafetyCertificateOutlined,
  ApiOutlined,
  LinkOutlined
} from '@ant-design/icons'
import SystemManagement from './pages/SystemManagement'
import ApiConfigManagement from './pages/ApiConfigManagement'

const { Header, Content, Sider } = Layout

const App = () => {
  const [collapsed, setCollapsed] = React.useState(false)
  const navigate = useNavigate()

  const menuItems = [
    {
      key: '/systems',
      icon: <AppstoreOutlined />,
      label: '对接系统管理',
      link: '/systems'
    },
    {
      key: '/api-config',
      icon: <ApiOutlined />,
      label: 'API 配置管理',
      link: '/api-config'
    }
  ]

  const handleMenuClick = (e) => {
    const menuItem = menuItems.find(item => item.key === e.key)
    if (menuItem && menuItem.link) {
      navigate(menuItem.link)
    }
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div style={{ 
          height: 32, 
          margin: 16, 
          background: 'rgba(255, 255, 255, 0.2)',
          color: 'white',
          textAlign: 'center',
          lineHeight: '32px',
          fontWeight: 'bold'
        }}>
          {collapsed ? '开放平台' : '开放平台管理系统'}
        </div>
        <Menu theme="dark" defaultSelectedKeys={['/systems']} mode="inline" items={menuItems} onClick={handleMenuClick} />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 16px', background: '#fff' }}>
          <h2 style={{ margin: 0, lineHeight: '64px' }}>开放平台管理系统</h2>
        </Header>
        <Content style={{ margin: '16px' }}>
          <div style={{ 
            padding: 24, 
            minHeight: 360, 
            background: '#fff' 
          }}>
            <Routes>
              <Route path="/" element={<Navigate to="/systems" replace />} />
              <Route path="/systems" element={<SystemManagement />} />
              <Route path="/api-config" element={<ApiConfigManagement />} />
            </Routes>
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default App

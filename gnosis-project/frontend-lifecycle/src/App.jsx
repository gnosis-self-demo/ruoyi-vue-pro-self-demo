import React from 'react'
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  BusinessOutlined,
  DeploymentUnitOutlined,
  RuleOutlined,
  MonitorOutlined,
  LineChartOutlined,
  ExportOutlined,
  BookOutlined
} from '@ant-design/icons'
import BusinessTypeManagement from './pages/BusinessTypeManagement'
import StateModelConfig from './pages/StateModelConfig'
import RouteRuleConfig from './pages/RouteRuleConfig'
import EventMonitor from './pages/EventMonitor'
import InstanceTrace from './pages/InstanceTrace'
import ExitConfig from './pages/ExitConfig'
import MetadataDictionary from './pages/MetadataDictionary'

const { Header, Content, Sider } = Layout

const menuItems = [
  {
    key: '/business-type',
    icon: <BusinessOutlined />,
    label: <Link to="/business-type">业务类型管理</Link>
  },
  {
    key: '/state-model',
    icon: <DeploymentUnitOutlined />,
    label: <Link to="/state-model">状态模型配置</Link>
  },
  {
    key: '/route-rule',
    icon: <RuleOutlined />,
    label: <Link to="/route-rule">路由规则配置</Link>
  },
  {
    key: '/event-monitor',
    icon: <MonitorOutlined />,
    label: <Link to="/event-monitor">事件监控</Link>
  },
  {
    key: '/instance-trace',
    icon: <LineChartOutlined />,
    label: <Link to="/instance-trace">实例追踪</Link>
  },
  {
    key: '/exit-config',
    icon: <ExportOutlined />,
    label: <Link to="/exit-config">出口配置</Link>
  },
  {
    key: '/metadata-dictionary',
    icon: <BookOutlined />,
    label: <Link to="/metadata-dictionary">元数据字典</Link>
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
            业务生命周期管理
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
            <h2 style={{ margin: 0 }}>业务生命周期管理系统</h2>
          </Header>
          <Content style={{ margin: '24px 16px 0' }}>
            <div style={{
              padding: 24,
              background: '#fff',
              minHeight: 360
            }}>
              <Routes>
                <Route path="/" element={<BusinessTypeManagement />} />
                <Route path="/business-type" element={<BusinessTypeManagement />} />
                <Route path="/state-model" element={<StateModelConfig />} />
                <Route path="/route-rule" element={<RouteRuleConfig />} />
                <Route path="/event-monitor" element={<EventMonitor />} />
                <Route path="/instance-trace" element={<InstanceTrace />} />
                <Route path="/exit-config" element={<ExitConfig />} />
                <Route path="/metadata-dictionary" element={<MetadataDictionary />} />
              </Routes>
            </div>
          </Content>
        </Layout>
      </Layout>
    </BrowserRouter>
  )
}

export default App

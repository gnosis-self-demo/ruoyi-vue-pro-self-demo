import React from 'react'
import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  MailOutlined,
  MessageOutlined,
  UnorderedListOutlined,
  SendOutlined,
} from '@ant-design/icons'
import TemplateManage from './pages/TemplateManage'
import LogManage from './pages/LogManage'
import InboxManage from './pages/InboxManage'

const { Header, Sider, Content } = Layout

const items = [
  {
    key: '/template',
    icon: <MailOutlined />,
    label: <Link to="/template">模板管理</Link>,
  },
  {
    key: '/log',
    icon: <UnorderedListOutlined />,
    label: <Link to="/log">发送日志</Link>,
  },
  {
    key: '/inbox',
    icon: <MessageOutlined />,
    label: <Link to="/inbox">站内信</Link>,
  },
]

const App = () => {
  const location = useLocation()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', color: '#fff' }}>
        <div style={{ fontSize: 18, fontWeight: 'bold' }}>统一消息通知中心</div>
      </Header>
      <Layout>
        <Sider width={200} style={{ background: '#fff' }}>
          <Menu
            mode="inline"
            selectedKeys={[location.pathname]}
            items={items}
            style={{ height: '100%', borderRight: 0 }}
          />
        </Sider>
        <Layout style={{ padding: 24 }}>
          <Content style={{ background: '#fff', padding: 24, margin: 0, minHeight: 280 }}>
            <Routes>
              <Route path="/" element={<TemplateManage />} />
              <Route path="/template" element={<TemplateManage />} />
              <Route path="/log" element={<LogManage />} />
              <Route path="/inbox" element={<InboxManage />} />
            </Routes>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  )
}

export default App

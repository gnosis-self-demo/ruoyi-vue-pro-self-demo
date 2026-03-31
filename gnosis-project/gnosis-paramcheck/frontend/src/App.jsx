import React from 'react';
import { Layout, Menu, Button } from 'antd';
import { Link, Routes, Route, useNavigate } from 'react-router-dom';
import BusinessTypeManagement from './pages/BusinessTypeManagement';
import FlowConfigManagement from './pages/FlowConfigManagement';
import ValidationTest from './pages/ValidationTest';

const { Header, Sider, Content } = Layout;

function App() {
  const navigate = useNavigate();

  return (
    <Layout>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: '#001529' }}>
        <div style={{ color: '#fff', fontSize: '18px', fontWeight: 'bold' }}>参数校验系统</div>
        <Button type="text" style={{ color: '#fff' }} onClick={() => navigate('/')}>
          首页
        </Button>
      </Header>
      <Layout>
        <Sider width={200} style={{ background: '#001529' }}>
          <Menu
            mode="inline"
            theme="dark"
            defaultSelectedKeys={['1']}
            style={{ height: '100%', borderRight: 0 }}
          >
            <Menu.Item key="1">
              <Link to="/business-types">业务类型管理</Link>
            </Menu.Item>
            <Menu.Item key="2">
              <Link to="/flow-configs">流程配置管理</Link>
            </Menu.Item>
            <Menu.Item key="3">
              <Link to="/validation-test">校验测试</Link>
            </Menu.Item>
          </Menu>
        </Sider>
        <Layout style={{ padding: '0 24px 24px' }}>
          <Content
            style={{
              background: '#fff',
              padding: 24,
              margin: 0,
              minHeight: 280,
            }}
          >
            <Routes>
              <Route path="/business-types" element={<BusinessTypeManagement />} />
              <Route path="/flow-configs" element={<FlowConfigManagement />} />
              <Route path="/validation-test" element={<ValidationTest />} />
              <Route path="/" element={<BusinessTypeManagement />} />
            </Routes>
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
}

export default App;
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import FlowConfigManagement from './pages/FlowConfigManagement';
import BusinessTypeManagement from './pages/BusinessTypeManagement';
import ValidationLogManagement from './pages/ValidationLogManagement';
import ValidationTest from './pages/ValidationTest';
import { Layout, Menu } from 'antd';

const { Header, Content } = Layout;

const App = () => {
  const menuItems = [
    { key: '/flow-config', label: <Link to="/flow-config">流程配置管理</Link> },
    { key: '/business-type', label: <Link to="/business-type">业务类型管理</Link> },
    { key: '/validation-log', label: <Link to="/validation-log">校验日志管理</Link> },
    { key: '/validation-test', label: <Link to="/validation-test">校验测试</Link> },
  ];

  return (
    <Router>
      <Layout style={{ minHeight: '100vh' }}>
        <Header style={{ display: 'flex', alignItems: 'center' }}>
          <div style={{ color: '#fff', fontSize: 18, fontWeight: 'bold', marginRight: 40 }}>
            参数校验平台
          </div>
          <Menu
            theme="dark"
            mode="horizontal"
            items={menuItems}
            style={{ flex: 1 }}
          />
        </Header>
        <Content style={{ padding: '24px', background: '#f0f2f5' }}>
          <div style={{ background: '#fff', padding: '24px', borderRadius: '8px', minHeight: 'calc(100vh - 112px)' }}>
            <Routes>
              <Route path="/flow-config" element={<FlowConfigManagement />} />
              <Route path="/business-type" element={<BusinessTypeManagement />} />
              <Route path="/validation-log" element={<ValidationLogManagement />} />
              <Route path="/validation-test" element={<ValidationTest />} />
              <Route path="/" element={<Navigate to="/flow-config" replace />} />
            </Routes>
          </div>
        </Content>
      </Layout>
    </Router>
  );
};

export default App;

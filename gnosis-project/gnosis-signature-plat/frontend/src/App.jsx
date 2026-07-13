import React from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  FileProtectOutlined,
  SnippetsOutlined,
  NodeExpandOutlined,
  SafetyCertificateOutlined,
  ShopOutlined,
  AuditOutlined,
  HistoryOutlined,
} from '@ant-design/icons';
import FileManage from './pages/FileManage';
import TemplateManage from './pages/TemplateManage';
import ProcessManage from './pages/ProcessManage';
import SealManage from './pages/SealManage';
import SupplierManage from './pages/SupplierManage';
import CertificateManage from './pages/CertificateManage';
import AuditLogManage from './pages/AuditLogManage';

const { Sider, Content } = Layout;

const items = [
  { key: '/file', icon: <FileProtectOutlined />, label: <Link to="/file">文件管理</Link> },
  { key: '/template', icon: <SnippetsOutlined />, label: <Link to="/template">模板管理</Link> },
  { key: '/process', icon: <NodeExpandOutlined />, label: <Link to="/process">流程管理</Link> },
  { key: '/seal', icon: <SafetyCertificateOutlined />, label: <Link to="/seal">印章管理</Link> },
  { key: '/supplier', icon: <ShopOutlined />, label: <Link to="/supplier">供应商管理</Link> },
  { key: '/certificate', icon: <AuditOutlined />, label: <Link to="/certificate">证书管理</Link> },
  { key: '/audit', icon: <HistoryOutlined />, label: <Link to="/audit">审计日志</Link> },
];

const App = () => {
  const location = useLocation();

  const selectedKeys = location.pathname === '/'
    ? '/file'
    : location.pathname.split('/')[1]
      ? '/' + location.pathname.split('/')[1]
      : '/file';

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={200} style={{ borderRight: '1px solid #f0f0f0' }}>
        <div style={{
          padding: '16px',
          textAlign: 'center',
          fontSize: 18,
          fontWeight: 'bold',
          borderBottom: '1px solid #f0f0f0'
        }}>
          签章平台
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selectedKeys]}
          style={{ borderRight: 'none', marginTop: 8 }}
          items={items}
        />
      </Sider>
      <Layout>
        <Content style={{ background: '#f5f5f5' }}>
          <Routes>
            <Route path="/" element={<FileManage />} />
            <Route path="/file" element={<FileManage />} />
            <Route path="/template" element={<TemplateManage />} />
            <Route path="/process" element={<ProcessManage />} />
            <Route path="/seal" element={<SealManage />} />
            <Route path="/supplier" element={<SupplierManage />} />
            <Route path="/certificate" element={<CertificateManage />} />
            <Route path="/audit" element={<AuditLogManage />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
};

export default App;

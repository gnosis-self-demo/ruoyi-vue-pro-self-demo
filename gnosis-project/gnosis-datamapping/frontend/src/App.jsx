import React from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  SettingOutlined,
  ExperimentOutlined,
  PlayCircleOutlined,
  EditOutlined,
  ApiOutlined,
} from '@ant-design/icons';
import DataMappingConfigList from './pages/DataMappingConfigList';
import DataMappingConfigForm from './pages/DataMappingConfigForm';
import DataMappingConfigDetail from './pages/DataMappingConfigDetail';
import DataMappingSimulator from './pages/DataMappingSimulator';
import DataMappingTestCaseList from './pages/DataMappingTestCaseList';
import DataMappingApiManagement from './pages/DataMappingApiManagement';
import DataMappingVisualEditor from './pages/DataMappingVisualEditor';

const { Header, Sider, Content } = Layout;

const items = [
  { key: '/config', icon: <SettingOutlined />, label: <Link to="/config">配置管理</Link> },
  { key: '/testcase', icon: <ExperimentOutlined />, label: <Link to="/testcase">测试用例</Link> },
  { key: '/simulator', icon: <PlayCircleOutlined />, label: <Link to="/simulator">模拟器</Link> },
  { key: '/visual', icon: <EditOutlined />, label: <Link to="/visual">可视化编辑</Link> },
  { key: '/api', icon: <ApiOutlined />, label: <Link to="/api">API管理</Link> },
];

const App = () => {
  const location = useLocation();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={200} style={{ borderRight: '1px solid #f0f0f0' }}>
        <div style={{ padding: '16px', textAlign: 'center', fontSize: 18, fontWeight: 'bold', borderBottom: '1px solid #f0f0f0' }}>
          数据映射平台
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname === '/' ? '/config' : location.pathname.split('/')[1] ? '/' + location.pathname.split('/')[1] : '/config']}
          style={{ borderRight: 'none', marginTop: 8 }}
          items={items}
        />
      </Sider>
      <Layout>
        <Content style={{ background: '#f5f5f5' }}>
          <Routes>
            <Route path="/" element={<DataMappingConfigList />} />
            <Route path="/config" element={<DataMappingConfigList />} />
            <Route path="/config/create" element={<DataMappingConfigForm />} />
            <Route path="/config/edit/:id" element={<DataMappingConfigForm />} />
            <Route path="/config/detail/:id" element={<DataMappingConfigDetail />} />
            <Route path="/testcase" element={<DataMappingTestCaseList />} />
            <Route path="/simulator" element={<DataMappingSimulator />} />
            <Route path="/visual" element={<DataMappingVisualEditor />} />
            <Route path="/api" element={<DataMappingApiManagement />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
};

export default App;

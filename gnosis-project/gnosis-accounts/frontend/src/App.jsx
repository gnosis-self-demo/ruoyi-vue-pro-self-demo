import React from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import {
  SettingOutlined,
  WalletOutlined,
  FileTextOutlined,
  UnorderedListOutlined,
  SwapOutlined,
  BankOutlined,
  TeamOutlined,
  DashboardOutlined,
} from '@ant-design/icons';
import SubjectManage from './pages/SubjectManage';
import AccountManage from './pages/AccountManage';
import JournalList from './pages/JournalList';
import EntryList from './pages/EntryList';
import ChannelClearing from './pages/ChannelClearing';
import BankTransfer from './pages/BankTransfer';
import CustomerSettlement from './pages/CustomerSettlement';
import AccountDashboard from './pages/AccountDashboard';

const { Sider, Content } = Layout;

const items = [
  { key: '/subject', icon: <SettingOutlined />, label: <Link to="/subject">科目管理</Link> },
  { key: '/account', icon: <WalletOutlined />, label: <Link to="/account">账户管理</Link> },
  { key: '/journal', icon: <FileTextOutlined />, label: <Link to="/journal">会计流水</Link> },
  { key: '/entry', icon: <UnorderedListOutlined />, label: <Link to="/entry">分录明细</Link> },
  { key: '/clearing', icon: <SwapOutlined />, label: <Link to="/clearing">渠道清算</Link> },
  { key: '/transfer', icon: <BankOutlined />, label: <Link to="/transfer">银存结转</Link> },
  { key: '/settlement', icon: <TeamOutlined />, label: <Link to="/settlement">客资结算</Link> },
  { key: '/dashboard', icon: <DashboardOutlined />, label: <Link to="/dashboard">账务看板</Link> },
];

const App = () => {
  const location = useLocation();

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={200} style={{ borderRight: '1px solid #f0f0f0' }}>
        <div style={{ padding: '16px', textAlign: 'center', fontSize: 18, fontWeight: 'bold', borderBottom: '1px solid #f0f0f0' }}>
          账务管理平台
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname === '/' ? '/subject' : location.pathname.split('/')[1] ? '/' + location.pathname.split('/')[1] : '/subject']}
          style={{ borderRight: 'none', marginTop: 8 }}
          items={items}
        />
      </Sider>
      <Layout>
        <Content style={{ background: '#f5f5f5' }}>
          <Routes>
            <Route path="/" element={<SubjectManage />} />
            <Route path="/subject" element={<SubjectManage />} />
            <Route path="/account" element={<AccountManage />} />
            <Route path="/journal" element={<JournalList />} />
            <Route path="/entry" element={<EntryList />} />
            <Route path="/clearing" element={<ChannelClearing />} />
            <Route path="/transfer" element={<BankTransfer />} />
            <Route path="/settlement" element={<CustomerSettlement />} />
            <Route path="/dashboard" element={<AccountDashboard />} />
          </Routes>
        </Content>
      </Layout>
    </Layout>
  );
};

export default App;

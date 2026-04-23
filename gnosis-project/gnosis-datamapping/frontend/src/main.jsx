import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import DataMappingConfigList from './pages/DataMappingConfigList';
import './index.css';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ConfigProvider locale={zhCN}>
    <BrowserRouter>
      <DataMappingConfigList />
    </BrowserRouter>
  </ConfigProvider>
);

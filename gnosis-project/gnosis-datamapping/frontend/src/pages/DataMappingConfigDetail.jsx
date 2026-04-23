import React from 'react';
import { Descriptions, Tag } from 'antd';

const DataMappingConfigDetail = ({ record }) => {
  return (
    <Descriptions column={1} bordered>
      <Descriptions.Item label="配置名称">{record.configName}</Descriptions.Item>
      <Descriptions.Item label="配置编码">{record.configCode}</Descriptions.Item>
      <Descriptions.Item label="系统ID">{record.systemId}</Descriptions.Item>
      <Descriptions.Item label="系统名称">{record.systemName}</Descriptions.Item>
      <Descriptions.Item label="配置版本">{record.configVersion}</Descriptions.Item>
      <Descriptions.Item label="API版本">{record.apiVersion}</Descriptions.Item>
      <Descriptions.Item label="目标接口地址">{record.targetEndpoint}</Descriptions.Item>
      <Descriptions.Item label="JSON根路径">{record.jsonRootPath}</Descriptions.Item>
      <Descriptions.Item label="状态">
        <Tag color={record.status === 1 ? 'green' : 'red'}>
          {record.status === 1 ? '启用' : '禁用'}
        </Tag>
      </Descriptions.Item>
      <Descriptions.Item label="描述">{record.description}</Descriptions.Item>
      <Descriptions.Item label="配置JSON">
        <pre style={{ whiteSpace: 'pre-wrap', wordBreak: 'break-all', maxHeight: 300, overflow: 'auto' }}>
          {record.configJson}
        </pre>
      </Descriptions.Item>
      <Descriptions.Item label="创建人ID">{record.createUserId}</Descriptions.Item>
      <Descriptions.Item label="创建时间">{record.createTime}</Descriptions.Item>
      <Descriptions.Item label="更新人ID">{record.updateUserId}</Descriptions.Item>
      <Descriptions.Item label="更新时间">{record.updateTime}</Descriptions.Item>
    </Descriptions>
  );
};

export default DataMappingConfigDetail;

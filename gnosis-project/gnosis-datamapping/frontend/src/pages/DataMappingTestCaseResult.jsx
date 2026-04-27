import React from 'react';
import { Modal, Descriptions, Tag, Typography, Tabs } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const DataMappingTestCaseResult = ({ visible, onCancel, data }) => {
  if (!data) return null;

  const isPassed = data.isPassed;

  const formatJson = (obj) => {
    if (!obj) return '{}';
    try {
      if (typeof obj === 'string') {
        return JSON.stringify(JSON.parse(obj), null, 2);
      }
      return JSON.stringify(obj, null, 2);
    } catch (e) {
      return String(obj);
    }
  };

  return (
    <Modal title="测试执行结果" open={visible} onCancel={onCancel} width={900} footer={null}>
      <Descriptions column={2} style={{ marginTop: 16 }}>
        <Descriptions.Item label="测试ID">{data.testCaseId || '-'}</Descriptions.Item>
        <Descriptions.Item label="执行耗时">{data.executionTime != null ? `${data.executionTime}ms` : '-'}</Descriptions.Item>
        <Descriptions.Item label="测试结果" span={2}>
          {isPassed ? (
            <Tag color="success" icon={<CheckCircleOutlined />}>通过</Tag>
          ) : (
            <Tag color="error" icon={<CloseCircleOutlined />}>失败</Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      {data.message && (
        <div style={{ marginTop: 12, padding: 8, background: '#fff2f0', borderRadius: 4 }}>
          <strong>错误信息：</strong>{data.message}
        </div>
      )}

      {data.diffInfo && (
        <div style={{ marginTop: 12, padding: 8, background: '#fffbe6', borderRadius: 4 }}>
          <strong>差异信息：</strong>
          <pre style={{ margin: '4px 0 0', whiteSpace: 'pre-wrap' }}>{data.diffInfo}</pre>
        </div>
      )}

      <div style={{ marginTop: 16 }}>
        <strong>实际结果：</strong>
        <pre style={{ background: isPassed ? '#f6ffed' : '#fff2f0', padding: 12, borderRadius: 4, maxHeight: 300, overflow: 'auto', fontSize: 12 }}>
          {formatJson(data.actualResult)}
        </pre>
      </div>
    </Modal>
  );
};

export default DataMappingTestCaseResult;

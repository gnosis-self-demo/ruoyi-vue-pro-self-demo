import React from 'react';
import { Modal, Descriptions, Tag, Typography } from 'antd';
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const { TextArea } = Typography;

const DataMappingTestCaseResult = ({ visible, onCancel, data }) => {
  if (!data) return null;

  const isPassed = data.isPassed;

  return (
    <Modal title="测试执行结果" open={visible} onCancel={onCancel} width={800} footer={null}>
      <Descriptions column={2} style={{ marginTop: 16 }}>
        <Descriptions.Item label="测试ID">{data.testCaseId}</Descriptions.Item>
        <Descriptions.Item label="执行耗时">{data.executionTime}ms</Descriptions.Item>
        <Descriptions.Item label="测试结果" span={2}>
          {isPassed ? (
            <Tag color="success" icon={<CheckCircleOutlined />}>通过</Tag>
          ) : (
            <Tag color="error" icon={<CloseCircleOutlined />}>失败</Tag>
          )}
        </Descriptions.Item>
      </Descriptions>

      {data.diffInfo && (
        <div style={{ marginTop: 16 }}>
          <h4>差异信息：</h4>
          <TextArea readOnly value={data.diffInfo} rows={4} style={{ backgroundColor: '#fffbe6' }} />
        </div>
      )}

      <div style={{ marginTop: 16 }}>
        <h4>实际结果：</h4>
        <pre style={{ background: '#f5f5f5', padding: 12, borderRadius: 4, maxHeight: 300, overflow: 'auto' }}>
          {JSON.stringify(data.actualResult, null, 2)}
        </pre>
      </div>
    </Modal>
  );
};

export default DataMappingTestCaseResult;

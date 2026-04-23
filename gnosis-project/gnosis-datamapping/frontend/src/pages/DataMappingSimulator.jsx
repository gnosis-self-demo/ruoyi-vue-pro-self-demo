import React, { useState } from 'react';
import { Row, Col, Input, Select, Button, Table, message, Tabs } from 'antd';
import { SendOutlined } from '@ant-design/icons';
import { dataMappingApi } from '../api/dataMappingApi';

const { TextArea } = Input;

const DataMappingSimulator = ({ configId, configCode }) => {
  const [requestJson, setRequestJson] = useState('');
  const [resultData, setResultData] = useState(null);
  const [errors, setErrors] = useState([]);
  const [validationErrors, setValidationErrors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [executionTime, setExecutionTime] = useState(null);

  const handleTest = async () => {
    if (!requestJson.trim()) {
      message.warning('请输入请求JSON');
      return;
    }
    try {
      JSON.parse(requestJson);
    } catch (e) {
      message.error('请求JSON格式错误');
      return;
    }

    setLoading(true);
    try {
      const res = await dataMappingApi.test(configId, requestJson);
      if (res.code === 200) {
        const data = res.data;
        setResultData(data.success ? data.resultData : data.resultData);
        setErrors(data.errors || []);
        setValidationErrors(data.validationErrors || []);
        setExecutionTime(data.executionTime);
        if (data.success) {
          message.success('测试成功');
        } else {
          message.warning('测试完成，存在错误');
        }
      }
    } catch (error) {
      message.error('测试失败');
    }
    setLoading(false);
  };

  const errorColumns = [
    { title: '字段', dataIndex: 'field', key: 'field' },
    { title: '规则', dataIndex: 'rule', key: 'rule' },
    { title: '错误信息', dataIndex: 'message', key: 'message' },
    { title: '当前值', dataIndex: 'value', key: 'value' },
  ];

  const items = [
    {
      key: '1',
      label: '转换结果',
      children: (
        <pre style={{ background: '#f5f5f5', padding: 16, borderRadius: 4, overflow: 'auto', maxHeight: 400 }}>
          {resultData ? JSON.stringify(resultData, null, 2) : '暂无结果'}
        </pre>
      ),
    },
    {
      key: '2',
      label: `转换错误 (${errors.length})`,
      children: (
        <Table
          columns={errorColumns}
          dataSource={errors}
          rowKey={(record, index) => index}
          pagination={false}
          size="small"
        />
      ),
    },
    {
      key: '3',
      label: `校验失败 (${validationErrors.length})`,
      children: (
        <Table
          columns={errorColumns}
          dataSource={validationErrors}
          rowKey={(record, index) => index}
          pagination={false}
          size="small"
        />
      ),
    },
  ];

  return (
    <div>
      <Row gutter={16}>
        <Col span={12}>
          <h4>输入请求JSON</h4>
          <TextArea
            value={requestJson}
            onChange={(e) => setRequestJson(e.target.value)}
            rows={15}
            placeholder='{"contractData": {"contractNo": "HT2024001", "contractAmount": 1500000.567}}'
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={handleTest}
            loading={loading}
            style={{ marginTop: 8 }}
          >
            执行测试
          </Button>
          {executionTime && <span style={{ marginLeft: 16 }}>耗时: {executionTime}ms</span>}
        </Col>
        <Col span={12}>
          <h4>输出结果</h4>
          <Tabs items={items} />
        </Col>
      </Row>
    </div>
  );
};

export default DataMappingSimulator;

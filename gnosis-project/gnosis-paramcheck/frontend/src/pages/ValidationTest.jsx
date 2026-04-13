import React, { useState, useEffect } from 'react';
import { Card, Form, Select, Button, Input, message, Tabs, Tag, Alert, Row, Col, Statistic } from 'antd';
import { flowConfigApi, validationApi } from '../services/apiService';

const { Option } = Select;
const { TextArea } = Input;

const ValidationTest = () => {
  const [flows, setFlows] = useState([]);
  const [selectedFlow, setSelectedFlow] = useState(null);
  const [inputJson, setInputJson] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchFlows();
  }, []);

  const fetchFlows = async () => {
    try {
      const res = await flowConfigApi.getActiveList();
      setFlows(res.data || []);
    } catch (error) {
      message.error('获取流程列表失败');
    }
  };

  const handleValidate = async () => {
    if (!selectedFlow) {
      message.warning('请选择校验流程');
      return;
    }
    if (!inputJson.trim()) {
      message.warning('请输入JSON数据');
      return;
    }
    try {
      JSON.parse(inputJson);
    } catch (e) {
      message.error('JSON格式不正确');
      return;
    }

    setLoading(true);
    try {
      const parsedData = JSON.parse(inputJson);
      const response = await validationApi.validate(selectedFlow, parsedData);
      setResult(response.data);
      if (response.data && response.data.success) {
        message.success('校验通过');
      } else {
        message.warning('校验未通过');
      }
    } catch (error) {
      setResult({ success: false, errorMsg: error.message || '请求失败' });
      message.error('校验请求失败');
    } finally {
      setLoading(false);
    }
  };

  const sampleData = {
    'ORDER_CREATE_FLOW': JSON.stringify({
      orderNo: 'ORD1234567890',
      amount: 99.99,
      userId: 'U001'
    }, null, 2),
    'USER_REGISTER_FLOW': JSON.stringify({
      username: 'testuser',
      email: 'test@example.com',
      phone: '13800138000'
    }, null, 2),
    'SIMPLE_CHECK_FLOW': JSON.stringify({
      email: 'test@example.com',
      phone: '13800138000'
    }, null, 2)
  };

  const handleFlowSelect = (flowId) => {
    setSelectedFlow(flowId);
    if (sampleData[flowId]) {
      setInputJson(sampleData[flowId]);
    }
  };

  return (
    <div>
      <h2>参数校验测试</h2>
      <Row gutter={16}>
        <Col span={16}>
          <Card title="校验配置" style={{ marginBottom: 16 }}>
            <Form layout="vertical">
              <Form.Item label="选择校验流程">
                <Select
                  placeholder="请选择校验流程"
                  onChange={handleFlowSelect}
                  style={{ width: '100%' }}
                  value={selectedFlow}
                >
                  {flows.map(flow => (
                    <Option key={flow.flowId} value={flow.flowId}>
                      {flow.flowName} ({flow.modeType})
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item label="输入JSON数据">
                <TextArea
                  rows={10}
                  value={inputJson}
                  onChange={(e) => setInputJson(e.target.value)}
                  placeholder='请输入JSON数据'
                />
              </Form.Item>
              <Form.Item>
                <Button type="primary" onClick={handleValidate} loading={loading} size="large">
                  执行校验
                </Button>
              </Form.Item>
            </Form>
          </Card>
        </Col>
        <Col span={8}>
          <Card title="校验结果" style={{ marginBottom: 16 }}>
            {result ? (
              <div>
                <Statistic
                  title="校验状态"
                  value={result.success ? '通过' : '未通过'}
                  valueStyle={{ color: result.success ? '#3f8600' : '#cf1322' }}
                />
                {result.errorMsg && (
                  <Alert
                    style={{ marginTop: 16 }}
                    message="错误信息"
                    description={result.errorMsg}
                    type="error"
                    showIcon
                  />
                )}
                {result.failedNode && (
                  <Alert
                    style={{ marginTop: 8 }}
                    message="失败节点"
                    description={result.failedNode}
                    type="warning"
                    showIcon
                  />
                )}
                {result.errorCode && (
                  <Alert
                    style={{ marginTop: 8 }}
                    message="错误编码"
                    description={result.errorCode}
                    type="warning"
                    showIcon
                  />
                )}
              </div>
            ) : (
              <Alert message="请选择流程并输入数据后执行校验" type="info" />
            )}
          </Card>
          <Card title="流程信息">
            {selectedFlow && (() => {
              const flow = flows.find(f => f.flowId === selectedFlow);
              if (!flow) return <Alert message="未找到流程信息" type="warning" />;
              return (
                <div>
                  <p><strong>流程ID:</strong> {flow.flowId}</p>
                  <p><strong>流程名称:</strong> {flow.flowName}</p>
                  <p><strong>校验模式:</strong> <Tag color={
                    flow.modeType === 'FLOW' ? 'blue' :
                    flow.modeType === 'HANDLER' ? 'green' : 'orange'
                  }>{flow.modeType}</Tag></p>
                  <p><strong>业务类型:</strong> {flow.businessType}</p>
                  {flow.elExpression && (
                    <p><strong>EL表达式:</strong> <code>{flow.elExpression}</code></p>
                  )}
                  {flow.handlerCode && (
                    <p><strong>处理器:</strong> {flow.handlerCode}</p>
                  )}
                </div>
              );
            })()}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ValidationTest;

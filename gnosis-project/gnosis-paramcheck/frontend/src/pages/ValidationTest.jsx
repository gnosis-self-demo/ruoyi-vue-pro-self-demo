import React, { useState } from 'react';
import { Form, Button, Input, Select, message, Card, Result, Space } from 'antd';
import { validationApi } from '../services/apiService';

const { Option } = Select;

const ValidationTest = () => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [validationResult, setValidationResult] = useState(null);

  const handleValidate = async (values) => {
    setLoading(true);
    try {
      const response = await validationApi.validate(values);
      setValidationResult(response.data);
      message.success('校验成功');
    } catch (error) {
      message.error('校验失败');
      console.error('Error validating:', error);
      setValidationResult({
        valid: false,
        message: '校验失败',
        details: []
      });
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    form.resetFields();
    setValidationResult(null);
  };

  return (
    <div>
      <h2>校验测试</h2>
      <Card style={{ marginBottom: 24 }}>
        <Form form={form} onFinish={handleValidate} layout="vertical">
          <Form.Item
            name="businessType"
            label="业务类型"
            rules={[{ required: true, message: '请选择业务类型' }]}
          >
            <Select placeholder="请选择业务类型">
              <Option value="order">订单业务</Option>
              <Option value="user">用户注册</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="validationMode"
            label="校验模式"
            rules={[{ required: true, message: '请选择校验模式' }]}
          >
            <Select placeholder="请选择校验模式">
              <Option value="FLOW">流程模式</Option>
              <Option value="HANDLER">处理器模式</Option>
              <Option value="HYBRID">混合模式</Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="params"
            label="参数JSON"
            rules={[{ required: true, message: '请输入参数JSON' }]}
          >
            <Input.TextArea 
              rows={6} 
              placeholder="请输入参数JSON"
            />
          </Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={loading}>
              执行校验
            </Button>
            <Button onClick={handleReset}>
              重置
            </Button>
          </Space>
        </Form>
      </Card>
      {validationResult && (
        <Card title="校验结果">
          <Result
            status={validationResult.valid ? "success" : "error"}
            title={validationResult.valid ? "校验通过" : "校验失败"}
            subTitle={validationResult.message}
          />
        </Card>
      )}
    </div>
  );
};

export default ValidationTest;
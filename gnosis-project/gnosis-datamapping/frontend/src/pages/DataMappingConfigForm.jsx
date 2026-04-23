import React, { useEffect } from 'react';
import { Form, Input, Button, Select, message } from 'antd';
import { dataMappingApi } from '../api/dataMappingApi';

const { TextArea } = Input;

const DataMappingConfigForm = ({ record, onSuccess, isEdit }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (record && isEdit) {
      form.setFieldsValue(record);
    }
  }, [record, isEdit, form]);

  const handleSubmit = async (values) => {
    try {
      if (isEdit) {
        await dataMappingApi.update({ ...values, updateUserId: 'admin' });
        message.success('更新成功');
      } else {
        await dataMappingApi.create({ ...values, createUserId: 'admin' });
        message.success('创建成功');
      }
      onSuccess();
    } catch (error) {
      message.error(isEdit ? '更新失败' : '创建失败');
    }
  };

  return (
    <Form form={form} layout="vertical" onFinish={handleSubmit}>
      <Form.Item name="configName" label="配置名称" rules={[{ required: true, message: '请输入配置名称' }]}>
        <Input />
      </Form.Item>
      <Form.Item name="configCode" label="配置编码" rules={[{ required: true, message: '请输入配置编码' }]}>
        <Input />
      </Form.Item>
      <Form.Item name="systemId" label="系统ID">
        <Input />
      </Form.Item>
      <Form.Item name="systemName" label="系统名称">
        <Input />
      </Form.Item>
      <Form.Item name="configVersion" label="配置版本">
        <Input />
      </Form.Item>
      <Form.Item name="apiVersion" label="API版本">
        <Input />
      </Form.Item>
      <Form.Item name="targetEndpoint" label="目标接口地址">
        <Input />
      </Form.Item>
      <Form.Item name="jsonRootPath" label="JSON根路径">
        <Input placeholder="例如: $.contractData" />
      </Form.Item>
      <Form.Item name="configJson" label="配置JSON">
        <TextArea rows={10} placeholder="输入完整的映射配置JSON" />
      </Form.Item>
      <Form.Item name="description" label="描述">
        <TextArea rows={3} />
      </Form.Item>
      <Form.Item>
        <Button type="primary" htmlType="submit">
          {isEdit ? '更新' : '创建'}
        </Button>
      </Form.Item>
    </Form>
  );
};

export default DataMappingConfigForm;

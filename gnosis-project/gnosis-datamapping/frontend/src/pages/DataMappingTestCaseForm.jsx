import React, { useState, useEffect } from 'react';
import { Modal, Form, Input, Select, Button, Space, message } from 'antd';
import { testCaseApi, dataMappingApi } from '../api/dataMappingApi';

const { TextArea } = Input;

const DataMappingTestCaseForm = ({ visible, onCancel, record, onSuccess }) => {
  const [form] = Form.useForm();
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (visible) {
      fetchConfigs();
      if (record) {
        form.setFieldsValue(record);
      } else {
        form.resetFields();
      }
    }
  }, [visible, record]);

  const fetchConfigs = async () => {
    try {
      const res = await dataMappingApi.pageList({ query: {}, pageNum: 0, pageSize: 1000 });
      if (res.code === 200) {
        setConfigs(res.data.list || []);
      }
    } catch (error) {
      console.error('获取配置列表失败', error);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      if (record) {
        const res = await testCaseApi.update({ ...values, id: record.id });
        if (res.code === 200) {
          message.success('更新成功');
          onSuccess();
        }
      } else {
        const res = await testCaseApi.create(values);
        if (res.code === 200) {
          message.success('创建成功');
          onSuccess();
        }
      }
    } catch (error) {
      console.error('保存失败', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal title={record ? '编辑测试用例' : '新建测试用例'} open={visible} onCancel={onCancel}
      onOk={handleSubmit} confirmLoading={loading} width={800} okText="保存" cancelText="取消">
      <Form form={form} layout="vertical" style={{ marginTop: 20 }}>
        <Form.Item name="configId" label="关联配置" rules={[{ required: true, message: '请选择关联配置' }]}>
          <Select showSearch placeholder="请选择配置" options={configs.map(c => ({ label: `${c.configName}(${c.configCode})`, value: c.id }))} />
        </Form.Item>
        <Form.Item name="configCode" hidden><Input /></Form.Item>
        <Form.Item name="caseName" label="用例名称" rules={[{ required: true, message: '请输入用例名称' }]}>
          <Input placeholder="请输入用例名称" />
        </Form.Item>
        <Form.Item name="caseCode" label="用例编码" rules={[{ required: true, message: '请输入用例编码' }]}>
          <Input placeholder="请输入用例编码" />
        </Form.Item>
        <Form.Item name="requestJson" label="请求JSON" rules={[{ required: true, message: '请输入请求JSON' }]}>
          <TextArea rows={6} placeholder='请输入请求JSON，如：{"field1": "value1"}' />
        </Form.Item>
        <Form.Item name="expectedResultJson" label="期望结果JSON" rules={[{ required: true, message: '请输入期望结果JSON' }]}>
          <TextArea rows={6} placeholder='请输入期望结果JSON，如：{"field1": "value1"}' />
        </Form.Item>
        <Form.Item name="description" label="描述">
          <TextArea rows={2} placeholder="请输入用例描述" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DataMappingTestCaseForm;

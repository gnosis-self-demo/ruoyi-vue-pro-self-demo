import React, { useState, useEffect } from 'react';
import { Table, Button, Tabs, Input, message } from 'antd';
import { SearchOutlined, SendOutlined } from '@ant-design/icons';
import { dataMappingApi } from '../api/dataMappingApi';

const { TextArea } = Input;

const DataMappingApiManagement = () => {
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState(null);
  const [testInput, setTestInput] = useState('');
  const [testResult, setTestResult] = useState(null);
  const [logs, setLogs] = useState([]);
  const [logLoading, setLogLoading] = useState(false);

  useEffect(() => {
    fetchConfigs();
  }, []);

  const fetchConfigs = async () => {
    setLoading(true);
    try {
      const res = await dataMappingApi.pageList({ pageNum: 1, pageSize: 100, query: {} });
      if (res.code === 200) {
        setConfigs(res.data.list || []);
      }
    } catch (error) {
      message.error('查询配置失败');
    }
    setLoading(false);
  };

  const handleTest = async () => {
    if (!selectedConfig) {
      message.warning('请选择配置');
      return;
    }
    if (!testInput.trim()) {
      message.warning('请输入测试JSON');
      return;
    }
    try {
      JSON.parse(testInput);
    } catch (e) {
      message.error('JSON格式错误');
      return;
    }
    try {
      const res = await dataMappingApi.test(selectedConfig.id, testInput);
      if (res.code === 200) {
        setTestResult(res.data);
        message.success('测试完成');
        fetchLogs(selectedConfig.id);
      }
    } catch (error) {
      message.error('测试失败');
    }
  };

  const fetchLogs = async (configId) => {
    setLogLoading(true);
    try {
      const res = await dataMappingApi.logPageList(configId, 1, 10);
      if (res.code === 200) {
        setLogs(res.data.list || []);
      }
    } catch (error) {
      message.error('查询日志失败');
    }
    setLogLoading(false);
  };

  const configColumns = [
    { title: '配置名称', dataIndex: 'configName', key: 'configName' },
    { title: '配置编码', dataIndex: 'configCode', key: 'configCode' },
    { title: '目标接口', dataIndex: 'targetEndpoint', key: 'targetEndpoint' },
    { title: '操作', key: 'action', render: (_, record) => (
      <Button type="link" onClick={() => setSelectedConfig(record)}>
        选择
      </Button>
    )},
  ];

  const logColumns = [
    { title: '成功', dataIndex: 'success', key: 'success', render: (v) => v ? '是' : '否' },
    { title: '执行耗时(ms)', dataIndex: 'executionTime', key: 'executionTime' },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime' },
  ];

  const items = [
    {
      key: '1',
      label: 'API列表',
      children: (
        <Table
          columns={configColumns}
          dataSource={configs}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      ),
    },
    {
      key: '2',
      label: '在线测试',
      children: (
        <div>
          <div style={{ marginBottom: 8 }}>
            已选配置: {selectedConfig ? `${selectedConfig.configName} (${selectedConfig.configCode})` : '未选择'}
          </div>
          <TextArea
            value={testInput}
            onChange={(e) => setTestInput(e.target.value)}
            rows={8}
            placeholder='{"contractData": {"contractNo": "HT2024001"}}'
            style={{ marginBottom: 8 }}
          />
          <Button type="primary" icon={<SendOutlined />} onClick={handleTest}>
            执行测试
          </Button>
          {testResult && (
            <div style={{ marginTop: 16 }}>
              <h4>测试结果</h4>
              <p>成功: {testResult.success ? '是' : '否'}</p>
              {testResult.executionTime && <p>耗时: {testResult.executionTime}ms</p>}
              <pre style={{ background: '#f5f5f5', padding: 16, borderRadius: 4, overflow: 'auto' }}>
                {JSON.stringify(testResult, null, 2)}
              </pre>
            </div>
          )}
        </div>
      ),
    },
    {
      key: '3',
      label: '调用历史',
      children: (
        <Table
          columns={logColumns}
          dataSource={logs}
          rowKey="id"
          loading={logLoading}
          pagination={false}
        />
      ),
    },
  ];

  return (
    <div>
      <Tabs items={items} />
    </div>
  );
};

export default DataMappingApiManagement;

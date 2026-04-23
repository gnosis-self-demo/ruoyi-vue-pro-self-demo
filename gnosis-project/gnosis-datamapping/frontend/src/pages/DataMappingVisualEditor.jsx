import React, { useState, useEffect } from 'react';
import { Tabs, Form, Input, Button, Select, Table, Space, message } from 'antd';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons';

const { TextArea } = Input;
const { Option } = Select;

const DataMappingVisualEditor = ({ record }) => {
  const [activeTab, setActiveTab] = useState('1');
  const [config, setConfig] = useState(null);
  const [directMappings, setDirectMappings] = useState([]);
  const [crossNodeMappings, setCrossNodeMappings] = useState([]);
  const [listExtractionMappings, setListExtractionMappings] = useState([]);
  const [stringToListMappings, setStringToListMappings] = useState([]);
  const [transformRules, setTransformRules] = useState({});
  const [previewJson, setPreviewJson] = useState('');

  useEffect(() => {
    if (record && record.configJson) {
      try {
        const parsed = JSON.parse(record.configJson);
        setConfig(parsed);
        setDirectMappings(parsed.directMappings || []);
        setCrossNodeMappings(parsed.crossNodeMappings || []);
        setListExtractionMappings(parsed.listExtractionMappings || []);
        setStringToListMappings(parsed.stringToListMappings || []);
        setTransformRules(parsed.transformRules || {});
      } catch (e) {
        message.error('配置JSON解析失败');
      }
    } else {
      const initialConfig = {
        configMetadata: {
          systemId: '',
          systemName: '',
          configVersion: '1.0',
          lastModified: new Date().toISOString(),
        },
        apiConfig: {
          apiVersion: 'v1.0',
          targetEndpoint: '',
          jsonRootPath: '$',
        },
        directMappings: [],
        crossNodeMappings: [],
        listExtractionMappings: [],
        stringToListMappings: [],
        transformRules: {},
        validationEngine: {
          stopOnFirstError: false,
          collectAllErrors: true,
        }
      };
      setConfig(initialConfig);
    }
  }, [record]);

  const addDirectMapping = () => {
    setDirectMappings([...directMappings, {
      sourceField: '',
      targetField: '',
      dataType: 'string',
      required: false,
      validationRules: [],
      transforms: []
    }]);
  };

  const updateDirectMapping = (index, field, value) => {
    const updated = [...directMappings];
    updated[index] = { ...updated[index], [field]: value };
    setDirectMappings(updated);
  };

  const removeDirectMapping = (index) => {
    setDirectMappings(directMappings.filter((_, i) => i !== index));
  };

  const addCrossNodeMapping = () => {
    setCrossNodeMappings([...crossNodeMappings, {
      direction: 'rootToNode',
      sourcePath: '',
      targetPath: '',
      dataType: 'string',
      transforms: []
    }]);
  };

  const updateCrossNodeMapping = (index, field, value) => {
    const updated = [...crossNodeMappings];
    updated[index] = { ...updated[index], [field]: value };
    setCrossNodeMappings(updated);
  };

  const removeCrossNodeMapping = (index) => {
    setCrossNodeMappings(crossNodeMappings.filter((_, i) => i !== index));
  };

  const generatePreview = () => {
    const fullConfig = {
      ...config,
      directMappings,
      crossNodeMappings,
      listExtractionMappings,
      stringToListMappings,
      transformRules,
    };
    setPreviewJson(JSON.stringify(fullConfig, null, 2));
  };

  const directColumns = [
    { title: '源字段', dataIndex: 'sourceField', key: 'sourceField', render: (text, _, index) => <Input value={text} onChange={(e) => updateDirectMapping(index, 'sourceField', e.target.value)} /> },
    { title: '目标字段', dataIndex: 'targetField', key: 'targetField', render: (text, _, index) => <Input value={text} onChange={(e) => updateDirectMapping(index, 'targetField', e.target.value)} /> },
    { title: '数据类型', dataIndex: 'dataType', key: 'dataType', render: (text, _, index) => (
      <Select value={text} onChange={(val) => updateDirectMapping(index, 'dataType', val)} style={{ width: 120 }}>
        <Option value="string">String</Option>
        <Option value="integer">Integer</Option>
        <Option value="decimal">Decimal</Option>
        <Option value="boolean">Boolean</Option>
        <Option value="date">Date</Option>
      </Select>
    )},
    { title: '必填', dataIndex: 'required', key: 'required', render: (text, _, index) => <Select value={text} onChange={(val) => updateDirectMapping(index, 'required', val)} style={{ width: 80 }}><Option value={true}>是</Option><Option value={false}>否</Option></Select> },
    { title: '操作', key: 'action', render: (_, __, index) => <Button type="link" danger icon={<DeleteOutlined />} onClick={() => removeDirectMapping(index)} /> },
  ];

  const crossNodeColumns = [
    { title: '方向', dataIndex: 'direction', key: 'direction', render: (text, _, index) => (
      <Select value={text} onChange={(val) => updateCrossNodeMapping(index, 'direction', val)} style={{ width: 120 }}>
        <Option value="rootToNode">根→节点</Option>
        <Option value="nodeToRoot">节点→根</Option>
        <Option value="nodeToNode">节点→节点</Option>
      </Select>
    )},
    { title: '源路径', dataIndex: 'sourcePath', key: 'sourcePath', render: (text, _, index) => <Input value={text} onChange={(e) => updateCrossNodeMapping(index, 'sourcePath', e.target.value)} placeholder="$.xxx" /> },
    { title: '目标路径', dataIndex: 'targetPath', key: 'targetPath', render: (text, _, index) => <Input value={text} onChange={(e) => updateCrossNodeMapping(index, 'targetPath', e.target.value)} placeholder="$.xxx" /> },
    { title: '数据类型', dataIndex: 'dataType', key: 'dataType', render: (text, _, index) => (
      <Select value={text} onChange={(val) => updateCrossNodeMapping(index, 'dataType', val)} style={{ width: 100 }}>
        <Option value="string">String</Option>
        <Option value="integer">Integer</Option>
        <Option value="decimal">Decimal</Option>
        <Option value="array">Array</Option>
      </Select>
    )},
    { title: '操作', key: 'action', render: (_, __, index) => <Button type="link" danger icon={<DeleteOutlined />} onClick={() => removeCrossNodeMapping(index)} /> },
  ];

  const items = [
    {
      key: '1',
      label: '字段直接映射',
      children: (
        <div>
          <Button type="dashed" onClick={addDirectMapping} icon={<PlusOutlined />} style={{ marginBottom: 16 }}>
            添加字段映射
          </Button>
          <Table columns={directColumns} dataSource={directMappings} rowKey={(r, i) => i} pagination={false} size="small" />
        </div>
      ),
    },
    {
      key: '2',
      label: '跨节点映射',
      children: (
        <div>
          <Button type="dashed" onClick={addCrossNodeMapping} icon={<PlusOutlined />} style={{ marginBottom: 16 }}>
            添加跨节点映射
          </Button>
          <Table columns={crossNodeColumns} dataSource={crossNodeMappings} rowKey={(r, i) => i} pagination={false} size="small" />
        </div>
      ),
    },
    {
      key: '3',
      label: '数组提取映射',
      children: <div style={{ padding: 24, textAlign: 'center' }}>数组提取规则配置（待扩展）</div>,
    },
    {
      key: '4',
      label: '字符串转数组',
      children: <div style={{ padding: 24, textAlign: 'center' }}>字符串转数组规则配置（待扩展）</div>,
    },
    {
      key: '5',
      label: '预览JSON',
      children: (
        <div>
          <Button onClick={generatePreview} type="primary" style={{ marginBottom: 16 }}>
            生成预览
          </Button>
          <TextArea value={previewJson} rows={20} readOnly style={{ fontFamily: 'monospace' }} />
        </div>
      ),
    },
  ];

  return (
    <div>
      <Tabs activeKey={activeTab} onChange={setActiveTab} items={items} />
    </div>
  );
};

export default DataMappingVisualEditor;

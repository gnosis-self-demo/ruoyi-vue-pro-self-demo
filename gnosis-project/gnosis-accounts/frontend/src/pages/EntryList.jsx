import React, { useState, useEffect } from 'react';
import { Table, Button, Input, Select, Space, Form, message, Tag } from 'antd';
import { ExportOutlined } from '@ant-design/icons';
import { entryApi } from '../api/accountsApi';

const { Option } = Select;

const entryDirectionMap = { DEBIT: '借', CREDIT: '贷' };

const EntryList = () => {
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchForm] = Form.useForm();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async (params = {}) => {
    setLoading(true);
    try {
      const searchValues = searchForm.getFieldsValue();
      const req = {
        pageNum: params.pageNum || 1,
        pageSize: params.pageSize || 10,
        query: { ...searchValues, ...params.query }
      };
      const res = await entryApi.pageList(req);
      if (res.code === 200) {
        setData(res.data.list || []);
        setTotal(res.data.total || 0);
      }
    } catch (error) {
      message.error('查询失败');
    }
    setLoading(false);
  };

  const handleSearch = () => {
    fetchData({ pageNum: 1 });
  };

  const handleReset = () => {
    searchForm.resetFields();
    fetchData({ pageNum: 1 });
  };

  const columns = [
    { title: '流水号', dataIndex: 'journalNo', key: 'journalNo', width: 150 },
    { title: '账户编号', dataIndex: 'accountNo', key: 'accountNo', width: 130 },
    { title: '账户名称', dataIndex: 'accountName', key: 'accountName', width: 120 },
    { title: '科目编码', dataIndex: 'subjectCode', key: 'subjectCode', width: 110 },
    { title: '科目名称', dataIndex: 'subjectName', key: 'subjectName', width: 110 },
    {
      title: '方向', dataIndex: 'entryDirection', key: 'entryDirection', width: 80,
      render: (val) => <Tag color={val === 'DEBIT' ? 'red' : 'green'}>{entryDirectionMap[val] || val}</Tag>,
    },
    {
      title: '金额', dataIndex: 'amount', key: 'amount', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '描述', dataIndex: 'description', key: 'description', ellipsis: true },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  ];

  return (
    <div style={{ padding: 24 }}>
      <h2>分录明细</h2>

      <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
        <Form.Item name="journalNo">
          <Input placeholder="流水号" />
        </Form.Item>
        <Form.Item name="accountId">
          <Input placeholder="账户ID" />
        </Form.Item>
        <Form.Item name="entryDirection">
          <Select placeholder="方向" style={{ width: 100 }} allowClear>
            <Option value="DEBIT">借</Option>
            <Option value="CREDIT">贷</Option>
          </Select>
        </Form.Item>
        <Form.Item>
          <Space>
            <Button type="primary" onClick={handleSearch}>查询</Button>
            <Button onClick={handleReset}>重置</Button>
          </Space>
        </Form.Item>
      </Form>

      <div style={{ marginBottom: 16 }}>
        <Space>
          <Button icon={<ExportOutlined />} onClick={() => message.info('导出功能开发中')}>导出</Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        scroll={{ x: 1200 }}
        pagination={{
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (pageNum, pageSize) => fetchData({ pageNum, pageSize }),
        }}
      />
    </div>
  );
};

export default EntryList;

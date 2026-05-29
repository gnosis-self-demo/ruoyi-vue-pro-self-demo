import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Statistic, Button, Space, Modal, Form, Input, Select, Tag, Table, message } from 'antd';
import { WalletOutlined, FileTextOutlined, SwapOutlined, BankOutlined, TeamOutlined, DollarOutlined, CheckOutlined } from '@ant-design/icons';
import { accountApi, journalApi, clearingApi, transferApi, settlementApi } from '../api/accountsApi';

const { Option } = Select;

const AccountDashboard = () => {
  const [stats, setStats] = useState({ totalAccounts: 0, totalBalance: 0, todayJournals: 0, pendingClearing: 0 });
  const [recentJournals, setRecentJournals] = useState([]);
  const [loading, setLoading] = useState(false);
  const [actionModalVisible, setActionModalVisible] = useState(false);
  const [currentAction, setCurrentAction] = useState(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [accountRes, journalRes, clearingRes] = await Promise.all([
        accountApi.pageList({ pageNum: 1, pageSize: 1, query: {} }),
        journalApi.pageList({ pageNum: 1, pageSize: 10, query: {} }),
        clearingApi.pageList({ pageNum: 1, pageSize: 1, query: { clearingStatus: 'PENDING' } }),
      ]);

      const totalAccounts = accountRes.code === 200 ? (accountRes.data.total || 0) : 0;
      const todayJournals = journalRes.code === 200 ? (journalRes.data.total || 0) : 0;
      const pendingClearing = clearingRes.code === 200 ? (clearingRes.data.total || 0) : 0;

      setStats({
        totalAccounts,
        totalBalance: 0,
        todayJournals,
        pendingClearing,
      });

      if (journalRes.code === 200) {
        setRecentJournals(journalRes.data.list || []);
      }
    } catch (error) {
      console.error('获取看板数据失败', error);
    }
    setLoading(false);
  };

  const handleQuickAction = (action) => {
    setCurrentAction(action);
    form.resetFields();
    setActionModalVisible(true);
  };

  const handleActionSubmit = async () => {
    try {
      const values = await form.validateFields();
      let res;
      switch (currentAction) {
        case 'collection':
          res = await journalApi.create({ ...values, businessType: 'COLLECTION', transactionType: 'ONLINE' });
          break;
        case 'refund':
          res = await journalApi.create({ ...values, businessType: 'REFUND', transactionType: 'ONLINE' });
          break;
        case 'payment':
          res = await journalApi.create({ ...values, businessType: 'PAYMENT', transactionType: 'ONLINE' });
          break;
        case 'clearing':
          res = await clearingApi.create({ ...values, clearingType: 'CHANNEL' });
          break;
        case 'transfer':
          res = await transferApi.create(values);
          break;
        case 'settlement':
          res = await settlementApi.create({ ...values, settlementType: 'CUSTOMER' });
          break;
        default:
          break;
      }
      if (res && res.code === 200) {
        message.success('操作成功');
        setActionModalVisible(false);
        fetchDashboardData();
      } else {
        message.error(res?.msg || '操作失败');
      }
    } catch (error) {
      if (error.errorFields) return;
      message.error('操作失败');
    }
  };

  const actionConfig = {
    collection: { title: '收单记账', fields: ['businessId', 'accountingDate', 'description'] },
    refund: { title: '退款记账', fields: ['businessId', 'accountingDate', 'description'] },
    payment: { title: '付款记账', fields: ['businessId', 'accountingDate', 'description'] },
    clearing: { title: '渠道清算', fields: ['clearingDate', 'channelCode', 'channelName', 'clearingAmount', 'description'] },
    transfer: { title: '银存结转', fields: ['transferDate', 'channelCode', 'channelName', 'transferType', 'transferAmount', 'fromAccountName', 'toAccountName', 'description'] },
    settlement: { title: '客资结算', fields: ['settlementDate', 'customerId', 'customerName', 'settlementAmount', 'feeAmount', 'actualAmount', 'fromAccountName', 'toAccountName', 'description'] },
  };

  const businessTypeMap = { COLLECTION: '收单', REFUND: '退款', PAYMENT: '付款' };
  const businessTypeColorMap = { COLLECTION: 'green', REFUND: 'orange', PAYMENT: 'blue' };
  const statusMap = { DRAFT: '草稿', POSTED: '已入账', REVERSED: '已冲正' };
  const statusColorMap = { DRAFT: 'blue', POSTED: 'green', REVERSED: 'red' };

  const journalColumns = [
    { title: '流水号', dataIndex: 'journalNo', key: 'journalNo', width: 150 },
    {
      title: '业务类型', dataIndex: 'businessType', key: 'businessType', width: 100,
      render: (val) => <Tag color={businessTypeColorMap[val]}>{businessTypeMap[val] || val}</Tag>,
    },
    {
      title: '状态', dataIndex: 'status', key: 'status', width: 90,
      render: (val) => <Tag color={statusColorMap[val]}>{statusMap[val] || val}</Tag>,
    },
    {
      title: '借方合计', dataIndex: 'totalDebit', key: 'totalDebit', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    {
      title: '贷方合计', dataIndex: 'totalCredit', key: 'totalCredit', width: 120,
      render: (val) => val !== undefined && val !== null ? Number(val).toFixed(2) : '0.00',
    },
    { title: '记账日期', dataIndex: 'accountingDate', key: 'accountingDate', width: 120 },
    { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
  ];

  const renderFormFields = () => {
    if (!currentAction) return null;
    const config = actionConfig[currentAction];
    return config.fields.map((field) => {
      const fieldConfig = {
        businessId: { label: '业务ID', required: true },
        accountingDate: { label: '记账日期', required: true },
        description: { label: '描述', required: false },
        clearingDate: { label: '清算日期', required: true },
        channelCode: { label: '渠道编码', required: true },
        channelName: { label: '渠道名称', required: false },
        clearingAmount: { label: '清算金额', required: true },
        transferDate: { label: '结转日期', required: true },
        transferType: { label: '结转类型', required: true },
        transferAmount: { label: '结转金额', required: true },
        fromAccountName: { label: '转出账户', required: false },
        toAccountName: { label: '转入账户', required: false },
        settlementDate: { label: '结算日期', required: true },
        customerId: { label: '客户ID', required: true },
        customerName: { label: '客户名称', required: false },
        settlementAmount: { label: '结算金额', required: true },
        feeAmount: { label: '手续费', required: false },
        actualAmount: { label: '实际金额', required: false },
      }[field];

      if (field === 'transferType') {
        return (
          <Form.Item key={field} name={field} label={fieldConfig.label} rules={[{ required: fieldConfig.required, message: `请选择${fieldConfig.label}` }]}>
            <Select placeholder={`请选择${fieldConfig.label}`}>
              <Option value="INCOME">入款</Option>
              <Option value="EXPENSE">出款</Option>
            </Select>
          </Form.Item>
        );
      }

      if (field === 'description') {
        return (
          <Form.Item key={field} name={field} label={fieldConfig.label}>
            <Input.TextArea rows={3} placeholder={`请输入${fieldConfig.label}`} />
          </Form.Item>
        );
      }

      const isAmount = ['clearingAmount', 'transferAmount', 'settlementAmount', 'feeAmount', 'actualAmount'].includes(field);
      return (
        <Form.Item key={field} name={field} label={fieldConfig.label} rules={[{ required: fieldConfig.required, message: `请输入${fieldConfig.label}` }]}>
          <Input placeholder={`请输入${fieldConfig.label}`} type={isAmount ? 'number' : 'text'} />
        </Form.Item>
      );
    });
  };

  return (
    <div style={{ padding: 24 }}>
      <h2>账务看板</h2>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="账户总数"
              value={stats.totalAccounts}
              prefix={<WalletOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="账户总余额"
              value={stats.totalBalance}
              prefix={<DollarOutlined />}
              precision={2}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="今日流水数"
              value={stats.todayJournals}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#722ed1' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="待清算笔数"
              value={stats.pendingClearing}
              prefix={<SwapOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
      </Row>

      <Card title="快捷操作" style={{ marginBottom: 24 }}>
        <Space size="middle" wrap>
          <Button type="primary" icon={<CheckOutlined />} onClick={() => handleQuickAction('collection')}>收单记账</Button>
          <Button icon={<CheckOutlined />} onClick={() => handleQuickAction('refund')}>退款记账</Button>
          <Button icon={<CheckOutlined />} onClick={() => handleQuickAction('payment')}>付款记账</Button>
          <Button type="primary" icon={<SwapOutlined />} onClick={() => handleQuickAction('clearing')}>渠道清算</Button>
          <Button type="primary" icon={<BankOutlined />} onClick={() => handleQuickAction('transfer')}>银存结转</Button>
          <Button type="primary" icon={<TeamOutlined />} onClick={() => handleQuickAction('settlement')}>客资结算</Button>
        </Space>
      </Card>

      <Card title="最近流水">
        <Table
          columns={journalColumns}
          dataSource={recentJournals}
          rowKey="id"
          size="small"
          pagination={false}
          loading={loading}
        />
      </Card>

      <Modal
        title={currentAction ? actionConfig[currentAction].title : ''}
        open={actionModalVisible}
        onOk={handleActionSubmit}
        onCancel={() => { setActionModalVisible(false); setCurrentAction(null); }}
        width={600}
        destroyOnClose
      >
        <Form form={form} layout="vertical">
          {renderFormFields()}
        </Form>
      </Modal>
    </div>
  );
};

export default AccountDashboard;

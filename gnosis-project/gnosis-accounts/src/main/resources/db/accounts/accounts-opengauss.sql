-- =============================================
-- 账务管理模块 - openGauss 建表脚本
-- =============================================

-- 1. 会计科目表
DROP TABLE IF EXISTS sys_acc_subject;
CREATE TABLE sys_acc_subject (
    id              varchar(128)    NOT NULL,
    subject_code    varchar(64)     NOT NULL,
    subject_name    varchar(128)    NOT NULL,
    subject_type    varchar(32)     NOT NULL,
    subject_category varchar(32)    NOT NULL,
    parent_id       varchar(128)    DEFAULT '',
    level           int             DEFAULT 1,
    balance_direction varchar(16)   NOT NULL,
    description     varchar(512)    DEFAULT '',
    status          int             DEFAULT 1,
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_subject IS '会计科目表';
COMMENT ON COLUMN sys_acc_subject.subject_code IS '科目编码';
COMMENT ON COLUMN sys_acc_subject.subject_name IS '科目名称';
COMMENT ON COLUMN sys_acc_subject.subject_type IS '科目类型:ASSET资产/LIABILITY负债/INCOME收入/EXPENSE支出';
COMMENT ON COLUMN sys_acc_subject.subject_category IS '科目分类:CHANNEL渠道清算/TRANSITION交易过渡/CUSTOMER客户负债/FEE收入费用';
COMMENT ON COLUMN sys_acc_subject.parent_id IS '父科目ID';
COMMENT ON COLUMN sys_acc_subject.level IS '科目层级';
COMMENT ON COLUMN sys_acc_subject.balance_direction IS '余额方向:DEBIT借方/CREDIT贷方';

-- 2. 账户表
DROP TABLE IF EXISTS sys_acc_account;
CREATE TABLE sys_acc_account (
    id              varchar(128)    NOT NULL,
    account_no      varchar(64)     NOT NULL,
    account_name    varchar(128)    NOT NULL,
    subject_id      varchar(128)    NOT NULL,
    account_type    varchar(32)     NOT NULL,
    channel_code    varchar(64)     DEFAULT '',
    customer_id     varchar(128)    DEFAULT '',
    customer_name   varchar(128)    DEFAULT '',
    currency        varchar(16)     DEFAULT 'CNY',
    balance         decimal(18,2)   DEFAULT 0.00,
    frozen_amount   decimal(18,2)   DEFAULT 0.00,
    balance_direction varchar(16)   NOT NULL,
    description     varchar(512)    DEFAULT '',
    status          int             DEFAULT 1,
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_account IS '账户表';
COMMENT ON COLUMN sys_acc_account.account_no IS '账户编号';
COMMENT ON COLUMN sys_acc_account.account_name IS '账户名称';
COMMENT ON COLUMN sys_acc_account.subject_id IS '所属科目ID';
COMMENT ON COLUMN sys_acc_account.account_type IS '账户类型:CHANNEL_CLEARING渠道清算/PENDING_SETTLEMENT待结算/CUSTOMER客户/BANK_DEPOSIT银存/CLEARED已清算/INCOME收入/EXPENSE支出';
COMMENT ON COLUMN sys_acc_account.channel_code IS '渠道编码';
COMMENT ON COLUMN sys_acc_account.customer_id IS '客户ID';
COMMENT ON COLUMN sys_acc_account.customer_name IS '客户名称';
COMMENT ON COLUMN sys_acc_account.currency IS '币种';
COMMENT ON COLUMN sys_acc_account.balance IS '账户余额';
COMMENT ON COLUMN sys_acc_account.frozen_amount IS '冻结金额';
COMMENT ON COLUMN sys_acc_account.balance_direction IS '余额方向:DEBIT借方/CREDIT贷方';

-- 3. 会计流水表
DROP TABLE IF EXISTS sys_acc_journal;
CREATE TABLE sys_acc_journal (
    id              varchar(128)    NOT NULL,
    journal_no      varchar(64)     NOT NULL,
    business_type   varchar(32)     NOT NULL,
    business_id     varchar(128)    DEFAULT '',
    transaction_type varchar(32)    NOT NULL,
    total_debit     decimal(18,2)   DEFAULT 0.00,
    total_credit    decimal(18,2)   DEFAULT 0.00,
    accounting_date date            NOT NULL,
    status          varchar(16)     DEFAULT 'DRAFT',
    description     varchar(512)    DEFAULT '',
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_journal IS '会计流水表';
COMMENT ON COLUMN sys_acc_journal.journal_no IS '流水号';
COMMENT ON COLUMN sys_acc_journal.business_type IS '业务类型:COLLECTION收单/REFUND退款/PAYMENT付款';
COMMENT ON COLUMN sys_acc_journal.business_id IS '关联业务单号';
COMMENT ON COLUMN sys_acc_journal.transaction_type IS '交易类型:ONLINE联机交易/CHANNEL_CLEARING渠道清算/BANK_TRANSFER银存结转/CUSTOMER_SETTLEMENT客资结算';
COMMENT ON COLUMN sys_acc_journal.total_debit IS '借方合计金额';
COMMENT ON COLUMN sys_acc_journal.total_credit IS '贷方合计金额';
COMMENT ON COLUMN sys_acc_journal.accounting_date IS '会计日期';
COMMENT ON COLUMN sys_acc_journal.status IS '状态:DRAFT草稿/POSTED已入账/REVERSED已冲正';

-- 4. 会计分录明细表
DROP TABLE IF EXISTS sys_acc_entry;
CREATE TABLE sys_acc_entry (
    id              varchar(128)    NOT NULL,
    journal_id      varchar(128)    NOT NULL,
    journal_no      varchar(64)     NOT NULL,
    account_id      varchar(128)    NOT NULL,
    account_no      varchar(64)     DEFAULT '',
    account_name    varchar(128)    DEFAULT '',
    subject_code    varchar(64)     DEFAULT '',
    subject_name    varchar(128)    DEFAULT '',
    entry_direction varchar(16)     NOT NULL,
    amount          decimal(18,2)   NOT NULL,
    description     varchar(512)    DEFAULT '',
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_entry IS '会计分录明细表';
COMMENT ON COLUMN sys_acc_entry.journal_id IS '所属流水ID';
COMMENT ON COLUMN sys_acc_entry.journal_no IS '流水号';
COMMENT ON COLUMN sys_acc_entry.account_id IS '账户ID';
COMMENT ON COLUMN sys_acc_entry.account_no IS '账户编号';
COMMENT ON COLUMN sys_acc_entry.account_name IS '账户名称';
COMMENT ON COLUMN sys_acc_entry.subject_code IS '科目编码';
COMMENT ON COLUMN sys_acc_entry.subject_name IS '科目名称';
COMMENT ON COLUMN sys_acc_entry.entry_direction IS '方向:DEBIT借/CREDIT贷';
COMMENT ON COLUMN sys_acc_entry.amount IS '金额';

-- 5. 渠道清算表
DROP TABLE IF EXISTS sys_acc_channel_clearing;
CREATE TABLE sys_acc_channel_clearing (
    id              varchar(128)    NOT NULL,
    clearing_no     varchar(64)     NOT NULL,
    clearing_date   date            NOT NULL,
    channel_code    varchar(64)     NOT NULL,
    channel_name    varchar(128)    DEFAULT '',
    clearing_type   varchar(32)     NOT NULL,
    clearing_amount decimal(18,2)   DEFAULT 0.00,
    clearing_status varchar(16)     DEFAULT 'PENDING',
    journal_id      varchar(128)    DEFAULT '',
    description     varchar(512)    DEFAULT '',
    status          int             DEFAULT 1,
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_channel_clearing IS '渠道清算表';
COMMENT ON COLUMN sys_acc_channel_clearing.clearing_no IS '清算单号';
COMMENT ON COLUMN sys_acc_channel_clearing.clearing_date IS '清算日期';
COMMENT ON COLUMN sys_acc_channel_clearing.channel_code IS '渠道编码';
COMMENT ON COLUMN sys_acc_channel_clearing.channel_name IS '渠道名称';
COMMENT ON COLUMN sys_acc_channel_clearing.clearing_type IS '清算类型:COLLECTION收单/REFUND退款/PAYMENT付款';
COMMENT ON COLUMN sys_acc_channel_clearing.clearing_amount IS '清算金额';
COMMENT ON COLUMN sys_acc_channel_clearing.clearing_status IS '清算状态:PENDING待清算/CLEARED已清算/FAILED失败';
COMMENT ON COLUMN sys_acc_channel_clearing.journal_id IS '关联流水ID';

-- 6. 银存结转表
DROP TABLE IF EXISTS sys_acc_bank_transfer;
CREATE TABLE sys_acc_bank_transfer (
    id              varchar(128)    NOT NULL,
    transfer_no     varchar(64)     NOT NULL,
    transfer_date   date            NOT NULL,
    channel_code    varchar(64)     NOT NULL,
    channel_name    varchar(128)    DEFAULT '',
    transfer_type   varchar(32)     NOT NULL,
    transfer_amount decimal(18,2)   DEFAULT 0.00,
    from_account_id varchar(128)    DEFAULT '',
    from_account_name varchar(128)  DEFAULT '',
    to_account_id   varchar(128)    DEFAULT '',
    to_account_name varchar(128)    DEFAULT '',
    transfer_status varchar(16)     DEFAULT 'PENDING',
    journal_id      varchar(128)    DEFAULT '',
    description     varchar(512)    DEFAULT '',
    status          int             DEFAULT 1,
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_bank_transfer IS '银存结转表';
COMMENT ON COLUMN sys_acc_bank_transfer.transfer_no IS '结转单号';
COMMENT ON COLUMN sys_acc_bank_transfer.transfer_date IS '结转日期';
COMMENT ON COLUMN sys_acc_bank_transfer.channel_code IS '渠道编码';
COMMENT ON COLUMN sys_acc_bank_transfer.channel_name IS '渠道名称';
COMMENT ON COLUMN sys_acc_bank_transfer.transfer_type IS '结转类型:INCOME入款/EXPENSE出款';
COMMENT ON COLUMN sys_acc_bank_transfer.transfer_amount IS '结转金额';
COMMENT ON COLUMN sys_acc_bank_transfer.from_account_id IS '转出账户ID';
COMMENT ON COLUMN sys_acc_bank_transfer.from_account_name IS '转出账户名称';
COMMENT ON COLUMN sys_acc_bank_transfer.to_account_id IS '转入账户ID';
COMMENT ON COLUMN sys_acc_bank_transfer.to_account_name IS '转入账户名称';
COMMENT ON COLUMN sys_acc_bank_transfer.transfer_status IS '结转状态:PENDING待结转/COMPLETED已完成/FAILED失败';

-- 7. 客资结算表
DROP TABLE IF EXISTS sys_acc_customer_settlement;
CREATE TABLE sys_acc_customer_settlement (
    id              varchar(128)    NOT NULL,
    settlement_no   varchar(64)     NOT NULL,
    settlement_date date            NOT NULL,
    customer_id     varchar(128)    NOT NULL,
    customer_name   varchar(128)    DEFAULT '',
    settlement_type varchar(32)     NOT NULL,
    settlement_amount decimal(18,2) DEFAULT 0.00,
    fee_amount      decimal(18,2)   DEFAULT 0.00,
    actual_amount   decimal(18,2)   DEFAULT 0.00,
    from_account_id varchar(128)    DEFAULT '',
    from_account_name varchar(128)  DEFAULT '',
    to_account_id   varchar(128)    DEFAULT '',
    to_account_name varchar(128)    DEFAULT '',
    settlement_status varchar(16)   DEFAULT 'PENDING',
    journal_id      varchar(128)    DEFAULT '',
    description     varchar(512)    DEFAULT '',
    status          int             DEFAULT 1,
    create_user_id  varchar(128)    DEFAULT '',
    update_user_id  varchar(128)    DEFAULT '',
    create_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time     timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_acc_customer_settlement IS '客资结算表';
COMMENT ON COLUMN sys_acc_customer_settlement.settlement_no IS '结算单号';
COMMENT ON COLUMN sys_acc_customer_settlement.settlement_date IS '结算日期';
COMMENT ON COLUMN sys_acc_customer_settlement.customer_id IS '客户ID';
COMMENT ON COLUMN sys_acc_customer_settlement.customer_name IS '客户名称';
COMMENT ON COLUMN sys_acc_customer_settlement.settlement_type IS '结算类型:COLLECTION收单/REFUND退款/PAYMENT付款';
COMMENT ON COLUMN sys_acc_customer_settlement.settlement_amount IS '结算金额';
COMMENT ON COLUMN sys_acc_customer_settlement.fee_amount IS '手续费金额';
COMMENT ON COLUMN sys_acc_customer_settlement.actual_amount IS '实际金额';
COMMENT ON COLUMN sys_acc_customer_settlement.from_account_id IS '转出账户ID';
COMMENT ON COLUMN sys_acc_customer_settlement.from_account_name IS '转出账户名称';
COMMENT ON COLUMN sys_acc_customer_settlement.to_account_id IS '转入账户ID';
COMMENT ON COLUMN sys_acc_customer_settlement.to_account_name IS '转入账户名称';
COMMENT ON COLUMN sys_acc_customer_settlement.settlement_status IS '结算状态:PENDING待结算/SETTLED已结算/FAILED失败';

-- =============================================
-- 初始科目数据
-- =============================================

-- 一级科目
INSERT INTO sys_acc_subject (id, subject_code, subject_name, subject_type, subject_category, parent_id, level, balance_direction, description, status, create_user_id, update_user_id) VALUES
('s1000', '1000', '资产类', 'ASSET', 'CHANNEL', '', 1, 'DEBIT', '资产类科目', 1, 'system', 'system'),
('s2000', '2000', '负债类', 'LIABILITY', 'TRANSITION', '', 1, 'CREDIT', '负债类科目', 1, 'system', 'system'),
('s3000', '3000', '负债类-客户', 'LIABILITY', 'CUSTOMER', '', 1, 'CREDIT', '客户负债类科目', 1, 'system', 'system'),
('s4000', '4000', '收入类', 'INCOME', 'FEE', '', 1, 'CREDIT', '收入类科目', 1, 'system', 'system'),
('s5000', '5000', '支出类', 'EXPENSE', 'FEE', '', 1, 'DEBIT', '支出类科目', 1, 'system', 'system');

-- 二级科目 - 渠道清算(资产)
INSERT INTO sys_acc_subject (id, subject_code, subject_name, subject_type, subject_category, parent_id, level, balance_direction, description, status, create_user_id, update_user_id) VALUES
('s1001', '1001', '待清算收单', 'ASSET', 'CHANNEL', 's1000', 2, 'DEBIT', '待清算收单-渠道侧', 1, 'system', 'system'),
('s1002', '1002', '入款已清算', 'ASSET', 'CHANNEL', 's1000', 2, 'DEBIT', '入款已清算账户', 1, 'system', 'system'),
('s1003', '1003', '银存账户', 'ASSET', 'CHANNEL', 's1000', 2, 'DEBIT', '银行存款账户', 1, 'system', 'system');

-- 二级科目 - 交易过渡(负债)
INSERT INTO sys_acc_subject (id, subject_code, subject_name, subject_type, subject_category, parent_id, level, balance_direction, description, status, create_user_id, update_user_id) VALUES
('s2001', '2001', '收单待结算', 'LIABILITY', 'TRANSITION', 's2000', 2, 'CREDIT', '其他应付-收单待结算', 1, 'system', 'system'),
('s2002', '2002', '退款待结算', 'LIABILITY', 'TRANSITION', 's2000', 2, 'CREDIT', '其他应付-待结算退款', 1, 'system', 'system'),
('s2003', '2003', '付款待结算', 'LIABILITY', 'TRANSITION', 's2000', 2, 'CREDIT', '其他应付-待结算付款', 1, 'system', 'system'),
('s2004', '2004', '待清算付款', 'LIABILITY', 'TRANSITION', 's2000', 2, 'CREDIT', '待清算付款-渠道侧', 1, 'system', 'system'),
('s2005', '2005', '出款已清算', 'LIABILITY', 'TRANSITION', 's2000', 2, 'CREDIT', '出款已清算账户', 1, 'system', 'system');

-- 二级科目 - 客户负债
INSERT INTO sys_acc_subject (id, subject_code, subject_name, subject_type, subject_category, parent_id, level, balance_direction, description, status, create_user_id, update_user_id) VALUES
('s3001', '3001', '商户账户', 'LIABILITY', 'CUSTOMER', 's3000', 2, 'CREDIT', '商户在机构内存放的资金', 1, 'system', 'system');

-- 二级科目 - 收入费用
INSERT INTO sys_acc_subject (id, subject_code, subject_name, subject_type, subject_category, parent_id, level, balance_direction, description, status, create_user_id, update_user_id) VALUES
('s4001', '4001', '手续费收入', 'INCOME', 'FEE', 's4000', 2, 'CREDIT', '从客户一侧收取的手续费收入', 1, 'system', 'system'),
('s5001', '5001', '手续费支出', 'EXPENSE', 'FEE', 's5000', 2, 'DEBIT', '支付给渠道的手续费支出', 1, 'system', 'system');

-- =============================================
-- 初始账户数据（示例渠道A）
-- =============================================

INSERT INTO sys_acc_account (id, account_no, account_name, subject_id, account_type, channel_code, customer_id, customer_name, currency, balance, frozen_amount, balance_direction, description, status, create_user_id, update_user_id) VALUES
('acc_channel_a_pending_collection', 'CH-A-1001', '待清算收单-A渠道', 's1001', 'CHANNEL_CLEARING', 'CHANNEL_A', '', '', 'CNY', 0.00, 0.00, 'DEBIT', 'A渠道待清算收单账户', 1, 'system', 'system'),
('acc_cleared_income', 'CL-1002', '入款已清算', 's1002', 'CLEARED', '', '', '', 'CNY', 0.00, 0.00, 'DEBIT', '入款已清算公共账户', 1, 'system', 'system'),
('acc_channel_a_bank', 'BK-A-1003', '银存账户-A渠道', 's1003', 'BANK_DEPOSIT', 'CHANNEL_A', '', '', 'CNY', 0.00, 0.00, 'DEBIT', 'A渠道银行存款账户', 1, 'system', 'system'),
('acc_pending_collection', 'PS-2001', '收单待结算', 's2001', 'PENDING_SETTLEMENT', '', '', '', 'CNY', 0.00, 0.00, 'CREDIT', '收单待结算过渡账户', 1, 'system', 'system'),
('acc_pending_refund', 'PS-2002', '退款待结算', 's2002', 'PENDING_SETTLEMENT', '', '', '', 'CNY', 0.00, 0.00, 'CREDIT', '退款待结算过渡账户', 1, 'system', 'system'),
('acc_pending_payment', 'PS-2003', '付款待结算', 's2003', 'PENDING_SETTLEMENT', '', '', '', 'CNY', 0.00, 0.00, 'CREDIT', '付款待结算过渡账户', 1, 'system', 'system'),
('acc_channel_a_pending_payment', 'CH-A-2004', '待清算付款-A渠道', 's2004', 'CHANNEL_CLEARING', 'CHANNEL_A', '', '', 'CNY', 0.00, 0.00, 'CREDIT', 'A渠道待清算付款账户', 1, 'system', 'system'),
('acc_cleared_expense', 'CL-2005', '出款已清算', 's2005', 'CLEARED', '', '', '', 'CNY', 0.00, 0.00, 'CREDIT', '出款已清算公共账户', 1, 'system', 'system'),
('acc_merchant_demo', 'CU-3001-001', '商户账户-大聪明电商', 's3001', 'CUSTOMER', '', 'M001', '大聪明电商', 'CNY', 1000.00, 0.00, 'CREDIT', '示例商户账户', 1, 'system', 'system'),
('acc_fee_income', 'IN-4001', '手续费收入', 's4001', 'INCOME', '', '', '', 'CNY', 0.00, 0.00, 'CREDIT', '手续费收入账户', 1, 'system', 'system'),
('acc_fee_expense', 'EX-5001', '手续费支出', 's5001', 'EXPENSE', '', '', '', 'CNY', 0.00, 0.00, 'DEBIT', '手续费支出账户', 1, 'system', 'system');

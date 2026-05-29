# 账务管理模块（gnosis-accounts）建设计划

## 一、需求分析

### 1.1 业务背景

参考文章《图解：四步账务核算》，支付清结算系统通过记录信息流和资金流的账务变动，最终给客户完成结算。核心口诀：**"联机交易、渠道清算、客资结算、银存结转"**。

### 1.2 四步核算流程

| 步骤 | 名称 | 说明 |
|------|------|------|
| 1 | 联机交易 | 记账过程，通过记录订单信息的流转来详细登记账务信息 |
| 2 | 渠道清算 | 渠道侧资金到账后，根据系统登记的收/付/退账务明细核算到账资金是否准确 |
| 3 | 银存结转 | 根据清算的收/付/退发生金额与银存账户进行结算，确保银行存款账户期末余额与渠道侧一致 |
| 4 | 客资结算 | 资金到账后，根据客户一侧的收/付/退交易订单扣除手续费后，给客户结算资金 |

### 1.3 科目体系（四大维度）

| 维度 | 科目类型 | 说明 | 举例 |
|------|----------|------|------|
| 渠道 | 渠道清算科目 | 按每条渠道分配一套账户 | 待清算收单-A渠道、银存账户-A渠道 |
| 交易 | 交易过渡科目 | 临时存放应付资金的过渡账户 | 收单待结算、待结算付款、待结算退款 |
| 客户 | 客户负债科目 | 记录客户在机构内存放的资金 | 商户账户 |
| 费用 | 收入费用科目 | 手续费收入和渠道支付费用 | 手续费收入、手续费支出 |

### 1.4 交易场景

| 场景 | 流程 | 核心分录 |
|------|------|----------|
| 收单 | 消费者刷卡→渠道→待结算→商户 | 借：待清算收单(资产+) 贷：收单待结算(负债+) |
| 退款 | 商户确认→待结算退款→渠道退款 | 借：商户账户(负债-) 贷：待结算收单(负债+) |
| 付款 | 商户提现→待结算付款→渠道付款 | 借：商户账户(负债-) 贷：待结算付款(负债+) 贷：手续费收入(收入+) |

### 1.5 功能要求

每个页面必须包含：分页查询、条件查询、导入、导出、详情、新建、编辑、批量删除、批量启用、批量禁用。

---

## 二、数据库设计（7张核心表）

### 2.1 会计科目表 `sys_acc_subject`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| subject_code | varchar(64) | 科目编码（如1001） |
| subject_name | varchar(128) | 科目名称（如待清算收单） |
| subject_type | varchar(32) | 科目类型：ASSET(资产)/LIABILITY(负债)/INCOME(收入)/EXPENSE(支出) |
| subject_category | varchar(32) | 科目分类：CHANNEL(渠道清算)/TRANSITION(交易过渡)/CUSTOMER(客户负债)/FEE(收入费用) |
| parent_id | varchar(128) | 父科目ID |
| level | int | 科目层级 |
| balance_direction | varchar(16) | 余额方向：DEBIT(借方)/CREDIT(贷方) |
| description | varchar(512) | 描述 |
| status | int | 状态：1启用/0禁用 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.2 账户表 `sys_acc_account`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| account_no | varchar(64) | 账户编号 |
| account_name | varchar(128) | 账户名称 |
| subject_id | varchar(128) | 所属科目ID |
| account_type | varchar(32) | 账户类型：CHANNEL_CLEARING(渠道清算)/PENDING_SETTLEMENT(待结算)/CUSTOMER(客户)/BANK_DEPOSIT(银存)/CLEARED(已清算)/INCOME(收入)/EXPENSE(支出) |
| channel_code | varchar(64) | 渠道编码（渠道类账户必填） |
| customer_id | varchar(128) | 客户ID（客户类账户必填） |
| customer_name | varchar(128) | 客户名称 |
| currency | varchar(16) | 币种（默认CNY） |
| balance | decimal(18,2) | 账户余额 |
| frozen_amount | decimal(18,2) | 冻结金额 |
| balance_direction | varchar(16) | 余额方向：DEBIT(借方)/CREDIT(贷方) |
| description | varchar(512) | 描述 |
| status | int | 状态：1启用/0禁用 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.3 会计流水表 `sys_acc_journal`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| journal_no | varchar(64) | 流水号（自动生成） |
| business_type | varchar(32) | 业务类型：COLLECTION(收单)/REFUND(退款)/PAYMENT(付款) |
| business_id | varchar(128) | 关联业务单号 |
| transaction_type | varchar(32) | 交易类型：ONLINE(联机交易)/CHANNEL_CLEARING(渠道清算)/BANK_TRANSFER(银存结转)/CUSTOMER_SETTLEMENT(客资结算) |
| total_debit | decimal(18,2) | 借方合计金额 |
| total_credit | decimal(18,2) | 贷方合计金额 |
| accounting_date | date | 会计日期 |
| status | varchar(16) | 状态：DRAFT(草稿)/POSTED(已入账)/REVERSED(已冲正) |
| description | varchar(512) | 摘要 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.4 会计分录明细表 `sys_acc_entry`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| journal_id | varchar(128) | 所属流水ID |
| journal_no | varchar(64) | 流水号（冗余） |
| account_id | varchar(128) | 账户ID |
| account_no | varchar(64) | 账户编号 |
| account_name | varchar(128) | 账户名称 |
| subject_code | varchar(64) | 科目编码 |
| subject_name | varchar(128) | 科目名称 |
| entry_direction | varchar(16) | 方向：DEBIT(借)/CREDIT(贷) |
| amount | decimal(18,2) | 金额 |
| description | varchar(512) | 摘要 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.5 渠道清算表 `sys_acc_channel_clearing`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| clearing_no | varchar(64) | 清算单号 |
| clearing_date | date | 清算日期 |
| channel_code | varchar(64) | 渠道编码 |
| channel_name | varchar(128) | 渠道名称 |
| clearing_type | varchar(32) | 清算类型：COLLECTION(收单)/REFUND(退款)/PAYMENT(付款) |
| clearing_amount | decimal(18,2) | 清算金额 |
| clearing_status | varchar(16) | 清算状态：PENDING(待清算)/CLEARED(已清算)/FAILED(失败) |
| journal_id | varchar(128) | 关联流水ID |
| description | varchar(512) | 描述 |
| status | int | 状态：1启用/0禁用 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.6 银存结转表 `sys_acc_bank_transfer`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| transfer_no | varchar(64) | 结转单号 |
| transfer_date | date | 结转日期 |
| channel_code | varchar(64) | 渠道编码 |
| channel_name | varchar(128) | 渠道名称 |
| transfer_type | varchar(32) | 结转类型：INCOME(入款)/EXPENSE(出款) |
| transfer_amount | decimal(18,2) | 结转金额 |
| from_account_id | varchar(128) | 转出账户ID |
| from_account_name | varchar(128) | 转出账户名称 |
| to_account_id | varchar(128) | 转入账户ID |
| to_account_name | varchar(128) | 转入账户名称 |
| transfer_status | varchar(16) | 结转状态：PENDING(待结转)/COMPLETED(已完成)/FAILED(失败) |
| journal_id | varchar(128) | 关联流水ID |
| description | varchar(512) | 描述 |
| status | int | 状态：1启用/0禁用 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

### 2.7 客资结算表 `sys_acc_customer_settlement`

| 字段 | 类型 | 说明 |
|------|------|------|
| id | varchar(128) PK | 主键 |
| settlement_no | varchar(64) | 结算单号 |
| settlement_date | date | 结算日期 |
| customer_id | varchar(128) | 客户ID |
| customer_name | varchar(128) | 客户名称 |
| settlement_type | varchar(32) | 结算类型：COLLECTION(收单)/REFUND(退款)/PAYMENT(付款) |
| settlement_amount | decimal(18,2) | 结算金额 |
| fee_amount | decimal(18,2) | 手续费金额 |
| actual_amount | decimal(18,2) | 实际金额 |
| from_account_id | varchar(128) | 转出账户ID |
| from_account_name | varchar(128) | 转出账户名称 |
| to_account_id | varchar(128) | 转入账户ID |
| to_account_name | varchar(128) | 转入账户名称 |
| settlement_status | varchar(16) | 结算状态：PENDING(待结算)/SETTLED(已结算)/FAILED(失败) |
| journal_id | varchar(128) | 关联流水ID |
| description | varchar(512) | 描述 |
| status | int | 状态：1启用/0禁用 |
| create_user_id | varchar(128) | 创建人ID |
| update_user_id | varchar(128) | 更新人ID |
| create_time | timestamp | 创建时间 |
| update_time | timestamp | 更新时间 |

---

## 三、模块结构设计

### 3.1 后端目录结构

```
gnosis-accounts/
├── pom.xml
├── src/main/java/com/gnosis/accounts/
│   ├── AccountsApplication.java                    # 启动类（端口8087）
│   ├── config/
│   │   ├── MybatisConfig.java                      # MyBatis配置
│   │   └── SwaggerConfig.java                      # Swagger配置
│   ├── domain/
│   │   ├── AccSubject.java                         # 会计科目实体
│   │   ├── AccAccount.java                         # 账户实体
│   │   ├── AccJournal.java                         # 会计流水实体
│   │   ├── AccEntry.java                           # 会计分录实体
│   │   ├── AccChannelClearing.java                 # 渠道清算实体
│   │   ├── AccBankTransfer.java                    # 银存结转实体
│   │   └── AccCustomerSettlement.java              # 客资结算实体
│   ├── dto/
│   │   ├── subject/
│   │   │   ├── AccSubjectCreateRequest.java
│   │   │   ├── AccSubjectUpdateRequest.java
│   │   │   ├── AccSubjectQueryRequest.java
│   │   │   ├── AccSubjectIdsRequest.java
│   │   │   └── AccSubjectVO.java
│   │   ├── account/
│   │   │   ├── AccAccountCreateRequest.java
│   │   │   ├── AccAccountUpdateRequest.java
│   │   │   ├── AccAccountQueryRequest.java
│   │   │   ├── AccAccountIdsRequest.java
│   │   │   └── AccAccountVO.java
│   │   ├── journal/
│   │   │   ├── AccJournalCreateRequest.java
│   │   │   ├── AccJournalQueryRequest.java
│   │   │   ├── AccJournalIdsRequest.java
│   │   │   └── AccJournalVO.java
│   │   ├── entry/
│   │   │   ├── AccEntryQueryRequest.java
│   │   │   └── AccEntryVO.java
│   │   ├── clearing/
│   │   │   ├── AccChannelClearingCreateRequest.java
│   │   │   ├── AccChannelClearingUpdateRequest.java
│   │   │   ├── AccChannelClearingQueryRequest.java
│   │   │   ├── AccChannelClearingIdsRequest.java
│   │   │   └── AccChannelClearingVO.java
│   │   ├── transfer/
│   │   │   ├── AccBankTransferCreateRequest.java
│   │   │   ├── AccBankTransferUpdateRequest.java
│   │   │   ├── AccBankTransferQueryRequest.java
│   │   │   ├── AccBankTransferIdsRequest.java
│   │   │   └── AccBankTransferVO.java
│   │   └── settlement/
│   │       ├── AccCustomerSettlementCreateRequest.java
│   │       ├── AccCustomerSettlementUpdateRequest.java
│   │       ├── AccCustomerSettlementQueryRequest.java
│   │       ├── AccCustomerSettlementIdsRequest.java
│   │       └── AccCustomerSettlementVO.java
│   ├── mapper/
│   │   ├── AccSubjectMapper.java
│   │   ├── AccAccountMapper.java
│   │   ├── AccJournalMapper.java
│   │   ├── AccEntryMapper.java
│   │   ├── AccChannelClearingMapper.java
│   │   ├── AccBankTransferMapper.java
│   │   └── AccCustomerSettlementMapper.java
│   ├── service/
│   │   ├── AccSubjectService.java
│   │   ├── AccAccountService.java
│   │   ├── AccJournalService.java
│   │   ├── AccEntryService.java
│   │   ├── AccChannelClearingService.java
│   │   ├── AccBankTransferService.java
│   │   ├── AccCustomerSettlementService.java
│   │   ├── AccImportExportService.java              # 导入导出服务
│   │   └── AccAccountingEngineService.java          # 账务引擎服务（核心）
│   ├── controller/
│   │   ├── AccSubjectController.java
│   │   ├── AccAccountController.java
│   │   ├── AccJournalController.java
│   │   ├── AccEntryController.java
│   │   ├── AccChannelClearingController.java
│   │   ├── AccBankTransferController.java
│   │   └── AccCustomerSettlementController.java
│   └── api/                                          # 对外API类（供系统内部调用）
│       └── AccountsApi.java                          # 简单好用的API入口
├── src/main/resources/
│   ├── application.properties
│   ├── db/accounts/
│   │   └── accounts-opengauss.sql                    # 建表+初始数据
│   └── mapper/accounts/
│       ├── AccSubjectMapper.xml
│       ├── AccAccountMapper.xml
│       ├── AccJournalMapper.xml
│       ├── AccEntryMapper.xml
│       ├── AccChannelClearingMapper.xml
│       ├── AccBankTransferMapper.xml
│       └── AccCustomerSettlementMapper.xml
└── frontend/
    ├── index.html
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── index.css
        ├── api/
        │   └── accountsApi.js
        ├── utils/
        │   └── request.js
        └── pages/
            ├── SubjectManage.jsx                      # 科目管理页面
            ├── AccountManage.jsx                      # 账户管理页面
            ├── JournalList.jsx                        # 会计流水页面
            ├── EntryList.jsx                          # 分录明细页面
            ├── ChannelClearing.jsx                    # 渠道清算页面
            ├── BankTransfer.jsx                       # 银存结转页面
            ├── CustomerSettlement.jsx                 # 客资结算页面
            └── AccountDashboard.jsx                   # 账务看板页面
```

### 3.2 核心API类设计（AccountsApi）

`AccountsApi` 是供系统内部调用的简单、好用的API类，封装了账务核心操作：

```java
public class AccountsApi {

    // ========== 联机交易记账 ==========

    /** 收单记账 - 一步完成收单联机交易 */
    AccJournalVO collectionEntry(String channelCode, String customerId,
                                  BigDecimal amount, BigDecimal fee,
                                  String businessId, String operatorId);

    /** 退款记账 - 一步完成退款联机交易 */
    AccJournalVO refundEntry(String channelCode, String customerId,
                              BigDecimal amount, String businessId, String operatorId);

    /** 付款记账 - 一步完成付款联机交易 */
    AccJournalVO paymentEntry(String channelCode, String customerId,
                               BigDecimal amount, BigDecimal fee,
                               String businessId, String operatorId);

    // ========== 渠道清算 ==========

    /** 执行渠道日终清算 */
    List<AccChannelClearingVO> executeChannelClearing(String channelCode, Date clearingDate, String operatorId);

    // ========== 银存结转 ==========

    /** 执行银存结转 */
    List<AccBankTransferVO> executeBankTransfer(String channelCode, Date transferDate, String operatorId);

    // ========== 客资结算 ==========

    /** 执行客资结算 */
    List<AccCustomerSettlementVO> executeCustomerSettlement(String customerId, Date settlementDate, String operatorId);

    // ========== 账户查询 ==========

    /** 查询账户余额 */
    BigDecimal getAccountBalance(String accountId);

    /** 查询客户账户余额 */
    BigDecimal getCustomerBalance(String customerId);

    /** 查询渠道待清算金额 */
    BigDecimal getChannelPendingAmount(String channelCode, String clearingType);

    // ========== 通用记账 ==========

    /** 自定义多借多贷记账 */
    AccJournalVO customEntry(List<EntryItem> debitEntries, List<EntryItem> creditEntries,
                              String businessType, String businessId, String operatorId);
}
```

其中 `EntryItem` 为内部DTO：
```java
public class EntryItem {
    private String accountId;
    private BigDecimal amount;
    private String description;
}
```

---

## 四、实施步骤

### 阶段一：基础框架搭建（步骤1-5）

1. **创建 gnosis-accounts 模块骨架**
   - 创建模块目录和 pom.xml
   - 在父 pom.xml 中注册模块
   - 创建启动类 AccountsApplication.java
   - 创建配置类 MybatisConfig.java、SwaggerConfig.java
   - 创建 application.properties

2. **创建数据库脚本**
   - 编写 accounts-opengauss.sql（7张表 + 初始科目数据）
   - 初始科目数据包括：渠道清算类、交易过渡类、客户负债类、收入费用类

3. **创建 Domain 实体层**
   - 7个实体类，均继承 BaseEntity

4. **创建 DTO 层**
   - 每个实体的 Create/Update/Query/Ids/VO 类

5. **创建 Mapper 层**
   - 7个 Mapper 接口 + 7个 XML 文件
   - 每个包含：insert、updateById、selectById、selectByCondition、countByCondition、deleteById、deleteByIds、batchUpdateStatus

### 阶段二：业务服务实现（步骤6-9）

6. **创建基础 Service 层**
   - AccSubjectService - 科目管理CRUD
   - AccAccountService - 账户管理CRUD + 余额操作
   - AccJournalService - 流水管理CRUD + 入账/冲正
   - AccEntryService - 分录查询
   - AccImportExportService - 导入导出

7. **创建账务引擎服务 AccAccountingEngineService**
   - 联机交易记账（收单/退款/付款）
   - 渠道清算逻辑
   - 银存结转逻辑
   - 客资结算逻辑
   - 余额更新逻辑
   - 借贷平衡校验

8. **创建核心API类 AccountsApi**
   - 封装 AccAccountingEngineService 的方法
   - 提供简单易用的方法签名
   - 供系统内部其他模块调用

9. **创建 Controller 层**
   - 7个 Controller，每个包含标准CRUD接口
   - AccJournalController 额外包含入账/冲正接口
   - AccChannelClearingController 额外包含执行清算接口
   - AccBankTransferController 额外包含执行结转接口
   - AccCustomerSettlementController 额外包含执行结算接口

### 阶段三：前端开发（步骤10-12）

10. **创建前端项目骨架**
    - 初始化 React + Vite 项目
    - 配置 vite.config.js（端口3004，代理8087）
    - 创建 request.js、accountsApi.js

11. **创建前端页面**
    - SubjectManage.jsx - 科目管理（树形结构+CRUD）
    - AccountManage.jsx - 账户管理（列表+CRUD）
    - JournalList.jsx - 会计流水（列表+详情+入账/冲正）
    - EntryList.jsx - 分录明细（列表+查询）
    - ChannelClearing.jsx - 渠道清算（列表+执行清算）
    - BankTransfer.jsx - 银存结转（列表+执行结转）
    - CustomerSettlement.jsx - 客资结算（列表+执行结算）
    - AccountDashboard.jsx - 账务看板（汇总统计）

12. **创建前端入口和路由**
    - App.jsx（侧边栏导航 + 路由）
    - main.jsx（Ant Design中文配置）

### 阶段四：验证与完善（步骤13-14）

13. **编译验证**
    - Maven 编译通过
    - 前端构建通过
    - 修复所有编译错误

14. **初始化数据验证**
    - SQL 脚本可正确执行
    - 初始科目数据完整

---

## 五、技术约束

- Java 1.8，不使用高版本语言特性
- Spring Boot 2.1.10.RELEASE
- MyBatis-Plus 3.5.4，使用 mapper.xml 管理SQL
- 数据库 openGauss 3.0.0，兼容 PostgreSQL 方言
- 所有 Controller 使用 POST 请求，请求/响应封装为对象
- 所有实体继承 BaseEntity（id/createUserId/updateUserId/createTime/updateTime）
- 所有列表页面包含创建人ID、创建时间、更新人ID、更新时间
- 禁止存储过程，所有逻辑用 Java 代码实现
- 前端 React + Ant Design 5 + Vite

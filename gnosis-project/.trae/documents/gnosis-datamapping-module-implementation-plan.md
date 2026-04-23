# 数据规则映射模块（gnosis-datamapping）实施计划

## 一、模块概述

新建 `gnosis-datamapping` 模块，实现数据规则映射配置系统，支持字段级映射、跨节点映射、数组处理、字符串解析、批量数据处理和数据增强等核心功能。

***

## 二、数据库设计

### 2.1 数据映射配置表 `sys_data_mapping_config`

| 字段名              | 类型              | 说明            |
| ---------------- | --------------- | ------------- |
| id               | varchar(128) PK | 主键            |
| config\_name     | varchar(256)    | 配置名称          |
| config\_code     | varchar(128)    | 配置编码（唯一）      |
| system\_id       | varchar(128)    | 系统ID          |
| system\_name     | varchar(256)    | 系统名称          |
| config\_version  | varchar(64)     | 配置版本          |
| api\_version     | varchar(64)     | API版本         |
| target\_endpoint | varchar(512)    | 目标接口地址        |
| json\_root\_path | varchar(512)    | JSON根路径       |
| config\_json     | text            | 完整配置JSON      |
| description      | varchar(1024)   | 描述            |
| status           | int             | 状态（0=禁用，1=启用） |
| create\_user\_id | varchar(128)    | 创建人ID         |
| update\_user\_id | varchar(128)    | 更新人ID         |
| create\_time     | timestamp       | 创建时间          |
| update\_time     | timestamp       | 更新时间          |

### 2.2 数据映射执行日志表 `sys_data_mapping_execution_log`

| 字段名              | 类型              | 说明       |
| ---------------- | --------------- | -------- |
| id               | varchar(128) PK | 主键       |
| config\_id       | varchar(128)    | 配置ID     |
| config\_code     | varchar(128)    | 配置编码     |
| request\_json    | text            | 请求JSON   |
| result\_json     | text            | 结果JSON   |
| config\_json     | text            | 完整配置JSON      |
| success          | boolean         | 是否成功     |
| error\_info      | text            | 错误信息     |
| execution\_time  | int             | 执行耗时(ms) |
| create\_user\_id | varchar(128)    | 创建人ID    |
| update\_user\_id | varchar(128)    | 更新人ID    |
| create\_time     | timestamp       | 创建时间     |
| update\_time     | timestamp       | 更新时间     |

### 2.3 SQL文件

创建 `gnosis-datamapping/src/main/resources/db/datamapping/datamapping-opengauss.sql`

***

## 三、后端实现

### 3.1 模块基础结构

创建 `gnosis-datamapping` 模块，目录结构如下：

```
gnosis-datamapping/
├── pom.xml
├── frontend/               # React前端
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── pages/
│   │   └── utils/
│   ├── package.json
│   └── vite.config.js
└── src/
    └── main/
        ├── java/com/gnosis/datamapping/
        │   ├── config/          # 配置类
        │   ├── controller/      # 控制器
        │   ├── domain/          # 实体类
        │   ├── dto/             # 请求/响应对象
        │   ├── mapper/          # MyBatis Mapper接口
        │   ├── service/         # 服务层
        │   ├── engine/          # 映射引擎核心
        │   ├── validator/       # 校验引擎
        │   ├── transform/       # 转换规则引擎
        │   └── DatamappingApplication.java
        └── resources/
            ├── db/              # 数据库脚本
            ├── mapper/          # MyBatis XML
            └── application.properties
```

### 3.2 pom.xml

参考 `gnosis-paramcheck` 的依赖配置，新增：

* Apache POI（导入导出）

* json-path（JSON路径解析）

### 3.3 Domain 实体类

* `DataMappingConfig.java` - 映射配置实体

* `DataMappingExecutionLog.java` - 执行日志实体

* 继承 `BaseEntity`（包含 create\_user\_id、update\_user\_id、create\_time、update\_time）

### 3.4 DTO 对象

**请求对象：**

* `DataMappingConfigCreateRequest.java` - 新建请求

* `DataMappingConfigUpdateRequest.java` - 编辑请求

* `DataMappingConfigQueryRequest.java` - 分页条件查询请求

* `DataMappingConfigIdsRequest.java` - 批量操作请求

* `DataMappingTestRequest.java` - 模拟器测试请求（输入JSON + 配置ID）

* `DataMappingExecuteRequest.java` - API执行请求

**响应对象：**

* `DataMappingConfigVO.java` - 配置详情

* `DataMappingExecuteResponse.java` - 执行结果响应

  ```java
  {
    boolean success;              // 是否成功
    Object resultData;            // 转换后的数据
    List<ErrorDetail> errors;     // 错误信息
    List<ErrorDetail> validationErrors; // 校验失败信息
  }
  ```

**ErrorDetail.java：**

```java
{
  String field;
  String rule;
  String message;
  Object value;
}
```

### 3.5 Mapper 接口和 XML

* `DataMappingConfigMapper.java` + `DataMappingConfigMapper.xml`

* `DataMappingExecutionLogMapper.java` + `DataMappingExecutionLogMapper.xml`

### 3.6 Service 层

* `DataMappingConfigService.java` - 配置CRUD服务

* `DataMappingExecutionLogService.java` - 日志服务

* `DataMappingEngineService.java` - 映射引擎核心服务

### 3.7 映射引擎核心（engine 包）

**核心组件：**

1. **MappingEngine.java** - 映射引擎入口

   * 解析JSON配置

   * 按配置顺序执行各类型映射规则

   * 返回执行结果

2. **DirectMappingExecutor.java** - 字段直接映射执行器

   * 按字段名匹配

   * 类型转换（智能转换：`'1'` → `Integer` 成功，`'a'` → `Integer` 失败但可降级）

   * 必填校验、合法性校验

3. **CrossNodeMappingExecutor.java** - 跨节点映射执行器

   * 支持 `rootToNode`、`nodeToRoot`、`nodeToNode` 三种方向

   * 使用 JSONPath 读写任意层级节点

4. **ListExtractionExecutor.java** - 数组提取执行器

   * 按索引提取（`extractByIndex`）

   * 按条件筛选（`extractByCondition`）

   * 值拼接（`concatenateValues`、`concatenateWithFormat`）

   * 过滤转换（`filterAndTransform`）

5. **StringToListExecutor.java** - 字符串转数组执行器

   * 分隔符拆分（`stringSplitToList`）

   * 正则拆分（`stringSplitWithPattern`）

   * 格式化拆分（`stringSplitWithFormat`）

   * 键值对拆分（`stringSplitWithKeyValue`）

   * 链式规则处理（`chainedMappings`）

6. **BatchProcessingExecutor.java** - 批量处理执行器

   * 支持 `forEachMappings`、`batchProcessing`、`arrayProcessingConfig` 三种模式

   * 成功/失败分离

   * 并行处理支持

### 3.8 校验引擎（validator 包）

**ValidationEngine.java** - 校验引擎

* 按校验规则顺序执行校验

* 支持规则类型：`required`、`maxLength`、`minLength`、`pattern`、`min`、`max`、`enum`、`dateFormat`、`dateRange`、`boolean`、`integer`、`decimalPrecision`

### 3.9 转换规则引擎（transform 包）

**TransformEngine.java** - 转换规则引擎

* 函数转换：`trim`、`uppercase`、`round(precision)`、`dateFormat`

* 查找映射（lookup）：键值映射表，支持忽略大小写、默认值

* 正则替换（regex）：正则匹配与替换

### 3.10 Controller 层

所有接口使用 POST 请求，请求/响应封装为对象：

* `DataMappingConfigController.java`

  * `/datamapping/config/page` - 分页查询

  * `/datamapping/config/detail` - 详情

  * `/datamapping/config/create` - 新建

  * `/datamapping/config/update` - 编辑

  * `/datamapping/config/delete` - 删除

  * `/datamapping/config/batchDelete` - 批量删除

  * `/datamapping/config/batchEnable` - 批量启用

  * `/datamapping/config/batchDisable` - 批量禁用

  * `/datamapping/config/export` - 导出

  * `/datamapping/config/import` - 导入

  * `/datamapping/config/execute` - 执行映射（API调用）

  * `/datamapping/config/test` - 模拟器测试

* `DataMappingExecutionLogController.java`

  * `/datamapping/log/page` - 日志分页查询

***

## 四、前端实现（React）

### 4.1 页面结构

```
frontend/src/pages/
├── DataMappingConfigList.jsx      # 配置列表页
├── DataMappingConfigForm.jsx      # 新建/编辑表单
├── DataMappingConfigDetail.jsx    # 配置详情
├── DataMappingVisualEditor.jsx    # 可视化配置编辑器
├── DataMappingSimulator.jsx       # 模拟器页面
├── DataMappingExecuteHistory.jsx  # 执行历史
└── DataMappingApiManagement.jsx   # API管理页面
```

### 4.2 核心页面功能

**DataMappingConfigList.jsx**

* 分页查询列表

* 所有字段的条件查询

* 新建/编辑/删除/详情

* 批量删除/启用/禁用

* 导入/导出

**DataMappingVisualEditor.jsx（可视化配置页面）**

* 树形结构展示JSON配置

* 拖拽方式添加映射规则

* 可视化配置字段映射（源字段→目标字段）

* 配置校验规则（下拉选择校验类型）

* 配置转换规则

* 配置跨节点映射

* 配置数组提取规则

* 配置字符串转数组规则

* 实时预览生成的JSON配置

* 保存/加载JSON配置文件

**DataMappingSimulator.jsx（模拟器页面）**

* 左侧：输入JSON文本框

* 中间：选择映射规则（下拉选择已配置的规则）

* 右侧：输出结果

  * 成功数据展示区

  * 转换错误展示区（单独对象）

  * 校验失败展示区（单独对象）

* 提供一键测试按钮

**DataMappingApiManagement.jsx（API管理页面）**

* 查看已配置的API列表

* 在线测试功能

* 查看API调用历史

* 查看执行结果统计

### 4.3 API 服务层

```javascript
// frontend/src/api/dataMappingApi.js
export const dataMappingApi = {
  pageList: '/datamapping/config/page',
  detail: '/datamapping/config/detail',
  create: '/datamapping/config/create',
  update: '/datamapping/config/update',
  delete: '/datamapping/config/delete',
  batchDelete: '/datamapping/config/batchDelete',
  batchEnable: '/datamapping/config/batchEnable',
  batchDisable: '/datamapping/config/batchDisable',
  export: '/datamapping/config/export',
  import: '/datamapping/config/import',
  execute: '/datamapping/config/execute',
  test: '/datamapping/config/test',
};
```

***

## 五、配置示例

### 5.1 场景1：字段名直接匹配

```json
{
  "configMetadata": {
    "systemId": "ERP_CONTRACT",
    "systemName": "ERP合同系统",
    "configVersion": "1.0",
    "lastModified": "2024-04-23T16:40:00"
  },
  "apiConfig": {
    "apiVersion": "v1.0",
    "targetEndpoint": "/api/v1/contracts/create",
    "jsonRootPath": "$.contractData"
  },
  "directMappings": [
    {
      "sourceField": "contractNo",
      "targetField": "contractNo",
      "dataType": "string",
      "required": true,
      "validationRules": [
        {"type": "required", "message": "合同编号不能为空"},
        {"type": "maxLength", "value": 50, "message": "合同编号长度不能超过50"},
        {"type": "pattern", "value": "^[A-Z0-9_-]+$", "message": "合同编号只能包含大写字母、数字、下划线和横线"}
      ],
      "transforms": ["trim", "uppercase"]
    },
    {
      "sourceField": "contractAmount",
      "targetField": "amount",
      "dataType": "decimal",
      "required": true,
      "validationRules": [
        {"type": "required", "message": "合同金额不能为空"},
        {"type": "min", "value": 0.01, "message": "合同金额必须大于0"}
      ],
      "transforms": ["round(2)"]
    }
  ],
  "transformRules": {
    "trim": {"type": "function", "function": "String.trim()"},
    "uppercase": {"type": "function", "function": "String.toUpperCase()"},
    "round": {"type": "function", "function": "Math.round(value, precision)", "parameters": {"precision": 2}}
  },
  "validationEngine": {"stopOnFirstError": false, "collectAllErrors": true}
}
```

**转换示例：**

```json
// 输入
{"contractData": {"contractNo": " ht2024001 ", "contractAmount": 1500000.567}}
// 输出
{"contractNo": "HT2024001", "amount": 1500000.57}
```

### 5.2 场景2：根节点→指定节点

```json
{
  "apiConfig": {"jsonRootPath": "$"},
  "crossNodeMappings": [
    {
      "direction": "rootToNode",
      "sourcePath": "$.contractNo",
      "targetPath": "$.basicInfo.contractNo",
      "dataType": "string",
      "transforms": ["trim"]
    },
    {
      "direction": "rootToNode",
      "sourcePath": "$.contractAmount",
      "targetPath": "$.paymentInfo.amount",
      "dataType": "decimal",
      "transforms": ["round(2)"]
    }
  ]
}
```

**转换示例：**

```json
// 输入
{"contractNo": "HT2024001", "contractAmount": 1500000.567}
// 输出
{"basicInfo": {"contractNo": "HT2024001"}, "paymentInfo": {"amount": 1500000.57}}
```

### 5.3 场景3：指定节点→根节点

```json
{
  "apiConfig": {"jsonRootPath": "$"},
  "crossNodeMappings": [
    {
      "direction": "nodeToRoot",
      "sourcePath": "$.basicInfo.contractName",
      "targetPath": "$.contractName",
      "dataType": "string"
    },
    {
      "direction": "nodeToRoot",
      "sourcePath": "$.paymentInfo.totalAmount",
      "targetPath": "$.totalAmount",
      "dataType": "decimal",
      "transforms": ["round(2)"]
    }
  ]
}
```

**转换示例：**

```json
// 输入
{"basicInfo": {"contractName": "2024年度采购框架合同"}, "paymentInfo": {"totalAmount": 1650000.00}}
// 输出
{"contractName": "2024年度采购框架合同", "totalAmount": 1650000.00}
```

### 5.4 场景4：List数据提取

```json
{
  "apiConfig": {"jsonRootPath": "$.contractData"},
  "listExtractionMappings": [
    {
      "type": "extractByIndex",
      "sourcePath": "$.signatories[*]",
      "targetPath": "$.primaryParty",
      "index": 0,
      "itemMappings": [
        {"source": "$.partyName", "target": "$.partyName", "dataType": "string", "transforms": ["trim"]},
        {"source": "$.partyType", "target": "$.partyType", "dataType": "string", "transforms": ["partyTypeMapping"]}
      ]
    },
    {
      "type": "concatenateValues",
      "sourcePath": "$.signatories[*].partyName",
      "targetPath": "$.allPartyNames",
      "separator": "、"
    }
  ],
  "transformRules": {
    "partyTypeMapping": {
      "type": "lookup",
      "mapping": {"甲方": "PRINCIPAL", "乙方": "VENDOR"},
      "defaultValue": "OTHER"
    },
    "trim": {"type": "function", "function": "String.trim()"}
  }
}
```

**转换示例：**

```json
// 输入
{"contractData": {"signatories": [{"partyName": "XX科技", "partyType": "甲方"}, {"partyName": "YY供应商", "partyType": "乙方"}]}}
// 输出
{"primaryParty": {"partyName": "XX科技", "partyType": "PRINCIPAL"}, "allPartyNames": "XX科技、YY供应商"}
```

### 5.5 场景5：String转List + 链式规则

```json
{
  "apiConfig": {"jsonRootPath": "$.contractData"},
  "stringToListMappings": [
    {
      "type": "stringSplitToList",
      "sourcePath": "$.partyNames",
      "targetPath": "$.parties",
      "separator": ",",
      "trimItems": true,
      "removeEmpty": true,
      "chainedMappings": [
        {
          "type": "enrichListItems",
          "enrichmentSource": "partyInfoMapping",
          "keyField": "itemValue",
          "targetFields": ["partyType", "creditCode"]
        }
      ]
    }
  ],
  "enrichmentMappings": {
    "partyInfoMapping": {
      "type": "lookup",
      "dataSource": "partyInfo",
      "keyField": "partyName",
      "mappingFields": {"partyType": "partyType", "creditCode": "creditCode"},
      "defaultValues": {"partyType": "OTHER", "creditCode": ""}
    }
  },
  "dataSources": {
    "partyInfo": {
      "type": "static",
      "staticData": {
        "XX科技": {"partyType": "PRINCIPAL", "creditCode": "91110108MA1234567X"},
        "YY供应商": {"partyType": "VENDOR", "creditCode": "91110108MA7654321Y"}
      }
    }
  }
}
```

**转换示例：**

```json
// 输入
{"contractData": {"partyNames": "XX科技, YY供应商, 未知公司"}}
// 输出
{"parties": [
  {"partyName": "XX科技", "partyType": "PRINCIPAL", "creditCode": "91110108MA1234567X"},
  {"partyName": "YY供应商", "partyType": "VENDOR", "creditCode": "91110108MA7654321Y"},
  {"partyName": "未知公司", "partyType": "OTHER", "creditCode": ""}
]}
```

***

## 六、实施步骤

### Step 1: 创建模块骨架

* 创建 `gnosis-datamapping` 目录结构

* 编写 `pom.xml`

* 更新父 `pom.xml` 添加模块

* 创建 `DatamappingApplication.java` 和 `application.properties`

### Step 2: 数据库设计

* 创建 `datamapping-opengauss.sql` 建表脚本

### Step 3: Domain + DTO 层

* 创建实体类 `DataMappingConfig`、`DataMappingExecutionLog`

* 创建请求/响应 DTO

### Step 4: Mapper 层

* 创建 Mapper 接口

* 创建 Mapper XML

### Step 5: Service 层

* 配置 CRUD 服务

* 执行日志服务

### Step 6: 映射引擎核心

* `MappingEngine` 引擎入口

* `DirectMappingExecutor` 字段直接映射

* `CrossNodeMappingExecutor` 跨节点映射

* `ListExtractionExecutor` 数组提取

* `StringToListExecutor` 字符串转数组

* `BatchProcessingExecutor` 批量处理

### Step 7: 校验引擎

* `ValidationEngine` 校验引擎

### Step 8: 转换规则引擎

* `TransformEngine` 转换规则引擎

### Step 9: Controller 层

* `DataMappingConfigController`

* `DataMappingExecutionLogController`

### Step 10: 前端实现

* React 配置列表页

* React 可视化配置编辑器

* React 模拟器页面

* React API管理页面

### Step 11: 导入导出功能

* 基于 Apache POI 实现 Excel 导入导出

### Step 12: 测试验证

* 单元测试

* 功能测试


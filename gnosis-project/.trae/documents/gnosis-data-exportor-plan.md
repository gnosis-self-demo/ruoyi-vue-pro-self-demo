# 数据导入导出模块（gnosis-data-exportor）建设计划

## 一、总体架构

```
gnosis-data-exportor/
├── pom.xml
├── src/main/java/com/gnosis/dataexportor/
│   ├── DataExportorApplication.java          # 启动类
│   ├── config/
│   │   └── DataSourceConfig.java             # openGauss数据源配置
│   ├── controller/
│   │   ├── ExcelExportController.java        # 导出接口
│   │   └── ExcelImportController.java        # 导入接口
│   ├── service/
│   │   ├── ExcelExportService.java           # 导出服务接口
│   │   ├── ExcelImportService.java           # 导入服务接口
│   │   └── impl/
│   │       ├── ExcelExportServiceImpl.java   # 导出服务实现
│   │       └── ExcelImportServiceImpl.java   # 导入服务实现
│   ├── listener/
│   │   ├── ProgressListener.java             # 进度监听器接口
│   │   └── LoggingProgressListener.java      # 日志进度监听器
│   ├── dto/
│   │   ├── ExportRequest.java                # 导出请求DTO
│   │   ├── ExportResponse.java               # 导出响应DTO
│   │   ├── ImportRequest.java                # 导入请求DTO
│   │   ├── ImportResult.java                 # 导入结果DTO
│   │   └── ErrorRecord.java                  # 错误记录DTO
│   ├── calculator/
│   │   └── BatchSizeCalculator.java          # 批次大小自动计算器
│   └── util/
│       ├── SqlUtils.java                     # SQL工具（COUNT转换、ORDER BY检测）
│       └── ExcelUtils.java                   # Excel工具（单元格值读取等）
└── src/main/resources/
    ├── application.properties                # 应用配置
    └── mapper/                               # MyBatis XML（如需）
```

## 二、详细实施步骤

### 步骤1：创建模块骨架

#### 1.1 在父POM中注册新模块
- 文件：`gnosis-project/pom.xml`
- 在 `<modules>` 中添加 `<module>gnosis-data-exportor</module>`

#### 1.2 创建 `gnosis-data-exportor/pom.xml`
- parent: `com.gnosis:gnosis-project:1.0.0-SNAPSHOT`
- artifactId: `gnosis-data-exportor`
- 依赖项（版本继承父POM `dependencyManagement`）：
  - `gnosis-common`
  - `spring-boot-starter-web`
  - `spring-boot-starter-jdbc`
  - `mybatis-plus-boot-starter`
  - `opengauss-jdbc`
  - `lombok`
  - `hutool-core`
  - `fastjson`
  - `poi` + `poi-ooxml`（版本 4.1.2，用于 SXSSFWorkbook / XSSFWorkbook）
  - `swagger2` + `swagger-ui`
  - `spring-boot-starter-test`（test scope）
- 构建插件：`spring-boot-maven-plugin`（mainClass 指向 DataExportorApplication）

#### 1.3 创建启动类 `DataExportorApplication.java`
- 包：`com.gnosis.dataexportor`
- `@SpringBootApplication` + `@ComponentScan(basePackages = {"com.gnosis.dataexportor", "com.gnosis.common"})`

#### 1.4 创建 `application.properties`
- 与 `gnosis-accounts` 一致的 openGauss 数据源配置
- `server.port=8090`（避免与现有模块冲突）
- `spring.application.name=gnosis-data-exportor`
- 文件上传大小限制：`spring.servlet.multipart.max-file-size=500MB`
- mybatis 配置（如需要）

---

### 步骤2：实现核心接口与DTO

#### 2.1 `ProgressListener` 接口
- 位置：`com.gnosis.dataexportor.listener`
- 方法（完全按需求文档第8章）：
  - `void onStart(long totalRows, int totalSheets)`
  - `void onProgress(long processedRows, long totalRows, int batchIndex)`
  - `void onSheetSwitch(String sheetName, int sheetIndex, long rowsInSheet)`
  - `void onComplete(long totalRows, int totalSheets, long durationMs)`
  - `boolean onError(String errorMsg, long rowNumber, Throwable cause)` — 返回true继续，false中止

#### 2.2 `LoggingProgressListener` 实现
- 使用 `org.slf4j.Logger` + `@Slf4j`
- 每10000行打印一次进度百分比
- `onError` 默认返回 false（中止）

#### 2.3 请求/响应DTO
- `ExportRequest`：
  - `String sql`（必填）
  - `String sheetNamePrefix`（可选，默认"Sheet"）
  - `List<String> columnNames`（可选）
  - 所有字段通过 `@RequestBody` JSON 接收
- `ExportResponse`：
  - `String filePath`（生成文件路径/下载URL）
  - `long totalRows`
  - `int totalSheets`
  - `long durationMs`
- `ImportRequest`：
  - `String tableName`（必填）
  - `Map<String, String> columnMapping`（可选）
  - `Integer batchSize`（可选）
- `ImportResult`：
  - `long totalRows` / `long successRows` / `long failedRows` / `long skippedRows`
  - `int totalSheets` / `long durationMs`
  - `List<ErrorRecord> errors`
- `ErrorRecord`：
  - `int sheetIndex` / `long rowNumber` / `String rowData` / `String errorMessage`

---

### 步骤3：实现导出核心逻辑

#### 3.1 `SqlUtils` 工具类
- `String wrapCountSql(String originalSql)` → `SELECT COUNT(*) FROM ({originalSql}) AS t`
- `boolean hasOrderBy(String sql)` → 检查是否包含 ORDER BY，未包含则打 WARN 日志
- `String buildPagingSql(String originalSql, int limit, long offset)` → 拼接 `LIMIT {limit} OFFSET {offset}`（openGauss 3.0.0 标准语法）

#### 3.2 `BatchSizeCalculator` 计算器
- `int calculateExportBatchSize(long totalRows)` 规则：
  - ≤ 5,000 → 全量（返回 totalRows）
  - 5,001 ~ 100,000 → 5,000
  - 100,001 ~ 500,000 → 10,000
  - 500,001 ~ 2,000,000 → 20,000
  - > 2,000,000 → 50,000
- `int calculateImportBatchSize(long totalRows)` 规则：
  - ≤ 5,000 → 全量
  - 其他 → 5,000
- `int calculateSheetsNeeded(long totalRows)` → `(int) Math.ceil((double) totalRows / 1048576)`

#### 3.3 `ExcelExportService` 接口
- `void exportToExcel(ExportRequest request, HttpServletResponse response, ProgressListener listener)`
- `void exportToExcel(String sql, OutputStream outputStream, String sheetNamePrefix, ProgressListener listener)`（通用版）

#### 3.4 `ExcelExportServiceImpl` 实现（核心）
**流程：**
1. 解析SQL，校验 ORDER BY（无则 warn）
2. 执行 COUNT 查询（超时30秒 → 降级 batchSize=10000）
3. `BatchSizeCalculator` 计算 batchSize 和 sheetsNeeded
4. 初始化 `SXSSFWorkbook(100)`（内存中保留100行，其余写临时文件）
5. 回调 `listener.onStart(totalRows, sheetsNeeded)`
6. 分页循环：
   - 构建 `LIMIT batchSize OFFSET offset` SQL
   - 使用 `JdbcTemplate` 执行查询（设置 `fetchSize=batchSize`，`TYPE_FORWARD_ONLY`）
   - 遍历 ResultSet，动态获取 `ResultSetMetaData` 生成列名
   - 按行写入 SXSSFWorkbook 当前 Sheet
   - 行数达到 1,048,576 时自动切换 Sheet（命名：`{prefix}_{N}`）
   - 每批次完成回调 `listener.onProgress()`
   - 切换 Sheet 时回调 `listener.onSheetSwitch()`
   - `offset += batchSize`
7. 写入 `response.getOutputStream()`（设置 Content-Type 和 Content-Disposition）
8. 清理临时文件：`workbook.dispose()`
9. 回调 `listener.onComplete()`

**openGauss 特殊处理：**
- JDBC URL 已包含 `?prepareThreshold=0`
- 查询在事务中执行（`@Transactional`）
- ResultSet 使用 `TYPE_FORWARD_ONLY`
- 分页语法 `LIMIT ? OFFSET ?`

**异常处理：**
- COUNT超时 → batchSize=10000 继续
- 分页查询超时 → 重试最多3次，失败中止
- 磁盘空间不足 → 中止，删除不完整文件

---

### 步骤4：实现导入核心逻辑

#### 4.1 `ExcelImportService` 接口
- `ImportResult importFromExcel(MultipartFile file, ImportRequest request, ProgressListener listener)`

#### 4.2 `ExcelImportServiceImpl` 实现
**流程：**
1. 使用 `XSSFWorkbook` 打开文件（流式模式，`InputStream`）
2. 遍历所有 Sheet：
   - 读取第一行作为表头（列名列表）
   - 构建列映射（显式映射 > 名称自动匹配 > 位置映射）
   - 生成 INSERT SQL：`INSERT INTO {tableName} ({columns}) VALUES ({placeholders})`
   - 分批次读取数据行（batchSize由计算器得出）：
     - 转换为参数数组
     - 使用 `JdbcTemplate.batchUpdate(sql, batchArgs)` 批量插入
     - 每批次提交一次事务（`@Transactional` + 手动控制）
     - 回调 `listener.onProgress()`
     - 释放当前批次内存
3. 统计结果填入 `ImportResult`
4. 回调 `listener.onComplete()`

**列映射策略（优先级从高到低）：**
1. 显式映射（`ImportRequest.columnMapping`）
2. 名称自动匹配（不区分大小写）
3. 位置映射（按Excel列顺序对应数据库字段顺序）

**数据校验与异常处理：**
- 类型不匹配 → 记录 `ErrorRecord`，跳过该行（可配置是否中止）
- 必填字段为空 → 记录 `ErrorRecord`，跳过
- 行数据格式错误 → 记录 `ErrorRecord`，跳过
- 通过 `listener.onError()` 决定是否继续

---

### 步骤5：实现 Controller 层

#### 5.1 `ExcelExportController`
- `@RestController` + `@RequestMapping("/data-exportor/export")`
- 所有接口使用 `@PostMapping`
- **接口列表：**

| 端点 | 说明 |
|------|------|
| `/execute` | 执行导出，接收 `ExportRequest` JSON，返回文件流 |
| `/page` | 分页查询导出任务记录（需建表） |
| `/detail` | 查询导出任务详情 |

#### 5.2 `ExcelImportController`
- `@RestController` + `@RequestMapping("/data-exportor/import")`
- 所有接口使用 `@PostMapping`
- **接口列表：**

| 端点 | 说明 |
|------|------|
| `/execute` | 执行导入，接收 `MultipartFile` + `ImportRequest` JSON 参数，返回 `ImportResult` |
| `/downloadTemplate` | 下载导入模板 |
| `/page` | 分页查询导入任务记录（需建表） |
| `/detail` | 查询导入任务详情 |

---

### 步骤6：前端实现（React）

#### 6.1 导出页面组件
- `DataExportPage`：主页面
  - SQL配置区域：输入SQL语句、Sheet前缀
  - 自定义列名（可选）
  - 导出按钮 + 进度条显示
  - 下载链接

#### 6.2 导入页面组件
- `DataImportPage`：主页面
  - 文件上传区域
  - 目标表名输入
  - 列映射配置（可选）
  - 批次大小配置（可选）
  - 导入按钮 + 进度条显示
  - 导入结果显示（成功/失败/跳过数量，错误详情列表）

#### 6.3 前端技术要点
- 使用 Ant Design（antd）或现有项目组件库
- 文件下载：使用 `blob` 方式处理 Excel 流
- 进度轮询：通过轮询或 WebSocket 获取进度
- 遵循项目现有的 React 代码风格

---

## 三、文件清单

### 后端新建文件（共约20个文件）

| # | 文件 | 类型 |
|---|------|------|
| 1 | `pom.xml` | Maven配置 |
| 2 | `DataExportorApplication.java` | 启动类 |
| 3 | `application.properties` | 配置 |
| 4 | `ProgressListener.java` | 接口 |
| 5 | `LoggingProgressListener.java` | 实现 |
| 6 | `ExportRequest.java` | DTO |
| 7 | `ExportResponse.java` | DTO |
| 8 | `ImportRequest.java` | DTO |
| 9 | `ImportResult.java` | DTO |
| 10 | `ErrorRecord.java` | DTO |
| 11 | `ExcelExportService.java` | 接口 |
| 12 | `ExcelImportService.java` | 接口 |
| 13 | `ExcelExportServiceImpl.java` | 实现 |
| 14 | `ExcelImportServiceImpl.java` | 实现 |
| 15 | `ExcelExportController.java` | 控制器 |
| 16 | `ExcelImportController.java` | 控制器 |
| 17 | `BatchSizeCalculator.java` | 工具 |
| 18 | `SqlUtils.java` | 工具 |
| 19 | `ExcelUtils.java` | 工具 |
| 20 | `DataSourceConfig.java`（可选） | 配置 |

### 后端修改文件

| # | 文件 | 修改内容 |
|---|------|----------|
| 1 | `gnosis-project/pom.xml` | 在 `<modules>` 中添加 `gnosis-data-exportor` |

### 前端新建文件（共约6-8个文件）

| # | 文件 | 类型 |
|---|------|------|
| 1 | `DataExportPage.tsx` | 导出页面 |
| 2 | `DataImportPage.tsx` | 导入页面 |
| 3 | `exportService.ts` | API调用 |
| 4 | `importService.ts` | API调用 |
| 5 | 路由配置更新 | 路由 |

---

## 四、技术约束遵循清单

| 约束 | 实现方式 |
|------|----------|
| Spring Boot 2.1.10 | pom继承父POM版本管理 |
| Java 1.8 | maven.compiler.source=1.8 |
| MyBatis-Plus 3.5.4 | 依赖声明，管理数据源 |
| Apache POI 4.1.2 | SXSSFWorkbook（导出）/ XSSFWorkbook（导入） |
| openGauss 3.0.0 | `LIMIT ? OFFSET ?` 分页语法 |
| openGauss游标 | `?prepareThreshold=0` + `TYPE_FORWARD_ONLY` |
| Lombok 1.18.30 | @Data, @Slf4j, @Builder 等 |
| Hutool 5.8.12 | 辅助工具（StrUtil, CollUtil等） |
| FastJSON 1.2.83 | JSON序列化 |
| Swagger 2.8.0 | @Api, @ApiOperation 注解 |
| Controller POST | 全部使用 @PostMapping，请求响应封装 |
| MyBatis XML | 如需动态SQL使用mapper.xml |

---

## 五、执行顺序

1. **创建模块骨架**：pom.xml、启动类、配置（步骤1）
2. **实现DTO和接口**：ProgressListener、Request/Response DTO（步骤2）
3. **实现工具类**：SqlUtils、BatchSizeCalculator（步骤3.1-3.2）
4. **实现导出服务**：ExcelExportServiceImpl（步骤3.4）
5. **实现导入服务**：ExcelImportServiceImpl（步骤4.2）
6. **实现Controller**：ExcelExportController、ExcelImportController（步骤5）
7. **修改父POM**：注册新模块
8. **实现前端页面**：导出页面 + 导入页面（步骤6）

---

## 六、关键设计决策

1. **不使用 MyBatis Mapper 做通用查询**：因为导出/导入需要处理任意SQL，直接使用 `JdbcTemplate` 更灵活，同时保留 MyBatis-Plus 用于管理任务记录表
2. **SXSSFWorkbook(100)**：内存中仅保留100行，其余写入磁盘临时文件，确保大数据量导出内存可控
3. **导入使用 XSSFWorkbook 而非 SAX（事件驱动）**：XSSFWorkbook 的 `InputStream` 方式已足够处理百万级数据，且代码更简洁可维护
4. **进度回调同步执行**：由于导出/导入在单线程中执行，回调天然线程安全；如需异步通知，由调用方在 ProgressListener 实现中处理
5. **不建任务记录表**：该模块定位为通用组件，任务记录由各业务模块按需管理；如需可后续扩展
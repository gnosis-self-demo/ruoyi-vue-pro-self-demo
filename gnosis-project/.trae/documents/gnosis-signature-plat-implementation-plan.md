# 签章平台模块 (gnosis-signature-plat) 实现计划

## 一、需求概述

根据 `需求/签章平台需求.md`，建设一个整合多方供应商签章能力的统一电子签章中台平台，核心能力包括：

1. **文件管理**：上传、下载、查询签章文件
2. **模板管理**：创建和管理签章模板
3. **签章流程配置**：会签、顺序签等流程管理
4. **印章生命周期管理**：创建、授权、使用、销毁印章
5. **供应商管理**：管理多个签章供应商（法大大、上上签、e签宝等）
6. **证书管理**：CA证书管理
7. **审计日志**：操作审计记录

## 二、当前项目状态分析

### 2.1 技术栈确认
| 组件 | 版本 |
|------|------|
| Spring Boot | 2.1.10.RELEASE |
| Java | 1.8 |
| MyBatis-Plus | 3.5.4 |
| openGauss | 3.0.0 |
| React | 18 + Ant Design 5 |
| Vite | 构建工具 |

### 2.2 现有模块模式
- 后端模块：独立 Spring Boot 应用，端口从 8080 开始递增
- 前端模块：内嵌在 `frontend/` 目录，端口从 3000 开始递增
- 数据库：统一使用 `gnosis_sample` 数据库，表名前缀区分模块

### 2.3 端口分配
- 后端端口：**8095**（当前最大为 8091）
- 前端端口：**3008**（当前最大为 3006）

## 三、模块结构设计

### 3.1 目录结构

```
gnosis-signature-plat/
├── pom.xml                                    # 模块 POM
├── src/main/
│   ├── java/com/gnosis/signature/
│   │   ├── SignaturePlatApplication.java      # Spring Boot 启动类
│   │   ├── api/                               # 对外 API（供其他模块调用）
│   │   │   └── SignatureApi.java
│   │   ├── config/                            # 配置类
│   │   │   ├── MybatisConfig.java
│   │   │   └── SwaggerConfig.java
│   │   ├── controller/                        # REST 控制器
│   │   │   ├── SignatureFileController.java
│   │   │   ├── SignatureTemplateController.java
│   │   │   ├── SignatureProcessController.java
│   │   │   ├── SignatureSealController.java
│   │   │   ├── SignatureSupplierController.java
│   │   │   ├── SignatureCertificateController.java
│   │   │   └── SignatureAuditLogController.java
│   │   ├── domain/                            # 实体类
│   │   │   ├── SignatureFile.java
│   │   │   ├── SignatureTemplate.java
│   │   │   ├── SignatureProcess.java
│   │   │   ├── SignatureSeal.java
│   │   │   ├── SignatureSupplier.java
│   │   │   ├── SignatureCertificate.java
│   │   │   └── SignatureAuditLog.java
│   │   ├── dto/                               # 数据传输对象
│   │   │   ├── file/
│   │   │   │   ├── SignatureFileCreateRequest.java
│   │   │   │   │   ├── SignatureFileUpdateRequest.java
│   │   │   │   │   ├── SignatureFileQueryRequest.java
│   │   │   │   │   ├── SignatureFileVO.java
│   │   │   │   │   └── SignatureFileIdsRequest.java
│   │   │   ├── template/
│   │   │   ├── process/
│   │   │   ├── seal/
│   │   │   ├── supplier/
│   │   │   ├── certificate/
│   │   │   └── audit/
│   │   ├── mapper/                            # MyBatis Mapper 接口
│   │   │   ├── SignatureFileMapper.java
│   │   │   ├── SignatureTemplateMapper.java
│   │   │   ├── SignatureProcessMapper.java
│   │   │   ├── SignatureSealMapper.java
│   │   │   ├── SignatureSupplierMapper.java
│   │   │   ├── SignatureCertificateMapper.java
│   │   │   └── SignatureAuditLogMapper.java
│   │   └── service/                           # 业务逻辑层
│   │       ├── SignatureFileService.java
│   │       ├── SignatureTemplateService.java
│   │       ├── SignatureProcessService.java
│   │       ├── SignatureSealService.java
│   │       ├── SignatureSupplierService.java
│   │       ├── SignatureCertificateService.java
│   │       ├── SignatureAuditLogService.java
│   │       └── SignatureImportExportService.java
│   └── resources/
│       ├── application.properties             # 应用配置
│       ├── db/signature/                      # 数据库脚本
│       │   └── signature-opengauss.sql
│       └── mapper/signature/                  # MyBatis XML
│           ├── SignatureFileMapper.xml
│           ├── SignatureTemplateMapper.xml
│           ├── SignatureProcessMapper.xml
│           ├── SignatureSealMapper.xml
│           ├── SignatureSupplierMapper.xml
│           ├── SignatureCertificateMapper.xml
│           └── SignatureAuditLogMapper.xml
└── frontend/                                  # 前端代码
    ├── package.json
    ├── vite.config.js
    ├── index.html
    └── src/
        ├── main.jsx
        ├── App.jsx
        ├── index.css
        ├── api/
        │   └── signatureApi.js
        ├── pages/
        │   ├── FileManage.jsx
        │   ├── TemplateManage.jsx
        │   ├── ProcessManage.jsx
        │   ├── SealManage.jsx
        │   ├── SupplierManage.jsx
        │   ├── CertificateManage.jsx
        │   └── AuditLogManage.jsx
        └── utils/
            └── request.js
```

## 四、数据库表设计

### 4.1 签章文件表 (sig_file)
```sql
DROP TABLE IF EXISTS sig_file;
CREATE TABLE sig_file (
    id                  varchar(128)    NOT NULL,
    file_name           varchar(256)    NOT NULL,
    file_type           varchar(32)     NOT NULL,
    file_size           bigint          DEFAULT 0,
    file_path           varchar(512)    DEFAULT '',
    file_hash           varchar(128)    DEFAULT '',
    template_id         varchar(128)    DEFAULT '',
    process_id          varchar(128)    DEFAULT '',
    supplier_id         varchar(128)    DEFAULT '',
    sign_status         int             DEFAULT 0,
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.2 签章模板表 (sig_template)
```sql
DROP TABLE IF EXISTS sig_template;
CREATE TABLE sig_template (
    id                  varchar(128)    NOT NULL,
    template_code       varchar(64)     NOT NULL,
    template_name       varchar(128)    NOT NULL,
    template_type       varchar(32)     NOT NULL,
    template_content    text            DEFAULT '',
    sign_position       varchar(512)    DEFAULT '',
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.3 签章流程表 (sig_process)
```sql
DROP TABLE IF EXISTS sig_process;
CREATE TABLE sig_process (
    id                  varchar(128)    NOT NULL,
    process_code        varchar(64)     NOT NULL,
    process_name        varchar(128)    NOT NULL,
    process_type        varchar(32)     NOT NULL,
    process_config      text            DEFAULT '',
    sign_order          int             DEFAULT 0,
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.4 印章表 (sig_seal)
```sql
DROP TABLE IF EXISTS sig_seal;
CREATE TABLE sig_seal (
    id                  varchar(128)    NOT NULL,
    seal_code           varchar(64)     NOT NULL,
    seal_name           varchar(128)    NOT NULL,
    seal_type           varchar(32)     NOT NULL,
    seal_image_path     varchar(512)    DEFAULT '',
    seal_image_data     text            DEFAULT '',
    authorize_user_id   varchar(128)    DEFAULT '',
    authorize_time      timestamp       DEFAULT CURRENT_TIMESTAMP,
    expire_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.5 供应商表 (sig_supplier)
```sql
DROP TABLE IF EXISTS sig_supplier;
CREATE TABLE sig_supplier (
    id                  varchar(128)    NOT NULL,
    supplier_code       varchar(64)     NOT NULL,
    supplier_name       varchar(128)    NOT NULL,
    supplier_type       varchar(32)     NOT NULL,
    api_url             varchar(512)    DEFAULT '',
    api_key             varchar(256)    DEFAULT '',
    api_secret          varchar(256)    DEFAULT '',
    contact_name        varchar(64)     DEFAULT '',
    contact_phone       varchar(32)     DEFAULT '',
    contact_email       varchar(128)    DEFAULT '',
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.6 证书表 (sig_certificate)
```sql
DROP TABLE IF EXISTS sig_certificate;
CREATE TABLE sig_certificate (
    id                  varchar(128)    NOT NULL,
    cert_code           varchar(64)     NOT NULL,
    cert_name           varchar(128)    NOT NULL,
    cert_type           varchar(32)     NOT NULL,
    ca_org              varchar(128)    DEFAULT '',
    cert_sn             varchar(128)    DEFAULT '',
    cert_content        text            DEFAULT '',
    expire_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    description         varchar(512)    DEFAULT '',
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

### 4.7 审计日志表 (sig_audit_log)
```sql
DROP TABLE IF EXISTS sig_audit_log;
CREATE TABLE sig_audit_log (
    id                  varchar(128)    NOT NULL,
    operation_type      varchar(32)     NOT NULL,
    operation_module    varchar(64)     NOT NULL,
    operation_desc      varchar(512)    DEFAULT '',
    request_params      text            DEFAULT '',
    response_result     text            DEFAULT '',
    operation_ip        varchar(64)     DEFAULT '',
    operation_result    int             DEFAULT 1,
    error_msg           varchar(512)    DEFAULT '',
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

## 五、API 设计（供其他模块调用）

### 5.1 SignatureApi.java
```java
@Component
public class SignatureApi {
    // 文件签章
    public String signFile(String fileId, String templateId, String processId, String userId);
    
    // 查询签章状态
    public Integer getSignStatus(String fileId);
    
    // 获取签章文件下载路径
    public String getSignedFilePath(String fileId);
    
    // 验证签章有效性
    public boolean verifySignature(String fileId);
    
    // 获取可用供应商列表
    public List<SignatureSupplier> getAvailableSuppliers();
}
```

## 六、功能页面清单

每个页面必须包含：分页查询、条件查询、导入、导出、详情、新建、编辑、批量删除、批量启用、批量禁用

| 页面 | 路由路径 | 功能说明 |
|------|----------|----------|
| 文件管理 | /file | 签章文件的上传、下载、签章操作 |
| 模板管理 | /template | 签章模板的创建和配置 |
| 流程管理 | /process | 签章流程配置（会签、顺序签） |
| 印章管理 | /seal | 印章生命周期管理 |
| 供应商管理 | /supplier | 签章供应商管理 |
| 证书管理 | /certificate | CA证书管理 |
| 审计日志 | /audit | 操作审计记录查询 |

## 七、实现步骤

### 步骤 1：创建模块基础结构
1. 创建 `gnosis-signature-plat/` 目录
2. 创建 `pom.xml`（继承父 pom，添加依赖）
3. 创建 `SignaturePlatApplication.java` 启动类
4. 创建 `application.properties` 配置文件
5. 创建 `MybatisConfig.java` 配置类
6. 更新根 `pom.xml` 添加新模块

### 步骤 2：创建数据库脚本
1. 创建 `src/main/resources/db/signature/signature-opengauss.sql`
2. 包含所有 7 张表的建表语句
3. 添加表和字段注释
4. 添加初始化数据（默认供应商、默认流程等）

### 步骤 7：创建 API 类
1. 创建 `SignatureApi.java` 供其他模块调用
2. 实现核心签章方法

### 步骤 8：创建前端基础结构
1. 创建 `frontend/package.json`
2. 创建 `frontend/vite.config.js`（端口 3008，代理到 8095）
3. 创建 `frontend/index.html`
4. 创建 `frontend/src/main.jsx`
5. 创建 `frontend/src/App.jsx`（含菜单路由）
6. 创建 `frontend/src/utils/request.js`
7. 创建 `frontend/src/api/signatureApi.js`

### 步骤 9：创建前端页面
1. FileManage.jsx - 文件管理页面
2. TemplateManage.jsx - 模板管理页面
3. ProcessManage.jsx - 流程管理页面
4. SealManage.jsx - 印章管理页面
5. SupplierManage.jsx - 供应商管理页面
6. CertificateManage.jsx - 证书管理页面
7. AuditLogManage.jsx - 审计日志页面

### 步骤 10：编译验证
1. 后端编译：`mvn clean compile -pl gnosis-signature-plat`
2. 前端编译：`cd frontend && npm install && npm run build`
3. 验证 API 功能正常
4. 验证前端功能正常

## 八、依赖配置

### 8.1 Maven 依赖
```xml
<dependencies>
    <!-- 公共模块 -->
    <dependency>
        <groupId>com.gnosis</groupId>
        <artifactId>gnosis-common</artifactId>
    </dependency>
    
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
    </dependency>
    
    <!-- openGauss JDBC -->
    <dependency>
        <groupId>org.opengauss</groupId>
        <artifactId>opengauss-jdbc</artifactId>
    </dependency>
    
    <!-- Apache POI (导入导出) -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
    </dependency>
    
    <!-- Swagger -->
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>
    
    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-core</artifactId>
    </dependency>
    
    <!-- FastJSON -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
    </dependency>
    
    <!-- Guava -->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
    </dependency>
</dependencies>
```

### 8.2 前端依赖 (package.json)
```json
{
  "name": "gnosis-signature-plat-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.8.0",
    "antd": "^5.1.0",
    "axios": "^1.3.0",
    "dayjs": "^1.11.0",
    "@ant-design/icons": "^5.0.0"
  },
  "devDependencies": {
    "@types/react": "^18.0.0",
    "@types/react-dom": "^18.0.0",
    "@vitejs/plugin-react": "^4.0.0",
    "vite": "^4.1.0"
  }
}
```

## 九、验证步骤

### 9.1 后端验证
1. 执行 `mvn clean compile -pl gnosis-signature-plat` 确保编译通过
2. 启动应用，检查 Swagger 文档：`http://localhost:8095/swagger-ui.html`
3. 测试各 API 接口功能正常

### 9.2 前端验证
1. 执行 `cd frontend && npm install && npm run build` 确保编译通过
2. 启动开发服务器 `npm run dev`
3. 验证各页面功能：
   - 分页查询正常
   - 条件查询正常
   - 新建/编辑功能正常
   - 详情查看正常
   - 批量删除/启用/禁用正常
   - 导入/导出功能正常

### 9.3 集成验证
1. 验证前端能正确调用后端 API
2. 验证数据库表创建成功
3. 验证 API 类可被其他模块调用

## 十、假设与决策

### 10.1 假设
1. 数据库 `gnosis_sample` 已存在且可连接
2. 开发环境已安装 JDK 8、Maven、Node.js
3. 端口 8095 和 3008 未被占用

### 10.2 决策
1. 使用 `@Component` 方式提供 API（与项目现有模式一致，非 Feign）
2. 使用 Apache POI 实现导入导出（与 gnosis-accounts 模式一致）
3. 使用 React + Ant Design 5 作为前端技术栈（与多数模块一致）
4. 使用手动分页方式（count + list 查询）而非 MyBatis-Plus 分页插件
5. 所有表使用 `varchar(128)` 作为主键，使用 UUID 生成
6. 所有表包含 `create_user_id`, `update_user_id`, `create_time`, `update_time` 审计字段

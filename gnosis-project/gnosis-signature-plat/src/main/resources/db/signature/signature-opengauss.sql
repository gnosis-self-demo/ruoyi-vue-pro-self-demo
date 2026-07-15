-- ============================================
-- 签章平台数据库脚本 (openGauss 3.0.0)
-- 模块: gnosis-signature-plat
-- ============================================

-- 1. 签章文件表
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

COMMENT ON TABLE sig_file IS '签章文件表';
COMMENT ON COLUMN sig_file.id IS '主键ID';
COMMENT ON COLUMN sig_file.file_name IS '文件名称';
COMMENT ON COLUMN sig_file.file_type IS '文件类型';
COMMENT ON COLUMN sig_file.file_size IS '文件大小(字节)';
COMMENT ON COLUMN sig_file.file_path IS '文件存储路径';
COMMENT ON COLUMN sig_file.file_hash IS '文件哈希值';
COMMENT ON COLUMN sig_file.template_id IS '模板ID';
COMMENT ON COLUMN sig_file.process_id IS '流程ID';
COMMENT ON COLUMN sig_file.supplier_id IS '供应商ID';
COMMENT ON COLUMN sig_file.sign_status IS '签章状态(0-未签章 1-签章中 2-已签章 3-签章失败)';
COMMENT ON COLUMN sig_file.description IS '描述';
COMMENT ON COLUMN sig_file.status IS '状态(0-禁用 1-启用)';
COMMENT ON COLUMN sig_file.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_file.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_file.create_time IS '创建时间';
COMMENT ON COLUMN sig_file.update_time IS '更新时间';

-- 2. 签章模板表
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

COMMENT ON TABLE sig_template IS '签章模板表';
COMMENT ON COLUMN sig_template.id IS '主键ID';
COMMENT ON COLUMN sig_template.template_code IS '模板编码';
COMMENT ON COLUMN sig_template.template_name IS '模板名称';
COMMENT ON COLUMN sig_template.template_type IS '模板类型';
COMMENT ON COLUMN sig_template.template_content IS '模板内容';
COMMENT ON COLUMN sig_template.sign_position IS '签章位置';
COMMENT ON COLUMN sig_template.description IS '描述';
COMMENT ON COLUMN sig_template.status IS '状态(0-禁用 1-启用)';
COMMENT ON COLUMN sig_template.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_template.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_template.create_time IS '创建时间';
COMMENT ON COLUMN sig_template.update_time IS '更新时间';

-- 3. 签章流程表
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

COMMENT ON TABLE sig_process IS '签章流程表';
COMMENT ON COLUMN sig_process.id IS '主键ID';
COMMENT ON COLUMN sig_process.process_code IS '流程编码';
COMMENT ON COLUMN sig_process.process_name IS '流程名称';
COMMENT ON COLUMN sig_process.process_type IS '流程类型(会签/顺序签/并行签)';
COMMENT ON COLUMN sig_process.process_config IS '流程配置(JSON)';
COMMENT ON COLUMN sig_process.sign_order IS '签署顺序';
COMMENT ON COLUMN sig_process.description IS '描述';
COMMENT ON COLUMN sig_process.status IS '状态(0-禁用 1-启用)';
COMMENT ON COLUMN sig_process.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_process.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_process.create_time IS '创建时间';
COMMENT ON COLUMN sig_process.update_time IS '更新时间';

-- 4. 印章表
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

COMMENT ON TABLE sig_seal IS '印章表';
COMMENT ON COLUMN sig_seal.id IS '主键ID';
COMMENT ON COLUMN sig_seal.seal_code IS '印章编码';
COMMENT ON COLUMN sig_seal.seal_name IS '印章名称';
COMMENT ON COLUMN sig_seal.seal_type IS '印章类型(公章/合同章/法人章等)';
COMMENT ON COLUMN sig_seal.seal_image_path IS '印章图片路径';
COMMENT ON COLUMN sig_seal.seal_image_data IS '印章图片数据(Base64)';
COMMENT ON COLUMN sig_seal.authorize_user_id IS '授权人ID';
COMMENT ON COLUMN sig_seal.authorize_time IS '授权时间';
COMMENT ON COLUMN sig_seal.expire_time IS '过期时间';
COMMENT ON COLUMN sig_seal.description IS '描述';
COMMENT ON COLUMN sig_seal.status IS '状态(0-禁用 1-启用 2-已销毁)';
COMMENT ON COLUMN sig_seal.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_seal.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_seal.create_time IS '创建时间';
COMMENT ON COLUMN sig_seal.update_time IS '更新时间';

-- 5. 供应商表
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

COMMENT ON TABLE sig_supplier IS '供应商表';
COMMENT ON COLUMN sig_supplier.id IS '主键ID';
COMMENT ON COLUMN sig_supplier.supplier_code IS '供应商编码';
COMMENT ON COLUMN sig_supplier.supplier_name IS '供应商名称';
COMMENT ON COLUMN sig_supplier.supplier_type IS '供应商类型';
COMMENT ON COLUMN sig_supplier.api_url IS 'API地址';
COMMENT ON COLUMN sig_supplier.api_key IS 'API Key';
COMMENT ON COLUMN sig_supplier.api_secret IS 'API Secret';
COMMENT ON COLUMN sig_supplier.contact_name IS '联系人';
COMMENT ON COLUMN sig_supplier.contact_phone IS '联系电话';
COMMENT ON COLUMN sig_supplier.contact_email IS '联系邮箱';
COMMENT ON COLUMN sig_supplier.description IS '描述';
COMMENT ON COLUMN sig_supplier.status IS '状态(0-禁用 1-启用)';
COMMENT ON COLUMN sig_supplier.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_supplier.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_supplier.create_time IS '创建时间';
COMMENT ON COLUMN sig_supplier.update_time IS '更新时间';

-- 6. 证书表
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

COMMENT ON TABLE sig_certificate IS '证书表';
COMMENT ON COLUMN sig_certificate.id IS '主键ID';
COMMENT ON COLUMN sig_certificate.cert_code IS '证书编码';
COMMENT ON COLUMN sig_certificate.cert_name IS '证书名称';
COMMENT ON COLUMN sig_certificate.cert_type IS '证书类型';
COMMENT ON COLUMN sig_certificate.ca_org IS 'CA机构';
COMMENT ON COLUMN sig_certificate.cert_sn IS '证书序列号';
COMMENT ON COLUMN sig_certificate.cert_content IS '证书内容';
COMMENT ON COLUMN sig_certificate.expire_time IS '过期时间';
COMMENT ON COLUMN sig_certificate.description IS '描述';
COMMENT ON COLUMN sig_certificate.status IS '状态(0-禁用 1-启用 2-已过期)';
COMMENT ON COLUMN sig_certificate.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_certificate.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_certificate.create_time IS '创建时间';
COMMENT ON COLUMN sig_certificate.update_time IS '更新时间';

-- 7. 审计日志表
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
    status              int             DEFAULT 1,
    create_user_id      varchar(128)    DEFAULT '',
    update_user_id      varchar(128)    DEFAULT '',
    create_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    update_time         timestamp       DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sig_audit_log IS '审计日志表';
COMMENT ON COLUMN sig_audit_log.id IS '主键ID';
COMMENT ON COLUMN sig_audit_log.operation_type IS '操作类型';
COMMENT ON COLUMN sig_audit_log.operation_module IS '操作模块';
COMMENT ON COLUMN sig_audit_log.operation_desc IS '操作描述';
COMMENT ON COLUMN sig_audit_log.request_params IS '请求参数';
COMMENT ON COLUMN sig_audit_log.response_result IS '响应结果';
COMMENT ON COLUMN sig_audit_log.operation_ip IS '操作IP';
COMMENT ON COLUMN sig_audit_log.operation_result IS '操作结果(0-失败 1-成功)';
COMMENT ON COLUMN sig_audit_log.error_msg IS '错误信息';
COMMENT ON COLUMN sig_audit_log.status IS '状态(0-禁用 1-启用)';
COMMENT ON COLUMN sig_audit_log.create_user_id IS '创建人ID';
COMMENT ON COLUMN sig_audit_log.update_user_id IS '更新人ID';
COMMENT ON COLUMN sig_audit_log.create_time IS '创建时间';
COMMENT ON COLUMN sig_audit_log.update_time IS '更新时间';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化供应商数据
INSERT INTO sig_supplier (id, supplier_code, supplier_name, supplier_type, api_url, description, status, create_user_id, create_time) 
VALUES ('SUP001', 'FADADADA', '法大大', 'ELECTRONIC_SIGN', 'https://api.fadada.com', '国内领先的电子签名平台', 1, 'system', CURRENT_TIMESTAMP);

INSERT INTO sig_supplier (id, supplier_code, supplier_name, supplier_type, api_url, description, status, create_user_id, create_time) 
VALUES ('SUP002', 'SHANGSHANGQIAN', '上上签', 'ELECTRONIC_SIGN', 'https://api.shangshangqian.com', '专业电子签约云平台', 1, 'system', CURRENT_TIMESTAMP);

INSERT INTO sig_supplier (id, supplier_code, supplier_name, supplier_type, api_url, description, status, create_user_id, create_time) 
VALUES ('SUP003', 'ESIGN', 'e签宝', 'ELECTRONIC_SIGN', 'https://api.esign.com', '电子签章行业领军者', 1, 'system', CURRENT_TIMESTAMP);

-- 初始化流程数据
INSERT INTO sig_process (id, process_code, process_name, process_type, process_config, sign_order, description, status, create_user_id, create_time) 
VALUES ('PROC001', 'SEQUENTIAL_SIGN', '顺序签', 'SEQUENTIAL', '{"steps":[{"order":1,"signer":"first"},{"order":2,"signer":"second"}]}', 1, '按顺序依次签署', 1, 'system', CURRENT_TIMESTAMP);

INSERT INTO sig_process (id, process_code, process_name, process_type, process_config, sign_order, description, status, create_user_id, create_time) 
VALUES ('PROC002', 'COUNTER_SIGN', '会签', 'COUNTER', '{"steps":[{"order":1,"signer":"all"}]}', 1, '所有签署人同时签署', 1, 'system', CURRENT_TIMESTAMP);

INSERT INTO sig_process (id, process_code, process_name, process_type, process_config, sign_order, description, status, create_user_id, create_time) 
VALUES ('PROC003', 'PARALLEL_SIGN', '并行签', 'PARALLEL', '{"steps":[{"order":1,"signer":"any"}]}', 1, '任意一人签署即可', 1, 'system', CURRENT_TIMESTAMP);

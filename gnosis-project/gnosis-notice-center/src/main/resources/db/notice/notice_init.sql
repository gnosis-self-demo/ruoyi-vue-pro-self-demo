-- ============================================
-- 统一消息通知中心 - 数据库初始化脚本
-- 数据库: OpenGauss 3.0.0
-- ============================================

-- 1. 消息模板表
CREATE TABLE IF NOT EXISTS sys_notice_template (
    id VARCHAR(64) NOT NULL,
    template_code VARCHAR(128) NOT NULL,
    template_name VARCHAR(256) NOT NULL,
    template_type VARCHAR(32) NOT NULL,
    notice_type VARCHAR(32) NOT NULL,
    subject VARCHAR(512),
    content TEXT NOT NULL,
    attachment_config TEXT,
    third_party_config TEXT,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_template IS '消息模板表';
COMMENT ON COLUMN sys_notice_template.id IS '主键ID';
COMMENT ON COLUMN sys_notice_template.template_code IS '模板编码';
COMMENT ON COLUMN sys_notice_template.template_name IS '模板名称';
COMMENT ON COLUMN sys_notice_template.template_type IS '模板类型：TEXT/RICH_TEXT/HTML';
COMMENT ON COLUMN sys_notice_template.notice_type IS '通知类型：SMS/EMAIL/INBOX/WECHAT';
COMMENT ON COLUMN sys_notice_template.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_template.content IS '模板内容';
COMMENT ON COLUMN sys_notice_template.attachment_config IS '附件配置JSON';
COMMENT ON COLUMN sys_notice_template.third_party_config IS '第三方配置JSON';
COMMENT ON COLUMN sys_notice_template.status IS '状态：1启用 0禁用';
COMMENT ON COLUMN sys_notice_template.remark IS '备注';
COMMENT ON COLUMN sys_notice_template.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_template.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_template.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_template.update_time IS '更新时间';

-- 创建唯一索引
CREATE UNIQUE INDEX idx_template_code ON sys_notice_template(template_code);
CREATE INDEX idx_notice_type ON sys_notice_template(notice_type);
CREATE INDEX idx_status ON sys_notice_template(status);

-- 2. 消息发送日志表
CREATE TABLE IF NOT EXISTS sys_notice_log (
    id VARCHAR(64) NOT NULL,
    template_id VARCHAR(64),
    template_code VARCHAR(128),
    notice_type VARCHAR(32) NOT NULL,
    receiver VARCHAR(512) NOT NULL,
    subject VARCHAR(512),
    content TEXT,
    attachment_paths TEXT,
    send_status INT NOT NULL DEFAULT 0,
    error_msg TEXT,
    retry_count INT DEFAULT 0,
    max_retry INT DEFAULT 3,
    request_id VARCHAR(128),
    third_party_response TEXT,
    send_time TIMESTAMP,
    create_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_log IS '消息发送日志表';
COMMENT ON COLUMN sys_notice_log.id IS '主键ID';
COMMENT ON COLUMN sys_notice_log.template_id IS '模板ID';
COMMENT ON COLUMN sys_notice_log.template_code IS '模板编码';
COMMENT ON COLUMN sys_notice_log.notice_type IS '通知类型';
COMMENT ON COLUMN sys_notice_log.receiver IS '接收人';
COMMENT ON COLUMN sys_notice_log.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_log.content IS '实际发送内容';
COMMENT ON COLUMN sys_notice_log.attachment_paths IS '附件路径JSON';
COMMENT ON COLUMN sys_notice_log.send_status IS '发送状态：0待发送 1发送中 2成功 3失败';
COMMENT ON COLUMN sys_notice_log.error_msg IS '错误信息';
COMMENT ON COLUMN sys_notice_log.retry_count IS '重试次数';
COMMENT ON COLUMN sys_notice_log.max_retry IS '最大重试次数';
COMMENT ON COLUMN sys_notice_log.request_id IS '第三方请求ID';
COMMENT ON COLUMN sys_notice_log.third_party_response IS '第三方响应内容';
COMMENT ON COLUMN sys_notice_log.send_time IS '发送时间';
COMMENT ON COLUMN sys_notice_log.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_log.create_time IS '创建时间';

-- 创建索引
CREATE INDEX idx_log_template_id ON sys_notice_log(template_id);
CREATE INDEX idx_log_template_code ON sys_notice_log(template_code);
CREATE INDEX idx_log_notice_type ON sys_notice_log(notice_type);
CREATE INDEX idx_log_send_status ON sys_notice_log(send_status);
CREATE INDEX idx_log_create_time ON sys_notice_log(create_time);

-- 3. 站内信表
CREATE TABLE IF NOT EXISTS sys_notice_inbox (
    id VARCHAR(64) NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    subject VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    attachment_paths TEXT,
    is_read INT NOT NULL DEFAULT 0,
    read_time TIMESTAMP,
    create_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_inbox IS '站内信表';
COMMENT ON COLUMN sys_notice_inbox.id IS '主键ID';
COMMENT ON COLUMN sys_notice_inbox.user_id IS '接收用户ID';
COMMENT ON COLUMN sys_notice_inbox.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_inbox.content IS '消息内容';
COMMENT ON COLUMN sys_notice_inbox.attachment_paths IS '附件路径JSON';
COMMENT ON COLUMN sys_notice_inbox.is_read IS '是否已读：0未读 1已读';
COMMENT ON COLUMN sys_notice_inbox.read_time IS '读取时间';
COMMENT ON COLUMN sys_notice_inbox.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_inbox.create_time IS '创建时间';

-- 创建索引
CREATE INDEX idx_inbox_user_id ON sys_notice_inbox(user_id);
CREATE INDEX idx_inbox_is_read ON sys_notice_inbox(is_read);
CREATE INDEX idx_inbox_create_time ON sys_notice_inbox(create_time);

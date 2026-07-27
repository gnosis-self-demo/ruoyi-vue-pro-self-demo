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
    priority INT NOT NULL DEFAULT 0,
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
COMMENT ON COLUMN sys_notice_template.notice_type IS '通知类型：SMS/EMAIL/INBOX/WECHAT/DINGTALK';
COMMENT ON COLUMN sys_notice_template.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_template.content IS '模板内容';
COMMENT ON COLUMN sys_notice_template.attachment_config IS '附件配置JSON';
COMMENT ON COLUMN sys_notice_template.third_party_config IS '第三方配置JSON';
COMMENT ON COLUMN sys_notice_template.priority IS '消息优先级：0普通 1重要 2紧急';
COMMENT ON COLUMN sys_notice_template.status IS '状态：1启用 0禁用';
COMMENT ON COLUMN sys_notice_template.remark IS '备注';
COMMENT ON COLUMN sys_notice_template.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_template.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_template.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_template.update_time IS '更新时间';

-- 创建唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_template_code ON sys_notice_template(template_code);
CREATE INDEX IF NOT EXISTS idx_notice_type ON sys_notice_template(notice_type);
CREATE INDEX IF NOT EXISTS idx_status ON sys_notice_template(status);

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
    priority INT NOT NULL DEFAULT 0,
    group_id VARCHAR(64),
    send_status INT NOT NULL DEFAULT 0,
    error_msg TEXT,
    retry_count INT DEFAULT 0,
    max_retry INT DEFAULT 3,
    request_id VARCHAR(128),
    third_party_response TEXT,
    send_time TIMESTAMP,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
COMMENT ON COLUMN sys_notice_log.priority IS '消息优先级：0普通 1重要 2紧急';
COMMENT ON COLUMN sys_notice_log.group_id IS '消息分组ID';
COMMENT ON COLUMN sys_notice_log.send_status IS '发送状态：0待发送 1发送中 2成功 3失败';
COMMENT ON COLUMN sys_notice_log.error_msg IS '错误信息';
COMMENT ON COLUMN sys_notice_log.retry_count IS '重试次数';
COMMENT ON COLUMN sys_notice_log.max_retry IS '最大重试次数';
COMMENT ON COLUMN sys_notice_log.request_id IS '第三方请求ID';
COMMENT ON COLUMN sys_notice_log.third_party_response IS '第三方响应内容';
COMMENT ON COLUMN sys_notice_log.send_time IS '发送时间';
COMMENT ON COLUMN sys_notice_log.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_log.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_log.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_log.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_log_template_id ON sys_notice_log(template_id);
CREATE INDEX IF NOT EXISTS idx_log_template_code ON sys_notice_log(template_code);
CREATE INDEX IF NOT EXISTS idx_log_notice_type ON sys_notice_log(notice_type);
CREATE INDEX IF NOT EXISTS idx_log_send_status ON sys_notice_log(send_status);
CREATE INDEX IF NOT EXISTS idx_log_create_time ON sys_notice_log(create_time);
CREATE INDEX IF NOT EXISTS idx_log_priority ON sys_notice_log(priority);
CREATE INDEX IF NOT EXISTS idx_log_group_id ON sys_notice_log(group_id);

-- 3. 站内信表
CREATE TABLE IF NOT EXISTS sys_notice_inbox (
    id VARCHAR(64) NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    subject VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    attachment_paths TEXT,
    priority INT NOT NULL DEFAULT 0,
    group_id VARCHAR(64),
    is_read INT NOT NULL DEFAULT 0,
    read_time TIMESTAMP,
    status INT NOT NULL DEFAULT 1,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_inbox IS '站内信表';
COMMENT ON COLUMN sys_notice_inbox.id IS '主键ID';
COMMENT ON COLUMN sys_notice_inbox.user_id IS '接收用户ID';
COMMENT ON COLUMN sys_notice_inbox.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_inbox.content IS '消息内容';
COMMENT ON COLUMN sys_notice_inbox.attachment_paths IS '附件路径JSON';
COMMENT ON COLUMN sys_notice_inbox.priority IS '消息优先级：0普通 1重要 2紧急';
COMMENT ON COLUMN sys_notice_inbox.group_id IS '消息分组ID';
COMMENT ON COLUMN sys_notice_inbox.is_read IS '是否已读：0未读 1已读';
COMMENT ON COLUMN sys_notice_inbox.read_time IS '读取时间';
COMMENT ON COLUMN sys_notice_inbox.status IS '状态：1正常 0禁用(归档)';
COMMENT ON COLUMN sys_notice_inbox.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_inbox.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_inbox.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_inbox.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_inbox_user_id ON sys_notice_inbox(user_id);
CREATE INDEX IF NOT EXISTS idx_inbox_is_read ON sys_notice_inbox(is_read);
CREATE INDEX IF NOT EXISTS idx_inbox_create_time ON sys_notice_inbox(create_time);
CREATE INDEX IF NOT EXISTS idx_inbox_status ON sys_notice_inbox(status);
CREATE INDEX IF NOT EXISTS idx_inbox_priority ON sys_notice_inbox(priority);
CREATE INDEX IF NOT EXISTS idx_inbox_group_id ON sys_notice_inbox(group_id);

-- 4. 模板版本表
CREATE TABLE IF NOT EXISTS sys_notice_template_version (
    id VARCHAR(64) NOT NULL,
    template_id VARCHAR(64) NOT NULL,
    template_code VARCHAR(128) NOT NULL,
    version_number INT NOT NULL DEFAULT 1,
    template_name VARCHAR(256) NOT NULL,
    template_type VARCHAR(32) NOT NULL,
    notice_type VARCHAR(32) NOT NULL,
    subject VARCHAR(512),
    content TEXT NOT NULL,
    attachment_config TEXT,
    third_party_config TEXT,
    priority INT NOT NULL DEFAULT 0,
    change_remark VARCHAR(500),
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_template_version IS '消息模板版本表';
COMMENT ON COLUMN sys_notice_template_version.id IS '主键ID';
COMMENT ON COLUMN sys_notice_template_version.template_id IS '模板ID';
COMMENT ON COLUMN sys_notice_template_version.template_code IS '模板编码';
COMMENT ON COLUMN sys_notice_template_version.version_number IS '版本号';
COMMENT ON COLUMN sys_notice_template_version.template_name IS '模板名称';
COMMENT ON COLUMN sys_notice_template_version.template_type IS '模板类型';
COMMENT ON COLUMN sys_notice_template_version.notice_type IS '通知类型';
COMMENT ON COLUMN sys_notice_template_version.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_template_version.content IS '模板内容';
COMMENT ON COLUMN sys_notice_template_version.attachment_config IS '附件配置JSON';
COMMENT ON COLUMN sys_notice_template_version.third_party_config IS '第三方配置JSON';
COMMENT ON COLUMN sys_notice_template_version.priority IS '消息优先级';
COMMENT ON COLUMN sys_notice_template_version.change_remark IS '变更备注';
COMMENT ON COLUMN sys_notice_template_version.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_template_version.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_template_version.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_template_version.update_time IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_tv_template_id ON sys_notice_template_version(template_id);
CREATE INDEX IF NOT EXISTS idx_tv_template_code ON sys_notice_template_version(template_code);
CREATE INDEX IF NOT EXISTS idx_tv_version ON sys_notice_template_version(version_number);

-- 5. 消息黑名单/退订表
CREATE TABLE IF NOT EXISTS sys_notice_blacklist (
    id VARCHAR(64) NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    notice_type VARCHAR(32) NOT NULL,
    template_code VARCHAR(128),
    reason VARCHAR(500),
    status INT NOT NULL DEFAULT 1,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_blacklist IS '消息黑名单/退订表';
COMMENT ON COLUMN sys_notice_blacklist.id IS '主键ID';
COMMENT ON COLUMN sys_notice_blacklist.user_id IS '用户ID';
COMMENT ON COLUMN sys_notice_blacklist.notice_type IS '通知类型：SMS/EMAIL/INBOX/WECHAT/DINGTALK';
COMMENT ON COLUMN sys_notice_blacklist.template_code IS '模板编码(空=退订该类型所有通知)';
COMMENT ON COLUMN sys_notice_blacklist.reason IS '退订原因';
COMMENT ON COLUMN sys_notice_blacklist.status IS '状态：1生效 0失效';
COMMENT ON COLUMN sys_notice_blacklist.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_blacklist.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_blacklist.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_blacklist.update_time IS '更新时间';

CREATE UNIQUE INDEX IF NOT EXISTS idx_blacklist_user_type ON sys_notice_blacklist(user_id, notice_type, template_code);
CREATE INDEX IF NOT EXISTS idx_blacklist_status ON sys_notice_blacklist(status);

-- 6. 定时发送表
CREATE TABLE IF NOT EXISTS sys_notice_scheduled_send (
    id VARCHAR(64) NOT NULL,
    template_code VARCHAR(128),
    notice_type VARCHAR(32) NOT NULL,
    receiver VARCHAR(512) NOT NULL,
    subject VARCHAR(512),
    content TEXT,
    params TEXT,
    attachment_paths TEXT,
    priority INT NOT NULL DEFAULT 0,
    group_id VARCHAR(64),
    scheduled_time TIMESTAMP NOT NULL,
    send_status INT NOT NULL DEFAULT 0,
    log_id VARCHAR(64),
    error_msg TEXT,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_scheduled_send IS '定时发送表';
COMMENT ON COLUMN sys_notice_scheduled_send.id IS '主键ID';
COMMENT ON COLUMN sys_notice_scheduled_send.template_code IS '模板编码';
COMMENT ON COLUMN sys_notice_scheduled_send.notice_type IS '通知类型';
COMMENT ON COLUMN sys_notice_scheduled_send.receiver IS '接收人';
COMMENT ON COLUMN sys_notice_scheduled_send.subject IS '消息主题';
COMMENT ON COLUMN sys_notice_scheduled_send.content IS '消息内容';
COMMENT ON COLUMN sys_notice_scheduled_send.params IS '模板变量参数JSON';
COMMENT ON COLUMN sys_notice_scheduled_send.attachment_paths IS '附件路径JSON';
COMMENT ON COLUMN sys_notice_scheduled_send.priority IS '消息优先级';
COMMENT ON COLUMN sys_notice_scheduled_send.group_id IS '消息分组ID';
COMMENT ON COLUMN sys_notice_scheduled_send.scheduled_time IS '计划发送时间';
COMMENT ON COLUMN sys_notice_scheduled_send.send_status IS '发送状态：0待发送 1已发送 2发送失败 3已取消';
COMMENT ON COLUMN sys_notice_scheduled_send.log_id IS '发送日志ID(发送后回填)';
COMMENT ON COLUMN sys_notice_scheduled_send.error_msg IS '错误信息';
COMMENT ON COLUMN sys_notice_scheduled_send.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_scheduled_send.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_scheduled_send.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_scheduled_send.update_time IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_ss_scheduled_time ON sys_notice_scheduled_send(scheduled_time);
CREATE INDEX IF NOT EXISTS idx_ss_send_status ON sys_notice_scheduled_send(send_status);
CREATE INDEX IF NOT EXISTS idx_ss_create_time ON sys_notice_scheduled_send(create_time);

-- 7. 消息分组表
CREATE TABLE IF NOT EXISTS sys_notice_group (
    id VARCHAR(64) NOT NULL,
    group_code VARCHAR(128) NOT NULL,
    group_name VARCHAR(256) NOT NULL,
    description VARCHAR(500),
    status INT NOT NULL DEFAULT 1,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

COMMENT ON TABLE sys_notice_group IS '消息分组表';
COMMENT ON COLUMN sys_notice_group.id IS '主键ID';
COMMENT ON COLUMN sys_notice_group.group_code IS '分组编码';
COMMENT ON COLUMN sys_notice_group.group_name IS '分组名称';
COMMENT ON COLUMN sys_notice_group.description IS '分组描述';
COMMENT ON COLUMN sys_notice_group.status IS '状态：1启用 0禁用';
COMMENT ON COLUMN sys_notice_group.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_notice_group.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_notice_group.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice_group.update_time IS '更新时间';

CREATE UNIQUE INDEX IF NOT EXISTS idx_group_code ON sys_notice_group(group_code);
CREATE INDEX IF NOT EXISTS idx_group_status ON sys_notice_group(status);

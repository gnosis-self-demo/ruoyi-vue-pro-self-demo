-- 业务入口管理表
-- 用于管理业务生命周期的入口配置

-- 删除已存在的表
DROP TABLE IF EXISTS lifecycle_business_type;

-- 创建业务类型表
CREATE TABLE lifecycle_business_type (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键 ID',
    name VARCHAR(128) NOT NULL COMMENT '业务类型名称',
    code VARCHAR(64) NOT NULL COMMENT '业务类型编码',
    description VARCHAR(512) COMMENT '描述信息',
    entry_permission_config TEXT COMMENT '入口权限配置 (JSON 格式)',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    update_user_id VARCHAR(128) NOT NULL COMMENT '更新人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_lifecycle_business_type_code UNIQUE (code)
);

-- 创建索引
CREATE INDEX idx_lifecycle_business_type_name ON lifecycle_business_type(name);
CREATE INDEX idx_lifecycle_business_type_create_time ON lifecycle_business_type(create_time DESC);

-- 添加表注释
COMMENT ON TABLE lifecycle_business_type IS '业务生命周期类型表';
COMMENT ON COLUMN lifecycle_business_type.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_business_type.name IS '业务类型名称';
COMMENT ON COLUMN lifecycle_business_type.code IS '业务类型编码';
COMMENT ON COLUMN lifecycle_business_type.description IS '描述信息';
COMMENT ON COLUMN lifecycle_business_type.entry_permission_config IS '入口权限配置 (JSON 格式)';
COMMENT ON COLUMN lifecycle_business_type.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_business_type.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_business_type.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_business_type.update_time IS '更新时间';

-- 插入示例数据
INSERT INTO lifecycle_business_type (id, name, code, description, entry_permission_config, create_user_id, update_user_id, create_time, update_time)
VALUES 
    ('1001', '订单管理', 'ORDER_MANAGE', '订单管理业务类型', '{"roles":["ADMIN","USER"],"disabled":false}', 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1002', '用户管理', 'USER_MANAGE', '用户管理业务类型', '{"roles":["ADMIN"],"disabled":false}', 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1003', '库存管理', 'INVENTORY_MANAGE', '库存管理业务类型', '{"roles":["ADMIN","WAREHOUSE"],"disabled":false}', 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

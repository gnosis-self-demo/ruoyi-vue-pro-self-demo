-- 基于openGauss的分布式队列表结构

-- 主队列表
CREATE TABLE sys_distributed_queue (
    id BIGINT NOT NULL PRIMARY KEY,
    queue_name VARCHAR(128) NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consumer_id VARCHAR(128),
    attempt_count INT DEFAULT 0
);

-- 创建索引以提高查询性能
CREATE INDEX idx_queue_status_id ON sys_distributed_queue (queue_name, status, id);
CREATE INDEX idx_queue_created_at ON sys_distributed_queue (created_at);
CREATE INDEX idx_queue_consumer_id ON sys_distributed_queue (consumer_id);

-- 结果表
CREATE TABLE sys_distributed_queue_result (
    request_id VARCHAR(64) NOT NULL PRIMARY KEY,
    result_status VARCHAR(20) NOT NULL,
    result_data TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建结果表索引
CREATE INDEX idx_result_created_at ON sys_distributed_queue_result (created_at);
CREATE INDEX idx_result_status ON sys_distributed_queue_result (result_status);

COMMENT ON TABLE sys_distributed_queue IS '分布式队列表';
COMMENT ON COLUMN sys_distributed_queue.id IS '消息ID';
COMMENT ON COLUMN sys_distributed_queue.queue_name IS '队列名称';
COMMENT ON COLUMN sys_distributed_queue.message_body IS '消息内容';
COMMENT ON COLUMN sys_distributed_queue.status IS '消息状态：pending-待处理, processing-处理中, done-已完成';
COMMENT ON COLUMN sys_distributed_queue.created_at IS '创建时间';
COMMENT ON COLUMN sys_distributed_queue.updated_at IS '更新时间';
COMMENT ON COLUMN sys_distributed_queue.consumer_id IS '消费者ID';
COMMENT ON COLUMN sys_distributed_queue.attempt_count IS '尝试次数';

COMMENT ON TABLE sys_distributed_queue_result IS '队列处理结果表';
COMMENT ON COLUMN sys_distributed_queue_result.request_id IS '请求ID';
COMMENT ON COLUMN sys_distributed_queue_result.result_status IS '结果状态：SUCCESS-成功, FAILED-失败';
COMMENT ON COLUMN sys_distributed_queue_result.result_data IS '结果数据';
COMMENT ON COLUMN sys_distributed_queue_result.error_message IS '错误信息';
COMMENT ON COLUMN sys_distributed_queue_result.created_at IS '创建时间';
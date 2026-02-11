-- 分布式队列表
CREATE TABLE IF NOT EXISTS sys_distributed_queue (
    id BIGSERIAL PRIMARY KEY,
    queue_name VARCHAR(100) NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    consumer_id VARCHAR(100),
    attempt_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 队列结果表（用于同步等待）
CREATE TABLE IF NOT EXISTS sys_queue_result (
    request_id VARCHAR(100) PRIMARY KEY,
    success BOOLEAN NOT NULL,
    result_data TEXT,
    error_message TEXT,
    created_at TIMESTAMP
);

-- 索引优化
CREATE INDEX IF NOT EXISTS idx_queue_status ON sys_distributed_queue(status);
CREATE INDEX IF NOT EXISTS idx_queue_name_status ON sys_distributed_queue(queue_name, status);
CREATE INDEX IF NOT EXISTS idx_queue_updated_at ON sys_distributed_queue(updated_at);
CREATE INDEX IF NOT EXISTS idx_result_created_at ON sys_queue_result(created_at);

-- 添加注释
COMMENT ON TABLE sys_distributed_queue IS '分布式队列表';
COMMENT ON COLUMN sys_distributed_queue.id IS '主键ID';
COMMENT ON COLUMN sys_distributed_queue.queue_name IS '队列名称';
COMMENT ON COLUMN sys_distributed_queue.message_body IS '消息内容';
COMMENT ON COLUMN sys_distributed_queue.status IS '消息状态: pending, processing, done, failed, dead_letter';
COMMENT ON COLUMN sys_distributed_queue.consumer_id IS '消费者ID';
COMMENT ON COLUMN sys_distributed_queue.attempt_count IS '尝试次数';
COMMENT ON COLUMN sys_distributed_queue.error_message IS '错误信息';
COMMENT ON COLUMN sys_distributed_queue.created_at IS '创建时间';
COMMENT ON COLUMN sys_distributed_queue.updated_at IS '更新时间';

COMMENT ON TABLE sys_queue_result IS '队列结果表';
COMMENT ON COLUMN sys_queue_result.request_id IS '请求ID';
COMMENT ON COLUMN sys_queue_result.success IS '是否成功';
COMMENT ON COLUMN sys_queue_result.result_data IS '结果数据';
COMMENT ON COLUMN sys_queue_result.error_message IS '错误信息';
COMMENT ON COLUMN sys_queue_result.created_at IS '创建时间';
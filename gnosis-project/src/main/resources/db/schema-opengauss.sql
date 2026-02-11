-- openGauss分布式队列表结构

-- 队列消息表
CREATE TABLE IF NOT EXISTS sys_distributed_queue (
    id BIGINT PRIMARY KEY,
    queue_name VARCHAR(100) NOT NULL,
    message_body TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    consumer_id VARCHAR(100),
    attempt_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 队列处理结果表
CREATE TABLE IF NOT EXISTS sys_distributed_queue_result (
    request_id VARCHAR(64) PRIMARY KEY,
    result_status VARCHAR(20) NOT NULL,
    result_data TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引优化
CREATE INDEX IF NOT EXISTS idx_queue_name_status ON sys_distributed_queue(queue_name, status);
CREATE INDEX IF NOT EXISTS idx_consumer_id ON sys_distributed_queue(consumer_id);
CREATE INDEX IF NOT EXISTS idx_created_at ON sys_distributed_queue(created_at);
CREATE INDEX IF NOT EXISTS idx_updated_at ON sys_distributed_queue(updated_at);

-- 插入测试数据
INSERT INTO sys_distributed_queue (id, queue_name, message_body, status) VALUES 
(1, 'sms_queue', '{"mobile":"13800138000","content":"测试短信"}', 'pending'),
(2, 'email_queue', '{"email":"test@example.com","subject":"测试邮件","content":"邮件内容"}', 'pending'),
(3, 'notification_queue', '{"userId":1,"type":"system","message":"系统通知"}', 'pending')
ON CONFLICT DO NOTHING;
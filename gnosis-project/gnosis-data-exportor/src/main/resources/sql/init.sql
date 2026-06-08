-- 数据导出任务表
CREATE TABLE IF NOT EXISTS data_export_task (
    id VARCHAR(128) PRIMARY KEY,
    task_name VARCHAR(256),
    sql_content TEXT,
    sheet_name VARCHAR(256),
    file_name VARCHAR(256),
    export_format VARCHAR(32),
    total_rows BIGINT,
    status VARCHAR(32),
    error_message TEXT,
    duration_ms BIGINT,
    create_user_id VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 数据导入任务表
CREATE TABLE IF NOT EXISTS data_import_task (
    id VARCHAR(128) PRIMARY KEY,
    task_name VARCHAR(256),
    table_name VARCHAR(128),
    file_name VARCHAR(256),
    import_format VARCHAR(32),
    total_rows BIGINT,
    success_rows BIGINT,
    failed_rows BIGINT,
    skipped_rows BIGINT,
    total_sheets INT,
    status VARCHAR(32),
    error_message TEXT,
    duration_ms BIGINT,
    create_user_id VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
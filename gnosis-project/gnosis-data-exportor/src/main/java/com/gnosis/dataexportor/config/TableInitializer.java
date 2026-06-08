package com.gnosis.dataexportor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Slf4j
@Component
public class TableInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 建表
            stmt.execute("CREATE TABLE IF NOT EXISTS data_export_task (" +
                    "id VARCHAR(128) PRIMARY KEY, " +
                    "task_name VARCHAR(256), " +
                    "business_type VARCHAR(128), " +
                    "scene VARCHAR(128), " +
                    "module VARCHAR(128), " +
                    "operator VARCHAR(128), " +
                    "client_ip VARCHAR(64), " +
                    "request_params TEXT, " +
                    "sql_content TEXT, " +
                    "sheet_name VARCHAR(256), " +
                    "file_name VARCHAR(256), " +
                    "export_format VARCHAR(32), " +
                    "total_rows BIGINT, " +
                    "processed_rows BIGINT, " +
                    "download_url VARCHAR(1024), " +
                    "status VARCHAR(32), " +
                    "error_message TEXT, " +
                    "duration_ms BIGINT, " +
                    "create_user_id VARCHAR(128), " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_user_id VARCHAR(128), " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            log.info("data_export_task 表已就绪");

            // 兼容旧表：添加新列
            addColumnIfNotExists(stmt, "data_export_task", "business_type", "VARCHAR(128)");
            addColumnIfNotExists(stmt, "data_export_task", "scene", "VARCHAR(128)");
            addColumnIfNotExists(stmt, "data_export_task", "module", "VARCHAR(128)");
            addColumnIfNotExists(stmt, "data_export_task", "operator", "VARCHAR(128)");
            addColumnIfNotExists(stmt, "data_export_task", "client_ip", "VARCHAR(64)");
            addColumnIfNotExists(stmt, "data_export_task", "request_params", "TEXT");
            addColumnIfNotExists(stmt, "data_export_task", "processed_rows", "BIGINT");
            addColumnIfNotExists(stmt, "data_export_task", "download_url", "VARCHAR(1024)");

            stmt.execute("CREATE TABLE IF NOT EXISTS data_import_task (" +
                    "id VARCHAR(128) PRIMARY KEY, " +
                    "task_name VARCHAR(256), " +
                    "table_name VARCHAR(128), " +
                    "file_name VARCHAR(256), " +
                    "import_format VARCHAR(32), " +
                    "total_rows BIGINT, " +
                    "success_rows BIGINT, " +
                    "failed_rows BIGINT, " +
                    "skipped_rows BIGINT, " +
                    "total_sheets INT, " +
                    "status VARCHAR(32), " +
                    "error_message TEXT, " +
                    "duration_ms BIGINT, " +
                    "create_user_id VARCHAR(128), " +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "update_user_id VARCHAR(128), " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");
            log.info("data_import_task 表已就绪");
        } catch (Exception e) {
            log.warn("表初始化失败（可能已存在）: {}", e.getMessage());
        }
    }

    private void addColumnIfNotExists(Statement stmt, String tableName, String columnName, String columnType) {
        try {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
            log.info("已添加列 {}.{} ({})", tableName, columnName, columnType);
        } catch (Exception e) {
            // 列已存在，忽略
        }
    }
}
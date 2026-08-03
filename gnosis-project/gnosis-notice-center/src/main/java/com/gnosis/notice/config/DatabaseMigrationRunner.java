package com.gnosis.notice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库迁移工具
 * 启动时自动检查并补充缺失的列和表
 */
@Component
@Order(1)
public class DatabaseMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        log.info("开始执行数据库迁移检查...");

        // sys_notice_log 表补充列
        addColumnIfNotExists("sys_notice_log", "priority", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("sys_notice_log", "group_id", "VARCHAR(64)");
        addColumnIfNotExists("sys_notice_log", "update_user_id", "VARCHAR(128)");
        addColumnIfNotExists("sys_notice_log", "update_time", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        // sys_notice_inbox 表补充列
        addColumnIfNotExists("sys_notice_inbox", "priority", "INT NOT NULL DEFAULT 0");
        addColumnIfNotExists("sys_notice_inbox", "group_id", "VARCHAR(64)");
        addColumnIfNotExists("sys_notice_inbox", "status", "INT NOT NULL DEFAULT 1");
        addColumnIfNotExists("sys_notice_inbox", "update_user_id", "VARCHAR(128)");
        addColumnIfNotExists("sys_notice_inbox", "update_time", "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

        // sys_notice_template 表补充列
        addColumnIfNotExists("sys_notice_template", "priority", "INT NOT NULL DEFAULT 0");

        // 创建缺失的表
        createTableIfNotExists("sys_notice_scheduled_send",
                "CREATE TABLE sys_notice_scheduled_send (" +
                "id VARCHAR(64) NOT NULL, " +
                "template_code VARCHAR(128), " +
                "notice_type VARCHAR(32) NOT NULL, " +
                "receiver VARCHAR(512) NOT NULL, " +
                "subject VARCHAR(512), " +
                "content TEXT, " +
                "params TEXT, " +
                "attachment_paths TEXT, " +
                "priority INT NOT NULL DEFAULT 0, " +
                "group_id VARCHAR(64), " +
                "scheduled_time TIMESTAMP NOT NULL, " +
                "send_status INT NOT NULL DEFAULT 0, " +
                "log_id VARCHAR(64), " +
                "error_msg TEXT, " +
                "create_user_id VARCHAR(128), " +
                "update_user_id VARCHAR(128), " +
                "create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id))");

        createTableIfNotExists("sys_notice_template_version",
                "CREATE TABLE sys_notice_template_version (" +
                "id VARCHAR(64) NOT NULL, " +
                "template_id VARCHAR(64) NOT NULL, " +
                "template_code VARCHAR(128) NOT NULL, " +
                "version_number INT NOT NULL DEFAULT 1, " +
                "template_name VARCHAR(256) NOT NULL, " +
                "template_type VARCHAR(32) NOT NULL, " +
                "notice_type VARCHAR(32) NOT NULL, " +
                "subject VARCHAR(512), " +
                "content TEXT NOT NULL, " +
                "attachment_config TEXT, " +
                "third_party_config TEXT, " +
                "priority INT NOT NULL DEFAULT 0, " +
                "change_remark VARCHAR(500), " +
                "create_user_id VARCHAR(128), " +
                "update_user_id VARCHAR(128), " +
                "create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id))");

        createTableIfNotExists("sys_notice_blacklist",
                "CREATE TABLE sys_notice_blacklist (" +
                "id VARCHAR(64) NOT NULL, " +
                "user_id VARCHAR(128) NOT NULL, " +
                "notice_type VARCHAR(32) NOT NULL, " +
                "template_code VARCHAR(128), " +
                "reason VARCHAR(500), " +
                "status INT NOT NULL DEFAULT 1, " +
                "create_user_id VARCHAR(128), " +
                "update_user_id VARCHAR(128), " +
                "create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id))");

        createTableIfNotExists("sys_notice_group",
                "CREATE TABLE sys_notice_group (" +
                "id VARCHAR(64) NOT NULL, " +
                "group_code VARCHAR(128) NOT NULL, " +
                "group_name VARCHAR(256) NOT NULL, " +
                "description VARCHAR(500), " +
                "status INT NOT NULL DEFAULT 1, " +
                "create_user_id VARCHAR(128), " +
                "update_user_id VARCHAR(128), " +
                "create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (id))");

        log.info("数据库迁移检查完成");
    }

    /**
     * 检查表是否存在，不存在则创建
     */
    private void createTableIfNotExists(String tableName, String createSql) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?",
                    Integer.class, tableName
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute(createSql);
                log.info("已创建表: {}", tableName);
            }
        } catch (Exception e) {
            log.warn("创建表 {} 时出错: {}", tableName, e.getMessage());
        }
    }

    /**
     * 检查列是否存在，不存在则添加
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?",
                    Integer.class, tableName, columnName
            );
            if (count == null || count == 0) {
                String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
                jdbcTemplate.execute(sql);
                log.info("已添加列: {}.{}", tableName, columnName);
            }
        } catch (Exception e) {
            log.warn("添加列 {}:{} 时出错: {}", tableName, columnName, e.getMessage());
        }
    }
}

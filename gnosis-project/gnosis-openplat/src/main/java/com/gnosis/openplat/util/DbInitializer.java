package com.gnosis.openplat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 数据库初始化工具类
 * 用于执行SQL脚本，初始化开放平台数据库表结构
 */
@Component
public class DbInitializer {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void init() {
        try {
            // 先检查并添加缺失的列
            addMissingColumns();
            // 创建缺失的表
            createMissingTables();
            // 执行初始化脚本
            executeSqlScript("classpath:db/openplat/openplat-opengauss.sql");
            log.info("[DbInitializer] Successfully initialized openplat database");
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to initialize database", e);
        }
    }
    
    /**
     * 添加缺失的列
     */
    private void addMissingColumns() {
        try {
            // 检查openplat_api_config表是否存在system_id列
            boolean hasSystemIdColumn = false;
            try {
                jdbcTemplate.queryForObject("SELECT system_id FROM openplat_api_config LIMIT 1", String.class);
                hasSystemIdColumn = true;
            } catch (Exception e) {
                // 列不存在，需要添加
                log.info("[DbInitializer] system_id column not found, adding it");
            }
            
            if (!hasSystemIdColumn) {
                // 添加system_id列
                jdbcTemplate.execute("ALTER TABLE openplat_api_config ADD COLUMN system_id VARCHAR(128)");
                // 更新现有数据
                jdbcTemplate.execute("UPDATE openplat_api_config SET system_id = 'sys_001'");
                // 设置为非空
                jdbcTemplate.execute("ALTER TABLE openplat_api_config ALTER COLUMN system_id SET NOT NULL");
            }
            
            // 添加外键约束
            try {
                jdbcTemplate.execute("ALTER TABLE openplat_api_config ADD CONSTRAINT fk_api_system FOREIGN KEY (system_id) REFERENCES openplat_system(id)");
            } catch (Exception e) {
                // 外键约束可能已经存在，忽略错误
                log.warn("[DbInitializer] Foreign key constraint fk_api_system may already exist", e);
            }
            
            // 添加索引
            try {
                jdbcTemplate.execute("CREATE INDEX idx_api_config_system ON openplat_api_config(system_id)");
            } catch (Exception e) {
                // 索引可能已经存在，忽略错误
                log.warn("[DbInitializer] Index idx_api_config_system may already exist", e);
            }
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to add missing columns", e);
        }
    }

    /**
     * 创建缺失的表
     */
    private void createMissingTables() {
        try {
            // 检查openplat_api_system_relation表是否存在
            boolean tableExists = false;
            try {
                jdbcTemplate.queryForObject("SELECT 1 FROM openplat_api_system_relation LIMIT 1", Integer.class);
                tableExists = true;
            } catch (Exception e) {
                log.info("[DbInitializer] openplat_api_system_relation table not found, creating it");
            }
            
            if (!tableExists) {
                jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS openplat_api_system_relation (" +
                    "relation_id VARCHAR(128) PRIMARY KEY, " +
                    "api_id VARCHAR(128) NOT NULL, " +
                    "system_id VARCHAR(128) NOT NULL, " +
                    "status VARCHAR(16) DEFAULT 'ENABLED', " +
                    "description VARCHAR(512), " +
                    "create_user_id VARCHAR(128) NOT NULL, " +
                    "create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "update_user_id VARCHAR(128), " +
                    "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
                );
                
                // 添加外键约束
                try {
                    jdbcTemplate.execute("ALTER TABLE openplat_api_system_relation ADD CONSTRAINT fk_rel_api FOREIGN KEY (api_id) REFERENCES openplat_api_config(id)");
                } catch (Exception e) {
                    log.warn("[DbInitializer] Foreign key fk_rel_api may already exist", e);
                }
                
                try {
                    jdbcTemplate.execute("ALTER TABLE openplat_api_system_relation ADD CONSTRAINT fk_rel_system FOREIGN KEY (system_id) REFERENCES openplat_system(id)");
                } catch (Exception e) {
                    log.warn("[DbInitializer] Foreign key fk_rel_system may already exist", e);
                }
                
                // 添加索引
                try {
                    jdbcTemplate.execute("CREATE INDEX idx_api_sys_rel_api ON openplat_api_system_relation(api_id)");
                } catch (Exception e) {
                    log.warn("[DbInitializer] Index idx_api_sys_rel_api may already exist", e);
                }
                
                try {
                    jdbcTemplate.execute("CREATE INDEX idx_api_sys_rel_system ON openplat_api_system_relation(system_id)");
                } catch (Exception e) {
                    log.warn("[DbInitializer] Index idx_api_sys_rel_system may already exist", e);
                }
                
                log.info("[DbInitializer] Successfully created openplat_api_system_relation table");
            }
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to create missing tables", e);
        }
    }

    /**
     * 执行SQL脚本
     */
    private void executeSqlScript(String scriptPath) throws Exception {
        Resource resource = resourceLoader.getResource(scriptPath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            StringBuilder sqlBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line);
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    jdbcTemplate.execute(sql);
                    sqlBuilder.setLength(0);
                }
            }
        }
    }
}
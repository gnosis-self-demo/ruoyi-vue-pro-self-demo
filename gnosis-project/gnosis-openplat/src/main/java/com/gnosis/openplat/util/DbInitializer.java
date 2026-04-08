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
            // 先检查并更新表结构
            updateTableStructures();
            // 执行初始化脚本
            executeSqlScript("classpath:db/openplat/openplat-opengauss.sql");
            log.info("[DbInitializer] Successfully initialized openplat database");
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to initialize database", e);
        }
    }
    
    /**
     * 更新表结构，添加缺失字段
     */
    private void updateTableStructures() {
        try {
            // 1. 更新 openplat_system 表
            checkAndAddColumn("openplat_system", "app_id", "VARCHAR(64)");
            checkAndAddColumn("openplat_system", "app_secret", "VARCHAR(128)");

            // 2. 更新 openplat_api_config 表
            checkAndAddColumn("openplat_api_config", "need_anti_replay", "BOOLEAN DEFAULT FALSE");
            checkAndRemoveColumn("openplat_api_config", "need_timestamp");
            checkAndRemoveColumn("openplat_api_config", "need_nonce");
            
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to update table structures", e);
        }
    }

    private void checkAndAddColumn(String tableName, String columnName, String columnType) {
        try {
            jdbcTemplate.execute("SELECT " + columnName + " FROM " + tableName + " LIMIT 1");
        } catch (Exception e) {
            log.info("[DbInitializer] Column {} not found in {}, adding it", columnName, tableName);
            try {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
            } catch (Exception ex) {
                log.error("[DbInitializer] Failed to add column {} to {}", columnName, tableName, ex);
            }
        }
    }

    private void checkAndRemoveColumn(String tableName, String columnName) {
        try {
            jdbcTemplate.execute("SELECT " + columnName + " FROM " + tableName + " LIMIT 1");
            log.info("[DbInitializer] Column {} found in {}, removing it", columnName, tableName);
            jdbcTemplate.execute("ALTER TABLE " + tableName + " DROP COLUMN " + columnName);
        } catch (Exception e) {
            // 列不存在，忽略
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
                    try {
                        jdbcTemplate.execute(sql);
                    } catch (Exception e) {
                        // 忽略重复插入等错误
                    }
                    sqlBuilder.setLength(0);
                }
            }
        }
    }
}

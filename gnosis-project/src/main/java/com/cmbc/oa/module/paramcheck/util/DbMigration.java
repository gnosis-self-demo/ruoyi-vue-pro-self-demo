package com.cmbc.oa.module.paramcheck.util;

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
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库迁移工具类
 * 用于执行SQL脚本，添加business_type字段
 */
@Component
public class DbMigration {

    private static final Logger log = LoggerFactory.getLogger(DbMigration.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void migrate() {
        try {
            // 检查business_type字段是否存在
            boolean columnExists = checkColumnExists();
            if (!columnExists) {
                // 执行迁移脚本
                executeSqlScript("classpath:db/add-business-type.sql");
                log.info("[DbMigration] Successfully added business_type column to sys_validation_flows");
            } else {
                log.info("[DbMigration] business_type column already exists, skipping migration");
            }
            
            // 执行初始化脚本，添加流程配置数据
            executeSqlScript("classpath:db/init-flow-configs.sql");
            log.info("[DbMigration] Successfully initialized flow config data");
        } catch (Exception e) {
            log.error("[DbMigration] Failed to migrate database", e);
        }
    }

    /**
     * 检查business_type字段是否存在
     */
    private boolean checkColumnExists() {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'gnosis_sample' AND table_name = 'sys_validation_flows' AND column_name = 'business_type'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            // 如果查询失败，假设字段不存在
            return false;
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
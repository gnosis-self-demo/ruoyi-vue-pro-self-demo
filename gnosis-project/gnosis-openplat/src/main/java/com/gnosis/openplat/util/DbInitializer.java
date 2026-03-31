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
            // 执行初始化脚本
            executeSqlScript("classpath:db/openplat/openplat-opengauss.sql");
            log.info("[DbInitializer] Successfully initialized openplat database");
        } catch (Exception e) {
            log.error("[DbInitializer] Failed to initialize database", e);
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
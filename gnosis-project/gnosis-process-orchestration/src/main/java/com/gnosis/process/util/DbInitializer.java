package com.gnosis.process.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DbInitializer {

    private static final Logger log = LoggerFactory.getLogger(DbInitializer.class);

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void init() {
        try (Connection connection = dataSource.getConnection()) {
            log.info("[DbInitializer] 开始执行建表脚本...");
            ClassPathResource resource = new ClassPathResource("db/process/process-orchestration-opengauss.sql");
            String sqlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

            String[] statements = sqlContent.split(";");
            try (Statement stmt = connection.createStatement()) {
                for (String sql : statements) {
                    String trimmedSql = sql.trim();
                    if (trimmedSql.isEmpty()) {
                        continue;
                    }
                    try {
                        stmt.execute(trimmedSql);
                        log.debug("[DbInitializer] 执行成功: {}", trimmedSql.substring(0, Math.min(80, trimmedSql.length())));
                    } catch (Exception e) {
                        log.warn("[DbInitializer] 执行跳过（可能已存在）: {} - {}", trimmedSql.substring(0, Math.min(80, trimmedSql.length())), e.getMessage());
                    }
                }
            }
            log.info("[DbInitializer] 建表脚本执行完成");
        } catch (Exception e) {
            log.error("[DbInitializer] 建表脚本执行异常", e);
        }
    }
}

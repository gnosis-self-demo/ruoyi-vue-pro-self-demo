package paramcheck.util;

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
            boolean businessTypeExists = checkColumnExists("business_type");
            if (!businessTypeExists) {
                // 执行迁移脚本
                executeSqlScript("classpath:db/add-business-type.sql");
                log.info("[DbMigration] Successfully added business_type column to sys_validation_flows");
            } else {
                log.info("[DbMigration] business_type column already exists, skipping migration");
            }
            
            // 检查create_user_id字段是否存在
            boolean createUserIdExists = checkColumnExists("create_user_id");
            if (!createUserIdExists) {
                // 添加create_user_id字段
                jdbcTemplate.execute("ALTER TABLE gnosis_sample.sys_validation_flows ADD COLUMN create_user_id VARCHAR(128)");
                log.info("[DbMigration] Successfully added create_user_id column to sys_validation_flows");
            } else {
                log.info("[DbMigration] create_user_id column already exists, skipping migration");
            }
            
            // 检查update_user_id字段是否存在
            boolean updateUserIdExists = checkColumnExists("update_user_id");
            if (!updateUserIdExists) {
                // 添加update_user_id字段
                jdbcTemplate.execute("ALTER TABLE gnosis_sample.sys_validation_flows ADD COLUMN update_user_id VARCHAR(128)");
                log.info("[DbMigration] Successfully added update_user_id column to sys_validation_flows");
            } else {
                log.info("[DbMigration] update_user_id column already exists, skipping migration");
            }
            
            // 检查create_time字段是否存在
            boolean createTimeExists = checkColumnExists("create_time");
            if (!createTimeExists) {
                // 添加create_time字段
                jdbcTemplate.execute("ALTER TABLE gnosis_sample.sys_validation_flows ADD COLUMN create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP");
                log.info("[DbMigration] Successfully added create_time column to sys_validation_flows");
            } else {
                log.info("[DbMigration] create_time column already exists, skipping migration");
            }
            
            // 检查updated_time字段是否存在
            boolean updatedTimeExists = checkColumnExists("updated_time");
            if (!updatedTimeExists) {
                // 添加updated_time字段
                jdbcTemplate.execute("ALTER TABLE gnosis_sample.sys_validation_flows ADD COLUMN updated_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP");
                log.info("[DbMigration] Successfully added updated_time column to sys_validation_flows");
            } else {
                log.info("[DbMigration] updated_time column already exists, skipping migration");
            }
            
            // 检查业务类型表是否存在
            boolean businessTypeTableExists = checkTableExists("sys_business_types");
            if (!businessTypeTableExists) {
                // 创建业务类型表
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS gnosis_sample.sys_business_types (code VARCHAR(64) PRIMARY KEY, name VARCHAR(128) NOT NULL, description TEXT, create_user_id VARCHAR(128), update_user_id VARCHAR(128), create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP, update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP)");
                log.info("[DbMigration] Successfully created sys_business_types table");
                
                // 初始化业务类型数据
                jdbcTemplate.execute("INSERT INTO gnosis_sample.sys_business_types (code, name, description, create_user_id, update_user_id) VALUES ('ORDER_CREATE', '订单创建', '订单创建相关的参数校验', 'admin', 'admin'), ('USER_REGISTER', '用户注册', '用户注册相关的参数校验', 'admin', 'admin'), ('BASIC_DATA', '基础数据录入', '基础数据录入相关的参数校验', 'admin', 'admin'), ('OTHER', '其他', '其他业务类型的参数校验', 'admin', 'admin') ON CONFLICT (code) DO NOTHING");
                log.info("[DbMigration] Successfully initialized business type data");
            } else {
                log.info("[DbMigration] sys_business_types table already exists, skipping migration");
            }
            
            // 执行初始化脚本，添加流程配置数据
            executeSqlScript("classpath:db/init-flow-configs.sql");
            log.info("[DbMigration] Successfully initialized flow config data");
        } catch (Exception e) {
            log.error("[DbMigration] Failed to migrate database", e);
        }
    }
    
    /**
     * 检查指定表是否存在
     */
    private boolean checkTableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'gnosis_sample' AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            // 如果查询失败，假设表不存在
            return false;
        }
    }

    /**
     * 检查指定字段是否存在
     */
    private boolean checkColumnExists(String columnName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = 'gnosis_sample' AND table_name = 'sys_validation_flows' AND column_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, columnName);
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
package com.gnosis.paramcheck.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * gnosis_sample 专属数据源配置
 * 使用 HikariCP 连接池，指向 openGauss 3.0.0 数据库的 gnosis_sample schema
 */
@Configuration
@MapperScan(basePackages = "com.gnosis.paramcheck.repository", sqlSessionFactoryRef = "gnosisSqlSessionFactory")
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    /**
     * gnosis_sample 数据源 (与主数据源隔离，可按需配置不同连接池参数)
     */
    @Bean(name = "gnosisDataSource")
    public DataSource gnosisDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setPoolName("gnosis-sample-pool");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }

    /**
     * 指向 gnosis_sample 的 JdbcTemplate
     * 用于在 Handler、Component 中执行动态 SQL
     */
    @Bean(name = "gnosisJdbcTemplate")
    public JdbcTemplate gnosisJdbcTemplate() {
        return new JdbcTemplate(gnosisDataSource());
    }

    /**
     * MyBatis SqlSessionFactory (如需 Mapper 方式)
     */
    @Bean(name = "gnosisSqlSessionFactory")
    public SqlSessionFactory gnosisSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(gnosisDataSource());
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath*:mapper/paramcheck/**/*.xml"));
        return bean.getObject();
    }
}

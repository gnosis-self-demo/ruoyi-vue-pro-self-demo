package paramcheck.repository;

import paramcheck.domain.BusinessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * 业务类型仓库
 */
@Repository
public class BusinessTypeRepository {

    private static final Logger log = LoggerFactory.getLogger(BusinessTypeRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取所有业务类型
     */
    public List<BusinessType> findAll() {
        String sql = "SELECT code, name, description, create_user_id, update_user_id, create_time, update_time " +
                "FROM gnosis_sample.sys_business_types";
        return jdbcTemplate.query(sql, new BusinessTypeRowMapper());
    }

    /**
     * 根据编码查询业务类型
     */
    public BusinessType findByCode(String code) {
        String sql = "SELECT code, name, description, create_user_id, update_user_id, create_time, update_time " +
                "FROM gnosis_sample.sys_business_types WHERE code = ?";
        List<BusinessType> list = jdbcTemplate.query(sql, new BusinessTypeRowMapper(), code);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 保存业务类型（创建或更新）
     */
    public void save(BusinessType businessType) {
        // 先检查记录是否存在
        String checkSql = "SELECT COUNT(*) FROM gnosis_sample.sys_business_types WHERE code = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, businessType.getCode());
        
        if (count != null && count > 0) {
            // 更新现有记录
            String updateSql = "UPDATE gnosis_sample.sys_business_types SET " +
                    "name = ?, " +
                    "description = ?, " +
                    "update_user_id = ?, " +
                    "update_time = CURRENT_TIMESTAMP " +
                    "WHERE code = ?";
            
            jdbcTemplate.update(updateSql, 
                    businessType.getName(),
                    businessType.getDescription(),
                    businessType.getUpdateUserId() != null ? businessType.getUpdateUserId() : "admin",
                    businessType.getCode());
        } else {
            // 插入新记录
            String insertSql = "INSERT INTO gnosis_sample.sys_business_types " +
                    "(code, name, description, create_user_id, update_user_id, create_time, update_time) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            
            jdbcTemplate.update(insertSql, 
                    businessType.getCode(),
                    businessType.getName(),
                    businessType.getDescription(),
                    businessType.getCreateUserId() != null ? businessType.getCreateUserId() : "admin",
                    businessType.getUpdateUserId() != null ? businessType.getUpdateUserId() : "admin");
        }
    }

    /**
     * 删除业务类型
     */
    public void delete(String code) {
        String sql = "DELETE FROM gnosis_sample.sys_business_types WHERE code = ?";
        jdbcTemplate.update(sql, code);
    }

    private class BusinessTypeRowMapper implements RowMapper<BusinessType> {
        @Override
        public BusinessType mapRow(ResultSet rs, int rowNum) throws SQLException {
            BusinessType businessType = new BusinessType();
            businessType.setCode(rs.getString("code"));
            businessType.setName(rs.getString("name"));
            businessType.setDescription(rs.getString("description"));
            businessType.setCreateUserId(rs.getString("create_user_id"));
            businessType.setUpdateUserId(rs.getString("update_user_id"));
            businessType.setCreateTime(rs.getObject("create_time", OffsetDateTime.class));
            businessType.setUpdateTime(rs.getObject("update_time", OffsetDateTime.class));
            return businessType;
        }
    }
}

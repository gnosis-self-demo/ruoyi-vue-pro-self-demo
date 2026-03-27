package paramcheck.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import paramcheck.domain.ValidationLog;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

/**
 * 校验日志仓储 — 写入 openGauss sys_validation_logs 表
 * 异步写入，不阻塞主校验流程
 *
 * 注意: 共用主项目的 JdbcTemplate
 */
@Repository
public class LogRepository {

    private static final Logger log = LoggerFactory.getLogger(LogRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Async
    public void saveLogAsync(ValidationLog validationLog) {
        try {
            saveLog(validationLog);
        } catch (Exception e) {
            log.error("[LogRepository] async save log failed", e);
        }
    }

    public void saveLog(ValidationLog validationLog) {
        String sql = "INSERT INTO gnosis_sample.sys_validation_logs " +
                "(flow_id, request_id, mode_type, input_snapshot, failed_node, error_msg, created_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String inputJson = null;
        if (validationLog.getInputSnapshot() != null) {
            try {
                inputJson = objectMapper.writeValueAsString(validationLog.getInputSnapshot());
            } catch (JsonProcessingException e) {
                inputJson = String.valueOf(validationLog.getInputSnapshot());
            }
        }

        // openGauss jsonb 列使用 PGobject 传值
        PGobject jsonSnapshot = null;
        if (inputJson != null) {
            try {
                jsonSnapshot = new PGobject();
                jsonSnapshot.setType("jsonb");
                jsonSnapshot.setValue(inputJson);
            } catch (java.sql.SQLException e) {
                log.warn("[LogRepository] failed to create PGobject", e);
                // 回退为字符串
                inputJson = String.valueOf(validationLog.getInputSnapshot());
            }
        }

        jdbcTemplate.update(sql,
                validationLog.getFlowId(),
                validationLog.getRequestId(),
                validationLog.getModeType(),
                jsonSnapshot != null ? jsonSnapshot : inputJson,
                validationLog.getFailedNode(),
                validationLog.getErrorMsg(),
                validationLog.getCreatedTime() != null
                        ? Timestamp.from(validationLog.getCreatedTime().toInstant())
                        : Timestamp.from(OffsetDateTime.now().toInstant()));
    }
}

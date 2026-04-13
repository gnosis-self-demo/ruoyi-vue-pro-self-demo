package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.ValidationLog;
import com.gnosis.paramcheck.dto.PageRequest;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.mapper.ValidationLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ValidationLogService {

    private static final Logger log = LoggerFactory.getLogger(ValidationLogService.class);

    @Autowired
    private ValidationLogMapper logMapper;

    public ValidationLog findById(Long logId) {
        return logMapper.selectByLogId(logId);
    }

    public List<ValidationLog> findAll() {
        return logMapper.selectAll();
    }

    public PageResult<ValidationLog> findPage(String flowId, String requestId, String modeType,
                                               Boolean isActive, PageRequest pageRequest) {
        int total = logMapper.countByCondition(flowId, requestId, modeType, isActive);
        List<ValidationLog> records = logMapper.selectByConditionWithPaging(
                flowId, requestId, modeType, isActive,
                pageRequest.getOffset(), pageRequest.getPageSize());
        return new PageResult<>(records, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    @Async
    public void saveLogAsync(ValidationLog validationLog) {
        try {
            saveLog(validationLog);
        } catch (Exception e) {
            log.error("[ValidationLogService] async save log failed", e);
        }
    }

    public void saveLog(ValidationLog validationLog) {
        if (validationLog.getIsActive() == null) {
            validationLog.setIsActive(true);
        }
        if (validationLog.getCreateUserId() == null) {
            validationLog.setCreateUserId("system");
        }
        if (validationLog.getUpdateUserId() == null) {
            validationLog.setUpdateUserId("system");
        }
        logMapper.insert(validationLog);
    }

    public void delete(Long logId) {
        logMapper.deleteByLogId(logId);
    }

    public void batchDelete(List<Long> logIds) {
        if (logIds != null && !logIds.isEmpty()) {
            logMapper.batchDelete(logIds);
        }
    }

    public void activate(Long logId) {
        logMapper.activate(logId);
    }

    public void deactivate(Long logId) {
        logMapper.deactivate(logId);
    }

    public void batchActivate(List<Long> logIds) {
        if (logIds != null && !logIds.isEmpty()) {
            logMapper.batchActivate(logIds);
        }
    }

    public void batchDeactivate(List<Long> logIds) {
        if (logIds != null && !logIds.isEmpty()) {
            logMapper.batchDeactivate(logIds);
        }
    }
}

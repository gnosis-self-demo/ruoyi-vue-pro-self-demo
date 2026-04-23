package com.gnosis.datamapping.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.datamapping.domain.DataMappingExecutionLog;
import com.gnosis.datamapping.service.DataMappingExecutionLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "数据映射执行日志")
@RestController
@RequestMapping("/datamapping/log")
public class DataMappingExecutionLogController {

    @Autowired
    private DataMappingExecutionLogService logService;

    @ApiOperation("分页查询执行日志")
    @PostMapping("/page")
    public CommonResponse<Map<String, Object>> pageList(@RequestBody Map<String, Object> request) {
        String configId = (String) request.get("configId");
        int pageNum = request.get("pageNum") != null ? (Integer) request.get("pageNum") : 1;
        int pageSize = request.get("pageSize") != null ? (Integer) request.get("pageSize") : 10;

        Long total = logService.countByConfigId(configId);
        List<DataMappingExecutionLog> list = logService.getByConfigId(configId, pageNum, pageSize);

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("list", list);
        return CommonResponse.success(result);
    }

    @ApiOperation("查询日志详情")
    @PostMapping("/detail")
    public CommonResponse<DataMappingExecutionLog> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        return CommonResponse.success(null);
    }
}

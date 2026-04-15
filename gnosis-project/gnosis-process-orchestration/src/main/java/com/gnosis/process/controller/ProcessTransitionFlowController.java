package com.gnosis.process.controller;

import com.gnosis.process.domain.ProcessTransitionFlow;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.IdRequest;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.dto.ProcessTransitionFlowQueryRequest;
import com.gnosis.process.dto.ProcessTransitionFlowVO;
import com.gnosis.process.service.ProcessTransitionFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 流转日志管理 Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/transition-flow")
@Api(tags = "流转日志管理")
public class ProcessTransitionFlowController {

    @Autowired
    private ProcessTransitionFlowService processTransitionFlowService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询流转日志列表")
    public CommonResponse<PageResult<ProcessTransitionFlowVO>> list(@RequestBody ProcessTransitionFlowQueryRequest request) {
        PageResult<ProcessTransitionFlowVO> result = processTransitionFlowService.list(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询流转日志详情")
    public CommonResponse<ProcessTransitionFlow> detail(@RequestBody IdRequest request) {
        ProcessTransitionFlow flow = processTransitionFlowService.getById(request.getId());
        return CommonResponse.success(flow);
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出流转日志数据")
    public void exportData(@RequestBody ProcessTransitionFlowQueryRequest request, HttpServletResponse response) {
        processTransitionFlowService.exportData(request, response);
    }
}

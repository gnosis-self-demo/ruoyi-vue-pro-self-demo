package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.service.ExcelExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(tags = "数据导出管理")
@RestController
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @ApiOperation("执行数据导出")
    @PostMapping("/data-exportor/export/execute")
    public void execute(@RequestBody ExportRequest request, HttpServletResponse response) {
        try {
            excelExportService.exportToExcel(request, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"msg\":\"导出失败: " + e.getMessage().replace("\"", "\\\"") + "\"}");
            } catch (Exception ignored) {
            }
        }
    }

    @ApiOperation("查询导出任务记录")
    @PostMapping("/data-exportor/export/page")
    public BaseResponse<String> page(@RequestBody ExportRequest request) {
        return BaseResponse.success("分页查询功能待实现");
    }

    @ApiOperation("查询导出任务详情")
    @PostMapping("/data-exportor/export/detail")
    public BaseResponse<String> detail(@RequestBody ExportRequest request) {
        return BaseResponse.success("详情查询功能待实现");
    }
}

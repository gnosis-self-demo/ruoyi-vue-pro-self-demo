package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.service.ExcelExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Api(tags = "数据导出管理")
@RestController
@RequestMapping("/data-exportor/export")
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @ApiOperation("执行数据导出")
    @PostMapping("/execute")
    public void execute(@RequestBody ExportRequest request, HttpServletResponse response) {
        try {
            excelExportService.exportToExcel(request, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"msg\":\"导出失败: " + e.getMessage() + "\"}");
            } catch (Exception ignored) {
            }
        }
    }

    @ApiOperation("查询导出任务记录")
    @PostMapping("/page")
    public CommonResponse<String> page(@RequestBody ExportRequest request) {
        return CommonResponse.success("分页查询功能待实现");
    }

    @ApiOperation("查询导出任务详情")
    @PostMapping("/detail")
    public CommonResponse<String> detail(@RequestBody ExportRequest request) {
        return CommonResponse.success("详情查询功能待实现");
    }
}
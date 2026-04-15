package com.gnosis.process.controller;

import com.gnosis.process.domain.BusinessResourceBinding;
import com.gnosis.process.dto.BusinessResourceBindingCreateRequest;
import com.gnosis.process.dto.BusinessResourceBindingQueryRequest;
import com.gnosis.process.dto.BusinessResourceBindingUpdateRequest;
import com.gnosis.process.dto.BusinessResourceBindingVO;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.IdRequest;
import com.gnosis.process.dto.IdsRequest;
import com.gnosis.process.dto.PageResult;
import com.gnosis.process.service.BusinessResourceBindingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 业务资源绑定管理 Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/resource-binding")
@Api(tags = "业务资源绑定管理")
public class BusinessResourceBindingController {

    @Autowired
    private BusinessResourceBindingService businessResourceBindingService;

    @PostMapping("/list")
    @ApiOperation(value = "分页查询业务资源绑定列表")
    public CommonResponse<PageResult<BusinessResourceBindingVO>> list(@RequestBody BusinessResourceBindingQueryRequest request) {
        PageResult<BusinessResourceBindingVO> result = businessResourceBindingService.list(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "查询业务资源绑定详情")
    public CommonResponse<BusinessResourceBinding> detail(@RequestBody IdRequest request) {
        BusinessResourceBinding binding = businessResourceBindingService.getById(request.getId());
        return CommonResponse.success(binding);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增业务资源绑定")
    public CommonResponse<String> save(@RequestBody BusinessResourceBindingCreateRequest request) {
        int rows = businessResourceBindingService.save(request);
        return CommonResponse.success("新增成功，影响行数：" + rows);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新业务资源绑定")
    public CommonResponse<String> update(@RequestBody BusinessResourceBindingUpdateRequest request) {
        int rows = businessResourceBindingService.update(request);
        return CommonResponse.success("更新成功，影响行数：" + rows);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除业务资源绑定")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        int rows = businessResourceBindingService.delete(request.getId());
        return CommonResponse.success("删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchDelete")
    @ApiOperation(value = "批量删除业务资源绑定")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        int rows = businessResourceBindingService.batchDelete(request.getIds());
        return CommonResponse.success("批量删除成功，影响行数：" + rows);
    }

    @PostMapping("/batchEnable")
    @ApiOperation(value = "批量启用业务资源绑定")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        int rows = businessResourceBindingService.batchEnable(request.getIds(), "system");
        return CommonResponse.success("批量启用成功，影响行数：" + rows);
    }

    @PostMapping("/batchDisable")
    @ApiOperation(value = "批量禁用业务资源绑定")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        int rows = businessResourceBindingService.batchDisable(request.getIds(), "system");
        return CommonResponse.success("批量禁用成功，影响行数：" + rows);
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入业务资源绑定数据")
    public CommonResponse<String> importData(@RequestParam("file") MultipartFile file) {
        businessResourceBindingService.importData(file, "system");
        return CommonResponse.success("导入成功");
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出业务资源绑定数据")
    public void exportData(@RequestBody BusinessResourceBindingQueryRequest request, HttpServletResponse response) {
        businessResourceBindingService.exportData(request, response);
    }
}

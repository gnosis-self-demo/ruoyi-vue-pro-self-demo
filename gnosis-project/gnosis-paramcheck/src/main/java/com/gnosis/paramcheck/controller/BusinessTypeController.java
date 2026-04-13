package com.gnosis.paramcheck.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.paramcheck.domain.BusinessType;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.service.BusinessTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/paramcheck/business-types")
public class BusinessTypeController {

    private static final Logger log = LoggerFactory.getLogger(BusinessTypeController.class);

    @Autowired
    private BusinessTypeService businessTypeService;

    @GetMapping("/page")
    public CommonResponse<PageResult<BusinessType>> pageQuery(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive) {
        com.gnosis.paramcheck.dto.PageRequest pageRequest = new com.gnosis.paramcheck.dto.PageRequest();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        PageResult<BusinessType> result = businessTypeService.findPage(code, name, isActive, pageRequest);
        return CommonResponse.success(result);
    }

    @GetMapping
    public CommonResponse<List<BusinessType>> getAllBusinessTypes() {
        return CommonResponse.success(businessTypeService.findAll());
    }

    @GetMapping("/active")
    public CommonResponse<List<BusinessType>> getActiveBusinessTypes() {
        return CommonResponse.success(businessTypeService.findAllActive());
    }

    @GetMapping("/{code}")
    public CommonResponse<BusinessType> getBusinessType(@PathVariable String code) {
        BusinessType businessType = businessTypeService.findByCode(code);
        if (businessType == null) {
            return CommonResponse.error(404, "Business type not found: " + code);
        }
        return CommonResponse.success(businessType);
    }

    @PostMapping
    public CommonResponse<Void> saveBusinessType(@RequestBody BusinessType businessType) {
        try {
            businessTypeService.save(businessType);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to save business type: {}", businessType.getCode(), e);
            return CommonResponse.error("保存失败: " + e.getMessage());
        }
    }

    @PutMapping
    public CommonResponse<Void> updateBusinessType(@RequestBody BusinessType businessType) {
        try {
            businessTypeService.save(businessType);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to update business type: {}", businessType.getCode(), e);
            return CommonResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{code}")
    public CommonResponse<Void> deleteBusinessType(@PathVariable String code) {
        try {
            businessTypeService.delete(code);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to delete business type: {}", code, e);
            return CommonResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/delete")
    public CommonResponse<Void> batchDelete(@RequestBody List<String> codes) {
        try {
            businessTypeService.batchDelete(codes);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to batch delete", e);
            return CommonResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/activate")
    public CommonResponse<Void> batchActivate(@RequestBody List<String> codes) {
        try {
            businessTypeService.batchActivate(codes);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to batch activate", e);
            return CommonResponse.error("批量启用失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/deactivate")
    public CommonResponse<Void> batchDeactivate(@RequestBody List<String> codes) {
        try {
            businessTypeService.batchDeactivate(codes);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to batch deactivate", e);
            return CommonResponse.error("批量禁用失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportBusinessTypes(HttpServletResponse response) {
        try {
            List<BusinessType> list = businessTypeService.findAll();
            String json = com.alibaba.fastjson.JSON.toJSONString(list);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=business_types.json");
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("[BusinessTypeController] export failed", e);
        }
    }

    @PostMapping("/import")
    public CommonResponse<Void> importBusinessTypes(@RequestBody List<BusinessType> list) {
        try {
            for (BusinessType bt : list) {
                businessTypeService.save(bt);
            }
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[BusinessTypeController] import failed", e);
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }
}

package com.gnosis.openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.gnosis.openplat.domain.OpenplatWebhookConfig;
import com.gnosis.openplat.dto.*;
import com.gnosis.openplat.service.OpenplatWebhookConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Webhook 配置管理")
@RestController
@RequestMapping("/openplat/webhook-config")
public class OpenplatWebhookConfigController {

    @Autowired
    private OpenplatWebhookConfigService webhookConfigService;

    @ApiOperation("查询 Webhook 配置列表")
    @PostMapping("/list")
    public CommonResponse<List<OpenplatWebhookConfig>> list(@RequestBody(required = false) OpenplatWebhookConfig webhookConfig) {
        List<OpenplatWebhookConfig> list = webhookConfigService.list(webhookConfig);
        return CommonResponse.success(list);
    }

    @ApiOperation("根据 ID 查询 Webhook 配置详情")
    @PostMapping("/detail")
    public CommonResponse<OpenplatWebhookConfig> detail(@RequestBody IdRequest request) {
        OpenplatWebhookConfig webhookConfig = webhookConfigService.getById(request.getId());
        return CommonResponse.success(webhookConfig);
    }

    @ApiOperation("新增 Webhook 配置")
    @PostMapping("/save")
    public CommonResponse<String> save(@RequestBody OpenplatWebhookConfig webhookConfig) {
        webhookConfigService.save(webhookConfig);
        return CommonResponse.success("新增成功");
    }

    @ApiOperation("修改 Webhook 配置")
    @PostMapping("/update")
    public CommonResponse<String> update(@RequestBody OpenplatWebhookConfig webhookConfig) {
        webhookConfigService.update(webhookConfig);
        return CommonResponse.success("修改成功");
    }

    @ApiOperation("删除 Webhook 配置")
    @PostMapping("/delete")
    public CommonResponse<String> delete(@RequestBody IdRequest request) {
        webhookConfigService.delete(request.getId());
        return CommonResponse.success("删除成功");
    }

    @ApiOperation("批量删除 Webhook 配置")
    @PostMapping("/batchDelete")
    public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
        webhookConfigService.batchDelete(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量删除成功");
    }

    @ApiOperation("批量启用 Webhook")
    @PostMapping("/batchEnable")
    public CommonResponse<String> batchEnable(@RequestBody IdsRequest request) {
        webhookConfigService.batchEnable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量启用成功");
    }

    @ApiOperation("批量禁用 Webhook")
    @PostMapping("/batchDisable")
    public CommonResponse<String> batchDisable(@RequestBody IdsRequest request) {
        webhookConfigService.batchDisable(request.getIds().toArray(new String[0]));
        return CommonResponse.success("批量禁用成功");
    }
}

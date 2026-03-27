package openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import openplat.domain.OpenplatWebhookConfig;
import openplat.dto.CommonResponse;
import openplat.service.OpenplatWebhookConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Webhook 配置管理 Controller
 */
@Api(tags = "Webhook 配置管理")
@RestController
@RequestMapping("/openplat/webhook-config")
public class OpenplatWebhookConfigController {
    
    @Autowired
    private OpenplatWebhookConfigService webhookConfigService;
    
    @ApiOperation("分页查询 Webhook 配置列表")
    @GetMapping("/list")
    public CommonResponse<List<OpenplatWebhookConfig>> list(OpenplatWebhookConfig webhookConfig) {
        List<OpenplatWebhookConfig> list = webhookConfigService.list(webhookConfig);
        return CommonResponse.success(list);
    }
    
    @ApiOperation("根据 ID 查询 Webhook 配置详情")
    @GetMapping("/{id}")
    public CommonResponse<OpenplatWebhookConfig> getById(@PathVariable String id) {
        OpenplatWebhookConfig webhookConfig = webhookConfigService.getById(id);
        return CommonResponse.success(webhookConfig);
    }
    
    @ApiOperation("新增 Webhook 配置")
    @PostMapping
    public CommonResponse<String> save(@RequestBody OpenplatWebhookConfig webhookConfig) {
        webhookConfigService.save(webhookConfig);
        return CommonResponse.success("新增成功");
    }
    
    @ApiOperation("修改 Webhook 配置")
    @PutMapping
    public CommonResponse<String> update(@RequestBody OpenplatWebhookConfig webhookConfig) {
        webhookConfigService.update(webhookConfig);
        return CommonResponse.success("修改成功");
    }
    
    @ApiOperation("删除 Webhook 配置")
    @DeleteMapping("/{id}")
    public CommonResponse<String> delete(@PathVariable String id) {
        webhookConfigService.delete(id);
        return CommonResponse.success("删除成功");
    }
    
    @ApiOperation("批量删除 Webhook 配置")
    @DeleteMapping("/batch")
    public CommonResponse<String> batchDelete(@RequestBody String[] ids) {
        webhookConfigService.batchDelete(ids);
        return CommonResponse.success("批量删除成功");
    }
    
    @ApiOperation("批量启用 Webhook")
    @PutMapping("/enable")
    public CommonResponse<String> batchEnable(@RequestBody String[] ids) {
        webhookConfigService.batchEnable(ids);
        return CommonResponse.success("批量启用成功");
    }
    
    @ApiOperation("批量禁用 Webhook")
    @PutMapping("/disable")
    public CommonResponse<String> batchDisable(@RequestBody String[] ids) {
        webhookConfigService.batchDisable(ids);
        return CommonResponse.success("批量禁用成功");
    }
}

package openplat.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import openplat.domain.OpenplatAppAuth;
import openplat.dto.CommonResponse;
import openplat.service.OpenplatAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 应用认证管理 Controller
 */
@Api(tags = "应用认证管理")
@RestController
@RequestMapping("/openplat/app-auth")
public class OpenplatAppAuthController {
    
    @Autowired
    private OpenplatAppAuthService appAuthService;
    
    @ApiOperation("分页查询应用认证列表")
    @GetMapping("/list")
    public CommonResponse<List<OpenplatAppAuth>> list(OpenplatAppAuth appAuth) {
        List<OpenplatAppAuth> list = appAuthService.list(appAuth);
        return CommonResponse.success(list);
    }
    
    @ApiOperation("根据 ID 查询应用认证详情")
    @GetMapping("/{id}")
    public CommonResponse<OpenplatAppAuth> getById(@PathVariable String id) {
        OpenplatAppAuth appAuth = appAuthService.getById(id);
        return CommonResponse.success(appAuth);
    }
    
    @ApiOperation("新增应用认证")
    @PostMapping
    public CommonResponse<String> save(@RequestBody OpenplatAppAuth appAuth) {
        appAuthService.save(appAuth);
        return CommonResponse.success("新增成功");
    }
    
    @ApiOperation("修改应用认证")
    @PutMapping
    public CommonResponse<String> update(@RequestBody OpenplatAppAuth appAuth) {
        appAuthService.update(appAuth);
        return CommonResponse.success("修改成功");
    }
    
    @ApiOperation("删除应用认证")
    @DeleteMapping("/{id}")
    public CommonResponse<String> delete(@PathVariable String id) {
        appAuthService.delete(id);
        return CommonResponse.success("删除成功");
    }
    
    @ApiOperation("批量删除应用认证")
    @DeleteMapping("/batch")
    public CommonResponse<String> batchDelete(@RequestBody String[] ids) {
        appAuthService.batchDelete(ids);
        return CommonResponse.success("批量删除成功");
    }
    
    @ApiOperation("批量启用应用")
    @PutMapping("/enable")
    public CommonResponse<String> batchEnable(@RequestBody String[] ids) {
        appAuthService.batchEnable(ids);
        return CommonResponse.success("批量启用成功");
    }
    
    @ApiOperation("批量禁用应用")
    @PutMapping("/disable")
    public CommonResponse<String> batchDisable(@RequestBody String[] ids) {
        appAuthService.batchDisable(ids);
        return CommonResponse.success("批量禁用成功");
    }
    
    @ApiOperation("验证应用认证")
    @PostMapping("/validate")
    public CommonResponse<Boolean> validate(@RequestParam String appId, @RequestParam String appSecret) {
        boolean result = appAuthService.validateAppAuth(appId, appSecret);
        return CommonResponse.success(result);
    }
}

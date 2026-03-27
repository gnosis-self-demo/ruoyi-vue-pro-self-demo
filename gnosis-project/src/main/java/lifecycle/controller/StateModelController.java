package lifecycle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lifecycle.dto.StateModelCreateRequest;
import lifecycle.dto.StateModelVO;
import lifecycle.service.StateModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 状态模型 REST API 控制器
 */
@RestController
@RequestMapping("/api/lifecycle/state/model")
@Api(tags = "状态模型管理", description = "状态模型 CRUD 操作 API")
public class StateModelController {

    private static final Logger log = LoggerFactory.getLogger(StateModelController.class);

    @Autowired
    private StateModelService stateModelService;

    /**
     * 创建状态模型
     */
    @PostMapping
    @ApiOperation(value = "创建状态模型", notes = "创建新的状态模型，包括状态节点和流转")
    public ResponseEntity<StateModelVO> create(
            @ApiParam(value = "状态模型创建请求", required = true)
            @RequestBody StateModelCreateRequest request) {
        log.info("[StateModelController] 创建状态模型：businessTypeId={}, modelCode={}", 
                request.getBusinessTypeId(), request.getModelCode());

        try {
            StateModelVO result = stateModelService.create(request);
            log.info("[StateModelController] 状态模型创建成功：businessTypeId={}", result.getBusinessTypeId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[StateModelController] 创建失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[StateModelController] 创建异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取状态模型
     */
    @GetMapping("/{businessTypeId}")
    @ApiOperation(value = "获取状态模型", notes = "根据业务类型 ID 获取状态模型详细信息")
    public ResponseEntity<StateModelVO> getModel(
            @ApiParam(value = "业务类型 ID", required = true)
            @PathVariable String businessTypeId) {
        log.debug("[StateModelController] 获取状态模型：businessTypeId={}", businessTypeId);

        try {
            StateModelVO result = stateModelService.getModel(businessTypeId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[StateModelController] 获取详情失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[StateModelController] 获取详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新状态模型
     */
    @PutMapping("/{businessTypeId}")
    @ApiOperation(value = "更新状态模型", notes = "更新指定业务类型的状态模型")
    public ResponseEntity<StateModelVO> update(
            @ApiParam(value = "业务类型 ID", required = true)
            @PathVariable String businessTypeId,
            @ApiParam(value = "状态模型创建请求", required = true)
            @RequestBody StateModelCreateRequest request) {
        log.info("[StateModelController] 更新状态模型：businessTypeId={}", businessTypeId);

        try {
            StateModelVO result = stateModelService.update(businessTypeId, request);
            log.info("[StateModelController] 状态模型更新成功：businessTypeId={}", businessTypeId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[StateModelController] 更新失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[StateModelController] 更新异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除状态模型
     */
    @DeleteMapping("/{businessTypeId}")
    @ApiOperation(value = "删除状态模型", notes = "删除指定业务类型的状态模型")
    public ResponseEntity<Void> delete(
            @ApiParam(value = "业务类型 ID", required = true)
            @PathVariable String businessTypeId) {
        log.info("[StateModelController] 删除状态模型：businessTypeId={}", businessTypeId);

        try {
            stateModelService.delete(businessTypeId);
            log.info("[StateModelController] 状态模型删除成功：businessTypeId={}", businessTypeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("[StateModelController] 删除失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[StateModelController] 删除异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}

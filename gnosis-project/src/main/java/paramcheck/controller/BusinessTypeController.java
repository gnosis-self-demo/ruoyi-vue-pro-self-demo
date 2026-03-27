package paramcheck.controller;

import paramcheck.domain.BusinessType;
import paramcheck.repository.BusinessTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业务类型管理API
 */
@RestController
@RequestMapping("/api/paramcheck/business-types")
public class BusinessTypeController {

    private static final Logger log = LoggerFactory.getLogger(BusinessTypeController.class);

    @Autowired
    private BusinessTypeRepository businessTypeRepository;

    /**
     * 获取所有业务类型
     */
    @GetMapping
    public ResponseEntity<List<BusinessType>> getAllBusinessTypes() {
        List<BusinessType> businessTypes = businessTypeRepository.findAll();
        return ResponseEntity.ok(businessTypes);
    }

    /**
     * 根据编码获取业务类型
     */
    @GetMapping("/{code}")
    public ResponseEntity<BusinessType> getBusinessType(@PathVariable String code) {
        BusinessType businessType = businessTypeRepository.findByCode(code);
        return businessType != null ? ResponseEntity.ok(businessType) : ResponseEntity.notFound().build();
    }

    /**
     * 保存业务类型（创建或更新）
     */
    @PostMapping
    public ResponseEntity<Void> saveBusinessType(@RequestBody BusinessType businessType) {
        try {
            businessTypeRepository.save(businessType);
            log.info("[BusinessTypeController] saved business type: {}", businessType.getCode());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to save business type: {}", businessType.getCode(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除业务类型
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteBusinessType(@PathVariable String code) {
        try {
            businessTypeRepository.delete(code);
            log.info("[BusinessTypeController] deleted business type: {}", code);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[BusinessTypeController] failed to delete business type: {}", code, e);
            return ResponseEntity.badRequest().build();
        }
    }
}

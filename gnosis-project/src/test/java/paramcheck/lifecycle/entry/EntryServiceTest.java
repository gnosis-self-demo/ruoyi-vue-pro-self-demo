package paramcheck.lifecycle.entry;

import lifecycle.dto.*;
import lifecycle.entry.BusinessTypeManager;
import lifecycle.entry.EntryServiceImpl;
import lifecycle.entry.EntryValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.domain.LifecycleBusinessType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 入口服务测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class EntryServiceTest {

    @Mock
    private BusinessTypeManager businessTypeManager;

    @Mock
    private EntryValidator entryValidator;

    @InjectMocks
    private EntryServiceImpl entryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试创建业务类型
     */
    @Test
    public void testCreateBusinessType() {
        // 准备测试数据
        BusinessTypeCreateRequest request = new BusinessTypeCreateRequest();
        request.setName("测试业务类型");
        request.setCode("TEST_BIZ_TYPE");
        request.setDescription("测试描述");

        LifecycleBusinessType mockType = new LifecycleBusinessType();
        mockType.setId("test_id_123");
        mockType.setName("测试业务类型");
        mockType.setCode("TEST_BIZ_TYPE");
        mockType.setDescription("测试描述");
        mockType.setCreateUserId("system");
        mockType.setCreateTime(new Date());

        // 配置 Mock 行为
        when(businessTypeManager.create(request)).thenReturn(mockType);

        // 执行测试
        BusinessTypeVO result = entryService.register(request);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("ID 应该匹配", "test_id_123", result.getId());
        assertEquals("名称应该匹配", "测试业务类型", result.getName());
        assertEquals("编码应该匹配", "TEST_BIZ_TYPE", result.getCode());

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).create(request);
    }

    /**
     * 测试更新业务类型
     */
    @Test
    public void testUpdateBusinessType() {
        // 准备测试数据
        BusinessTypeUpdateRequest request = new BusinessTypeUpdateRequest();
        request.setId("test_id_123");
        request.setName("更新后的业务类型");
        request.setCode("TEST_BIZ_TYPE_UPDATED");
        request.setDescription("更新后的描述");

        LifecycleBusinessType mockType = new LifecycleBusinessType();
        mockType.setId("test_id_123");
        mockType.setName("更新后的业务类型");
        mockType.setCode("TEST_BIZ_TYPE_UPDATED");
        mockType.setDescription("更新后的描述");
        mockType.setUpdateUserId("system");
        mockType.setUpdateTime(new Date());

        // 配置 Mock 行为
        when(businessTypeManager.update(request)).thenReturn(mockType);

        // 执行测试
        BusinessTypeVO result = entryService.update(request);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("ID 应该匹配", "test_id_123", result.getId());
        assertEquals("名称应该匹配", "更新后的业务类型", result.getName());

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).update(request);
    }

    /**
     * 测试入口校验
     */
    @Test
    public void testValidateEntry() {
        // 准备测试数据
        EntryValidationRequest request = new EntryValidationRequest();
        request.setBusinessTypeId("test_biz_type_id");
        request.setUserId("test_user_123");
        request.setValidationData("{\"field1\": \"value1\", \"field2\": \"value2\"}");

        LifecycleBusinessType businessType = new LifecycleBusinessType();
        businessType.setId("test_biz_type_id");
        businessType.setName("测试业务类型");
        businessType.setCode("TEST_BIZ_TYPE");
        businessType.setEntryPermissionConfig("{\"disabled\": false}");

        EntryValidationResult mockResult = EntryValidationResult.success("校验通过");

        // 配置 Mock 行为
        when(businessTypeManager.getById(request.getBusinessTypeId())).thenReturn(businessType);
        when(entryValidator.validate(request, businessType)).thenReturn(mockResult);

        // 执行测试
        EntryValidationResult result = entryService.validate(request);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertTrue("校验应该通过", result.isPassed());
        assertEquals("消息应该匹配", "校验通过", result.getMessage());

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).getById(request.getBusinessTypeId());
        verify(entryValidator, times(1)).validate(request, businessType);
    }

    /**
     * 测试入口校验 - 业务类型不存在
     */
    @Test
    public void testValidateEntry_BusinessTypeNotFound() {
        // 准备测试数据
        EntryValidationRequest request = new EntryValidationRequest();
        request.setBusinessTypeId("non_existent_id");
        request.setUserId("test_user_123");

        // 配置 Mock 行为
        when(businessTypeManager.getById(request.getBusinessTypeId())).thenReturn(null);

        // 执行测试
        EntryValidationResult result = entryService.validate(request);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertFalse("校验应该失败", result.isPassed());
        assertTrue("错误消息应包含业务类型不存在", result.getMessage().contains("业务类型不存在"));

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).getById(request.getBusinessTypeId());
        verify(entryValidator, never()).validate(any(), any());
    }

    /**
     * 测试批量删除
     */
    @Test
    public void testBatchDelete() {
        // 准备测试数据
        String[] ids = {"id1", "id2", "id3"};

        // 执行测试
        entryService.batchDelete(ids);

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).batchDelete(ids);
    }

    /**
     * 测试批量启用
     */
    @Test
    public void testBatchEnableDisable() {
        // 准备测试数据
        String[] ids = {"id1", "id2", "id3"};

        LifecycleBusinessType businessType1 = new LifecycleBusinessType();
        businessType1.setId("id1");
        businessType1.setEntryPermissionConfig("{\"disabled\": true}");

        LifecycleBusinessType businessType2 = new LifecycleBusinessType();
        businessType2.setId("id2");
        businessType2.setEntryPermissionConfig("{\"disabled\": true}");

        LifecycleBusinessType businessType3 = new LifecycleBusinessType();
        businessType3.setId("id3");
        businessType3.setEntryPermissionConfig("{\"disabled\": true}");

        // 配置 Mock 行为
        when(businessTypeManager.getById("id1")).thenReturn(businessType1);
        when(businessTypeManager.getById("id2")).thenReturn(businessType2);
        when(businessTypeManager.getById("id3")).thenReturn(businessType3);
        when(businessTypeManager.update(any(BusinessTypeUpdateRequest.class))).thenReturn(null);

        // 执行测试
        entryService.batchEnable(ids);

        // 验证 Mock 调用 - 每个 ID 都应该被查询和更新
        verify(businessTypeManager, times(1)).getById("id1");
        verify(businessTypeManager, times(1)).getById("id2");
        verify(businessTypeManager, times(1)).getById("id3");
        verify(businessTypeManager, times(3)).update(any(BusinessTypeUpdateRequest.class));
    }

    /**
     * 测试批量禁用
     */
    @Test
    public void testBatchDisable() {
        // 准备测试数据
        String[] ids = {"id1", "id2", "id3"};

        LifecycleBusinessType businessType1 = new LifecycleBusinessType();
        businessType1.setId("id1");
        businessType1.setEntryPermissionConfig("{\"disabled\": false}");

        LifecycleBusinessType businessType2 = new LifecycleBusinessType();
        businessType2.setId("id2");
        businessType2.setEntryPermissionConfig("{\"disabled\": false}");

        LifecycleBusinessType businessType3 = new LifecycleBusinessType();
        businessType3.setId("id3");
        businessType3.setEntryPermissionConfig("{\"disabled\": false}");

        // 配置 Mock 行为
        when(businessTypeManager.getById("id1")).thenReturn(businessType1);
        when(businessTypeManager.getById("id2")).thenReturn(businessType2);
        when(businessTypeManager.getById("id3")).thenReturn(businessType3);
        when(businessTypeManager.update(any(BusinessTypeUpdateRequest.class))).thenReturn(null);

        // 执行测试
        entryService.batchDisable(ids);

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).getById("id1");
        verify(businessTypeManager, times(1)).getById("id2");
        verify(businessTypeManager, times(1)).getById("id3");
        verify(businessTypeManager, times(3)).update(any(BusinessTypeUpdateRequest.class));
    }

    /**
     * 测试获取业务类型列表
     */
    @Test
    public void testList() {
        // 准备测试数据
        LifecycleBusinessType query = new LifecycleBusinessType();
        query.setCode("TEST");

        List<LifecycleBusinessType> mockList = new ArrayList<>();
        LifecycleBusinessType type1 = new LifecycleBusinessType();
        type1.setId("id1");
        type1.setName("类型 1");
        type1.setCode("TEST_1");
        mockList.add(type1);

        LifecycleBusinessType type2 = new LifecycleBusinessType();
        type2.setId("id2");
        type2.setName("类型 2");
        type2.setCode("TEST_2");
        mockList.add(type2);

        // 配置 Mock 行为
        when(businessTypeManager.list(query)).thenReturn(mockList);

        // 执行测试
        List<BusinessTypeVO> result = entryService.list(query, 1, 10);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("列表大小应该匹配", 2, result.size());
        assertEquals("第一个元素名称应该匹配", "类型 1", result.get(0).getName());
        assertEquals("第二个元素名称应该匹配", "类型 2", result.get(1).getName());

        // 验证 Mock 调用
        verify(businessTypeManager, times(1)).list(query);
    }
}

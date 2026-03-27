package lifecycle.api;

import com.alibaba.fastjson.JSON;
import lifecycle.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import lifecycle.domain.LifecycleBusinessType;
import lifecycle.entry.EntryController;
import lifecycle.entry.EntryService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 入口 API 测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class EntryApiTest {

    private MockMvc mockMvc;

    @Mock
    private EntryService entryService;

    @InjectMocks
    private EntryController entryController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(entryController).build();
    }

    /**
     * 测试注册业务入口
     */
    @Test
    public void testRegisterEntry() throws Exception {
        // 准备测试数据
        BusinessTypeCreateRequest request = new BusinessTypeCreateRequest();
        request.setName("测试业务类型");
        request.setCode("TEST_BIZ_TYPE");
        request.setDescription("测试描述");

        BusinessTypeVO responseVO = new BusinessTypeVO();
        responseVO.setId("test_id_123");
        responseVO.setName("测试业务类型");
        responseVO.setCode("TEST_BIZ_TYPE");
        responseVO.setDescription("测试描述");

        // 配置 Mock 行为
        when(entryService.register(any(BusinessTypeCreateRequest.class))).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/entry/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test_id_123"))
                .andExpect(jsonPath("$.name").value("测试业务类型"))
                .andExpect(jsonPath("$.code").value("TEST_BIZ_TYPE"));

        // 验证 Mock 调用
        verify(entryService, times(1)).register(any(BusinessTypeCreateRequest.class));
    }

    /**
     * 测试获取入口列表
     */
    @Test
    public void testGetEntryList() throws Exception {
        // 准备测试数据
        List<BusinessTypeVO> mockList = new ArrayList<>();
        BusinessTypeVO vo1 = new BusinessTypeVO();
        vo1.setId("id1");
        vo1.setName("类型 1");
        vo1.setCode("TYPE_1");
        mockList.add(vo1);

        BusinessTypeVO vo2 = new BusinessTypeVO();
        vo2.setId("id2");
        vo2.setName("类型 2");
        vo2.setCode("TYPE_2");
        mockList.add(vo2);

        // 配置 Mock 行为
        when(entryService.list(any(LifecycleBusinessType.class), anyInt(), anyInt())).thenReturn(mockList);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/entry/list")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("id1"))
                .andExpect(jsonPath("$[1].id").value("id2"));

        // 验证 Mock 调用
        verify(entryService, times(1)).list(any(LifecycleBusinessType.class), eq(1), eq(10));
    }

    /**
     * 测试更新入口
     */
    @Test
    public void testUpdateEntry() throws Exception {
        // 准备测试数据
        BusinessTypeUpdateRequest request = new BusinessTypeUpdateRequest();
        request.setName("更新后的类型");
        request.setCode("UPDATED_TYPE");
        request.setDescription("更新后的描述");

        BusinessTypeVO responseVO = new BusinessTypeVO();
        responseVO.setId("test_id_123");
        responseVO.setName("更新后的类型");
        responseVO.setCode("UPDATED_TYPE");

        // 配置 Mock 行为
        when(entryService.update(any(BusinessTypeUpdateRequest.class))).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(put("/api/lifecycle/entry/{id}", "test_id_123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test_id_123"))
                .andExpect(jsonPath("$.name").value("更新后的类型"));

        // 验证 Mock 调用
        verify(entryService, times(1)).update(any(BusinessTypeUpdateRequest.class));
    }

    /**
     * 测试删除入口
     */
    @Test
    public void testDeleteEntry() throws Exception {
        // 配置 Mock 行为
        doNothing().when(entryService).delete("test_id_123");

        // 执行请求
        mockMvc.perform(delete("/api/lifecycle/entry/{id}", "test_id_123"))
                .andExpect(status().isOk());

        // 验证 Mock 调用
        verify(entryService, times(1)).delete("test_id_123");
    }

    /**
     * 测试批量操作
     */
    @Test
    public void testBatchOperations() throws Exception {
        // 准备测试数据
        String[] ids = {"id1", "id2", "id3"};

        // 配置 Mock 行为
        doNothing().when(entryService).batchDelete(any(String[].class));
        doNothing().when(entryService).batchEnable(any(String[].class));
        doNothing().when(entryService).batchDisable(any(String[].class));

        // 测试批量删除
        mockMvc.perform(delete("/api/lifecycle/entry/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(3));

        // 测试批量启用
        mockMvc.perform(post("/api/lifecycle/entry/batch/enable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 测试批量禁用
        mockMvc.perform(post("/api/lifecycle/entry/batch/disable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证 Mock 调用
        verify(entryService, times(1)).batchDelete(any(String[].class));
        verify(entryService, times(1)).batchEnable(any(String[].class));
        verify(entryService, times(1)).batchDisable(any(String[].class));
    }

    /**
     * 测试入口校验 API
     */
    @Test
    public void testValidateEntryApi() throws Exception {
        // 准备测试数据
        EntryValidationRequest request = new EntryValidationRequest();
        request.setBusinessTypeId("test_biz_type");
        request.setUserId("test_user");
        request.setValidationData("{\"field\": \"value\"}");

        EntryValidationResult successResult = EntryValidationResult.success("校验通过");

        // 配置 Mock 行为
        when(entryService.validate(any(EntryValidationRequest.class))).thenReturn(successResult);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/entry/{id}/validate", "test_biz_type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passed").value(true))
                .andExpect(jsonPath("$.message").value("校验通过"));

        // 验证 Mock 调用
        verify(entryService, times(1)).validate(any(EntryValidationRequest.class));
    }

    /**
     * 测试入口校验失败
     */
    @Test
    public void testValidateEntryApi_Failure() throws Exception {
        // 准备测试数据
        EntryValidationRequest request = new EntryValidationRequest();
        request.setBusinessTypeId("test_biz_type");
        request.setUserId("test_user");

        EntryValidationResult failureResult = EntryValidationResult.fail("PERMISSION_DENIED", "权限不足");

        // 配置 Mock 行为
        when(entryService.validate(any(EntryValidationRequest.class))).thenReturn(failureResult);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/entry/{id}/validate", "test_biz_type")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.passed").value(false))
                .andExpect(jsonPath("$.errorCode").value("PERMISSION_DENIED"));

        // 验证 Mock 调用
        verify(entryService, times(1)).validate(any(EntryValidationRequest.class));
    }

    /**
     * 测试获取入口详情
     */
    @Test
    public void testGetEntryDetail() throws Exception {
        // 准备测试数据
        BusinessTypeVO responseVO = new BusinessTypeVO();
        responseVO.setId("test_id_123");
        responseVO.setName("测试类型");
        responseVO.setCode("TEST_TYPE");
        responseVO.setDescription("测试描述");

        // 配置 Mock 行为
        when(entryService.getById("test_id_123")).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/entry/{id}", "test_id_123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test_id_123"))
                .andExpect(jsonPath("$.name").value("测试类型"));

        // 验证 Mock 调用
        verify(entryService, times(1)).getById("test_id_123");
    }

    /**
     * 测试获取不存在的入口
     */
    @Test
    public void testGetNonExistentEntry() throws Exception {
        // 配置 Mock 行为
        when(entryService.getById("non_existent_id")).thenThrow(new IllegalArgumentException("业务入口不存在"));

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/entry/{id}", "non_existent_id"))
                .andExpect(status().isNotFound());

        // 验证 Mock 调用
        verify(entryService, times(1)).getById("non_existent_id");
    }

    /**
     * 测试注册入口 - 参数校验失败
     */
    @Test
    public void testRegisterEntry_ValidationFailure() throws Exception {
        // 准备测试数据 - 缺少必填字段
        BusinessTypeCreateRequest request = new BusinessTypeCreateRequest();
        request.setCode(""); // 空编码

        // 配置 Mock 行为
        when(entryService.register(any(BusinessTypeCreateRequest.class)))
                .thenThrow(new IllegalArgumentException("编码不能为空"));

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/entry/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isBadRequest());

        // 验证 Mock 调用
        verify(entryService, times(1)).register(any(BusinessTypeCreateRequest.class));
    }

    /**
     * 测试批量操作 - 空数组
     */
    @Test
    public void testBatchOperations_EmptyArray() throws Exception {
        // 准备测试数据
        String[] emptyIds = {};

        // 配置 Mock 行为
        doNothing().when(entryService).batchDelete(emptyIds);

        // 执行请求
        mockMvc.perform(delete("/api/lifecycle/entry/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(emptyIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));

        // 验证 Mock 调用
        verify(entryService, times(1)).batchDelete(emptyIds);
    }
}

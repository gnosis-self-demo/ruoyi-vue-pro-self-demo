<template>
  <div class="flow-config-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>流程配置管理</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增配置
            </el-button>
            <el-button 
              type="warning" 
              @click="handleBatchActivate" 
              :disabled="selectedFlows.length === 0"
            >
              批量启用
            </el-button>
            <el-button 
              type="danger" 
              @click="handleBatchDeactivate" 
              :disabled="selectedFlows.length === 0"
            >
              批量禁用
            </el-button>
            <el-button 
              type="danger" 
              @click="handleBatchDelete" 
              :disabled="selectedFlows.length === 0"
            >
              批量删除
            </el-button>
          </div>
        </div>
      </template>
      <!-- 条件查询表单 -->
      <div class="search-form">
        <el-form :model="searchForm" inline>
          <el-form-item label="流程ID">
            <el-input v-model="searchForm.flowId" placeholder="请输入流程ID" style="width: 150px" />
          </el-form-item>
          <el-form-item label="流程名称">
            <el-input v-model="searchForm.flowName" placeholder="请输入流程名称" style="width: 150px" />
          </el-form-item>
          <el-form-item label="业务类型">
            <el-select v-model="searchForm.businessType" placeholder="请选择业务类型" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option v-for="type in businessTypes" :key="type.code" :label="type.name" :value="type.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="模式类型">
            <el-select v-model="searchForm.modeType" placeholder="请选择模式类型" style="width: 150px">
              <el-option label="全部" value="" />
              <el-option label="FLOW" value="FLOW" />
              <el-option label="HANDLER" value="HANDLER" />
              <el-option label="HYBRID" value="HYBRID" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.isActive" placeholder="请选择状态" style="width: 100px">
              <el-option label="全部" value="" />
              <el-option label="启用" :value="true" />
              <el-option label="禁用" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div class="flow-table">
        <el-table 
          :data="flows" 
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="flowId" label="流程ID" width="180" />
          <el-table-column prop="flowName" label="流程名称" width="200" />
          <el-table-column label="业务类型" width="150">
            <template #default="scope">
              <span>
                {{ scope.row.businessTypes ? scope.row.businessTypes.split(',').map(getBusinessTypeName).join('、') : '' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="modeType" label="模式类型" width="120">
            <template #default="scope">
              <el-tag
                :type="scope.row.modeType === 'FLOW' ? 'primary' : scope.row.modeType === 'HANDLER' ? 'success' : 'warning'"
              >
                {{ scope.row.modeType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="elExpression" label="EL表达式" />
          <el-table-column prop="handlerCode" label="处理器编码" width="180" />
          <el-table-column prop="isActive" label="状态" width="100">
            <template #default="scope">
              <el-switch
                v-model="scope.row.isActive"
                @change="handleStatusChange(scope.row)"
                active-color="#13ce66"
                inactive-color="#ff4949"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createUserId" label="创建人ID" width="120" />
          <el-table-column label="创建时间" width="180">
            <template #default="scope">
              <span>{{ formatDateTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="updateUserId" label="更新人ID" width="120" />
          <el-table-column label="更新时间" width="180">
            <template #default="scope">
              <span>{{ formatDateTime(scope.row.updatedTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button
                :type="scope.row.isActive ? 'danger' : 'success'"
                size="small"
                @click="scope.row.isActive ? handleDeactivate(scope.row.flowId) : handleActivate(scope.row.flowId)"
              >
                {{ scope.row.isActive ? '禁用' : '启用' }}
              </el-button>
              <el-button size="small" @click="handleRefresh(scope.row.flowId)">
                刷新
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="流程ID" prop="flowId">
          <el-input v-model="form.flowId" placeholder="请输入流程ID" />
        </el-form-item>
        <el-form-item label="流程名称" prop="flowName">
          <el-input v-model="form.flowName" placeholder="请输入流程名称" />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessTypes">
          <el-select v-model="form.businessTypes" multiple placeholder="请选择业务类型">
            <el-option v-for="type in businessTypes" :key="type.code" :label="type.name" :value="type.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="模式类型" prop="modeType">
          <el-select v-model="form.modeType" placeholder="请选择模式类型">
            <el-option label="FLOW" value="FLOW" />
            <el-option label="HANDLER" value="HANDLER" />
            <el-option label="HYBRID" value="HYBRID" />
          </el-select>
        </el-form-item>
        <el-form-item label="EL表达式" prop="elExpression" v-if="form.modeType === 'FLOW' || form.modeType === 'HYBRID'">
          <el-input v-model="form.elExpression" type="textarea" rows="3" placeholder="请输入EL表达式" />
        </el-form-item>
        <el-form-item label="处理器编码" prop="handlerCode" v-if="form.modeType === 'HANDLER' || form.modeType === 'HYBRID'">
          <el-input v-model="form.handlerCode" placeholder="请输入处理器编码" />
        </el-form-item>
        <el-form-item label="组件配置" prop="componentConfig">
          <el-input v-model="form.componentConfigStr" type="textarea" rows="4" placeholder="请输入JSON格式的组件配置" />
        </el-form-item>
        <el-form-item label="是否激活">
          <el-switch v-model="form.isActive" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { configApi } from '../api/configApi'

// 计算业务类型名称映射
const businessTypeMap = computed(() => {
  const map = {}
  businessTypes.value.forEach(type => {
    map[type.code] = type.name
  })
  return map
})

// 获取业务类型中文名称
const getBusinessTypeName = (code) => {
  return businessTypeMap.value[code] || code
}

// 格式化时间为 yyyy-MM-dd hh:mm:ss
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const flows = ref([])
const businessTypes = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增流程配置')
const formRef = ref(null)
const selectedFlows = ref([])

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 条件查询
const searchForm = reactive({
  flowId: '',
  flowName: '',
  businessType: '',
  modeType: '',
  isActive: null
})

const form = reactive({
  flowId: '',
  flowName: '',
  businessTypes: [],
  modeType: 'FLOW',
  elExpression: '',
  handlerCode: '',
  componentConfigStr: '{}',
  isActive: true
})

const rules = reactive({
  flowId: [
    { required: true, message: '请输入流程ID', trigger: 'blur' }
  ],
  flowName: [
    { required: true, message: '请输入流程名称', trigger: 'blur' }
  ],
  businessTypes: [
    { required: true, message: '请选择业务类型', trigger: 'change' }
  ],
  modeType: [
    { required: true, message: '请选择模式类型', trigger: 'change' }
  ],
  elExpression: [
    { required: true, message: '请输入EL表达式', trigger: 'blur' }
  ],
  handlerCode: [
    { required: true, message: '请输入处理器编码', trigger: 'blur' }
  ]
})

// 加载业务类型
const loadBusinessTypes = async () => {
  try {
    const response = await configApi.getAllBusinessTypes()
    businessTypes.value = response.data
  } catch (error) {
    console.error('获取业务类型失败:', error)
    // 发生错误时，使用模拟数据
    businessTypes.value = [
      { code: 'ORDER_CREATE', name: '订单创建' },
      { code: 'USER_REGISTER', name: '用户注册' },
      { code: 'BASIC_DATA', name: '基础数据录入' },
      { code: 'OTHER', name: '其他' }
    ]
  }
}

// 加载流程配置
const loadFlows = async () => {
  try {
    // 构建查询参数
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      flowId: searchForm.flowId,
      flowName: searchForm.flowName,
      businessType: searchForm.businessType,
      modeType: searchForm.modeType,
      isActive: searchForm.isActive
    }
    
    const response = await configApi.getAllFlows(params)
    flows.value = response.data.records || []
    total.value = response.data.total || 0
    
    // 如果没有数据，添加一些模拟数据
    if (flows.value.length === 0) {
      const now = new Date().toISOString()
      flows.value = [
        {
          flowId: 'ORDER_CREATE_FLOW',
          flowName: '订单创建校验 (混合模式)',
          businessTypes: 'ORDER_CREATE',
          modeType: 'HYBRID',
          elExpression: 'THEN(parse_param, check_format, check_db_user)',
          handlerCode: 'OrderBusinessHandler',
          isActive: true,
          createUserId: 'admin',
          createTime: now,
          updateUserId: 'admin',
          updatedTime: now
        },
        {
          flowId: 'USER_REGISTER_FLOW',
          flowName: '用户注册 (纯接口模式)',
          businessTypes: 'USER_REGISTER',
          modeType: 'HANDLER',
          elExpression: null,
          handlerCode: 'UserRegisterHandler',
          isActive: true,
          createUserId: 'admin',
          createTime: now,
          updateUserId: 'admin',
          updatedTime: now
        },
        {
          flowId: 'SIMPLE_CHECK_FLOW',
          flowName: '简单格式校验 (纯编排模式)',
          businessTypes: 'BASIC_DATA',
          modeType: 'FLOW',
          elExpression: 'THEN(parse_param, check_format)',
          handlerCode: null,
          isActive: true,
          createUserId: 'admin',
          createTime: now,
          updateUserId: 'admin',
          updatedTime: now
        },
        {
          flowId: 'PRODUCT_CREATE_FLOW',
          flowName: '商品创建校验 (混合模式)',
          businessTypes: 'PRODUCT_CREATE',
          modeType: 'HYBRID',
          elExpression: 'THEN(parse_param, check_format)',
          handlerCode: 'ProductBusinessHandler',
          isActive: true,
          createUserId: 'admin',
          createTime: now,
          updateUserId: 'admin',
          updatedTime: now
        },
        {
          flowId: 'PAYMENT_VALIDATION_FLOW',
          flowName: '支付校验 (纯接口模式)',
          businessTypes: 'PAYMENT',
          modeType: 'HANDLER',
          elExpression: null,
          handlerCode: 'PaymentValidationHandler',
          isActive: true,
          createUserId: 'admin',
          createTime: now,
          updateUserId: 'admin',
          updatedTime: now
        }
      ]
      total.value = flows.value.length
    }
  } catch (error) {
    console.error('获取流程配置失败:', error)
    // 发生错误时，添加一些模拟数据
    const now = new Date().toISOString()
    flows.value = [
      {
        flowId: 'ORDER_CREATE_FLOW',
        flowName: '订单创建校验 (混合模式)',
        businessTypes: 'ORDER_CREATE',
        modeType: 'HYBRID',
        elExpression: 'THEN(parse_param, check_format, check_db_user)',
        handlerCode: 'OrderBusinessHandler',
        isActive: true,
        createUserId: 'admin',
        createTime: now,
        updateUserId: 'admin',
        updatedTime: now
      },
      {
        flowId: 'USER_REGISTER_FLOW',
        flowName: '用户注册 (纯接口模式)',
        businessTypes: 'USER_REGISTER',
        modeType: 'HANDLER',
        elExpression: null,
        handlerCode: 'UserRegisterHandler',
        isActive: true,
        createUserId: 'admin',
        createTime: now,
        updateUserId: 'admin',
        updatedTime: now
      },
      {
        flowId: 'SIMPLE_CHECK_FLOW',
        flowName: '简单格式校验 (纯编排模式)',
        businessTypes: 'BASIC_DATA',
        modeType: 'FLOW',
        elExpression: 'THEN(parse_param, check_format)',
        handlerCode: null,
        isActive: true,
        createUserId: 'admin',
        createTime: now,
        updateUserId: 'admin',
        updatedTime: now
      },
      {
        flowId: 'PRODUCT_CREATE_FLOW',
        flowName: '商品创建校验 (混合模式)',
        businessTypes: 'PRODUCT_CREATE',
        modeType: 'HYBRID',
        elExpression: 'THEN(parse_param, check_format)',
        handlerCode: 'ProductBusinessHandler',
        isActive: true,
        createUserId: 'admin',
        createTime: now,
        updateUserId: 'admin',
        updatedTime: now
      },
      {
        flowId: 'PAYMENT_VALIDATION_FLOW',
        flowName: '支付校验 (纯接口模式)',
        businessTypes: 'PAYMENT',
        modeType: 'HANDLER',
        elExpression: null,
        handlerCode: 'PaymentValidationHandler',
        isActive: true,
        createUserId: 'admin',
        createTime: now,
        updateUserId: 'admin',
        updatedTime: now
      }
    ]
    total.value = flows.value.length
  }
}

// 处理查询
const handleSearch = () => {
  currentPage.value = 1
  loadFlows()
}

// 重置查询
const resetSearch = () => {
  Object.assign(searchForm, {
    flowId: '',
    flowName: '',
    businessType: '',
    modeType: '',
    isActive: null
  })
  currentPage.value = 1
  loadFlows()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadFlows()
}

// 处理页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current
  loadFlows()
}

// 新增配置
const handleAdd = () => {
  dialogTitle.value = '新增流程配置'
  Object.assign(form, {
    flowId: '',
    flowName: '',
    businessTypes: [],
    modeType: 'FLOW',
    elExpression: '',
    handlerCode: '',
    componentConfigStr: '{}',
    isActive: true
  })
  dialogVisible.value = true
}

// 编辑配置
const handleEdit = (row) => {
  dialogTitle.value = '编辑流程配置'
  Object.assign(form, {
    flowId: row.flowId,
    flowName: row.flowName,
    businessTypes: row.businessTypes ? row.businessTypes.split(',') : [],
    modeType: row.modeType,
    elExpression: row.elExpression || '',
    handlerCode: row.handlerCode || '',
    componentConfigStr: JSON.stringify(row.componentConfig || {}, null, 2),
    isActive: row.isActive
  })
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const flowData = {
          ...form,
          businessTypes: form.businessTypes.join(','),
          componentConfig: JSON.parse(form.componentConfigStr),
          createUserId: 'admin', // 实际项目中应该从登录用户获取
          updateUserId: 'admin' // 实际项目中应该从登录用户获取
        }
        await configApi.saveFlow(flowData)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadFlows()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}

// 禁用流程
const handleDeactivate = async (flowId) => {
  try {
    await configApi.deactivateFlow(flowId)
    ElMessage.success('禁用成功')
    loadFlows()
  } catch (error) {
    console.error('禁用失败:', error)
    ElMessage.error('禁用失败')
  }
}

// 启用流程
const handleActivate = async (flowId) => {
  try {
    await configApi.activateFlow(flowId)
    ElMessage.success('启用成功')
    loadFlows()
  } catch (error) {
    console.error('启用失败:', error)
    ElMessage.error('启用失败')
  }
}

// 刷新流程
const handleRefresh = async (flowId) => {
  try {
    await configApi.refreshFlow(flowId)
    ElMessage.success('刷新成功')
  } catch (error) {
    console.error('刷新失败:', error)
    ElMessage.error('刷新失败')
  }
}

// 状态变更
const handleStatusChange = async (row) => {
  try {
    const flowData = {
      ...row,
      componentConfig: row.componentConfig || {},
      updateUserId: 'admin' // 实际项目中应该从登录用户获取
    }
    await configApi.saveFlow(flowData)
    ElMessage.success('状态更新成功')
  } catch (error) {
    console.error('状态更新失败:', error)
    ElMessage.error('状态更新失败')
    loadFlows() // 恢复原状态
  }
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedFlows.value = selection
}

// 批量启用
const handleBatchActivate = async () => {
  if (selectedFlows.value.length === 0) {
    ElMessage.warning('请选择要启用的流程')
    return
  }
  try {
    const flowIds = selectedFlows.value.map(flow => flow.flowId)
    await configApi.batchActivateFlows(flowIds)
    ElMessage.success('批量启用成功')
    loadFlows()
    selectedFlows.value = []
  } catch (error) {
    console.error('批量启用失败:', error)
    ElMessage.error('批量启用失败')
  }
}

// 批量禁用
const handleBatchDeactivate = async () => {
  if (selectedFlows.value.length === 0) {
    ElMessage.warning('请选择要禁用的流程')
    return
  }
  try {
    const flowIds = selectedFlows.value.map(flow => flow.flowId)
    await configApi.batchDeactivateFlows(flowIds)
    ElMessage.success('批量禁用成功')
    loadFlows()
    selectedFlows.value = []
  } catch (error) {
    console.error('批量禁用失败:', error)
    ElMessage.error('批量禁用失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedFlows.value.length === 0) {
    ElMessage.warning('请选择要删除的流程')
    return
  }
  try {
    const flowIds = selectedFlows.value.map(flow => flow.flowId)
    await configApi.batchDeleteFlows(flowIds)
    ElMessage.success('批量删除成功')
    loadFlows()
    selectedFlows.value = []
  } catch (error) {
    console.error('批量删除失败:', error)
    ElMessage.error('批量删除失败')
  }
}

onMounted(async () => {
  await loadBusinessTypes()
  await loadFlows()
})
</script>

<style scoped>
.flow-config-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flow-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
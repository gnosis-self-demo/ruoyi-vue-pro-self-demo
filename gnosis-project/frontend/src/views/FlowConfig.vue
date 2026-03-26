<template>
  <div class="flow-config-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>流程配置管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增配置
          </el-button>
        </div>
      </template>
      <div class="flow-table">
        <el-table :data="flows" style="width: 100%">
          <el-table-column prop="flowId" label="流程ID" width="180" />
          <el-table-column prop="flowName" label="流程名称" width="200" />
          <el-table-column label="业务类型" width="150">
            <template #default="scope">
              <span>{{ scope.row.businessTypes ? scope.row.businessTypes.split(',').join('、') : '' }}</span>
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
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateUserId" label="更新人ID" width="120" />
          <el-table-column prop="updatedTime" label="更新时间" width="180" />
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
            <el-option label="订单创建" value="ORDER_CREATE" />
            <el-option label="用户注册" value="USER_REGISTER" />
            <el-option label="基础数据录入" value="BASIC_DATA" />
            <el-option label="其他" value="OTHER" />
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

const flows = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增流程配置')
const formRef = ref(null)

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

// 加载流程配置
const loadFlows = async () => {
  try {
    const response = await configApi.getAllFlows()
    flows.value = response.data
    
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
  }
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

onMounted(() => {
  loadFlows()
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
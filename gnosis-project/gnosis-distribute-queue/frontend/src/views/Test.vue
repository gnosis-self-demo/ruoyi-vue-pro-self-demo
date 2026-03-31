<template>
  <div class="test-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>校验测试</span>
        </div>
      </template>
      <div class="test-content">
        <el-form :model="testForm" :rules="testRules" ref="testFormRef" label-width="100px">
          <el-form-item label="业务类型" prop="businessTypes">
            <el-select v-model="testForm.businessTypes" multiple placeholder="请选择业务类型" @change="handleBusinessTypeChange">
              <el-option label="订单创建" value="ORDER_CREATE" />
              <el-option label="用户注册" value="USER_REGISTER" />
              <el-option label="基础数据录入" value="BASIC_DATA" />
              <el-option label="其他" value="OTHER" />
            </el-select>
          </el-form-item>
          <el-form-item label="流程ID" prop="flowId">
            <el-select v-model="testForm.flowId" placeholder="请选择流程ID">
              <el-option
                v-for="flow in filteredFlows"
                :key="flow.flowId"
                :label="flow.flowName"
                :value="flow.flowId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="测试数据" prop="testData">
            <el-input v-model="testForm.testData" type="textarea" rows="5" placeholder="请输入JSON格式的测试数据" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleTest">开始测试</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>

        <!-- 测试结果 -->
        <div v-if="testResult" class="test-result">
          <el-card shadow="hover" style="margin-top: 20px;">
            <template #header>
              <div class="card-header">
                <span>测试结果</span>
              </div>
            </template>
            <div class="result-content">
              <el-alert
                :title="testResult.success ? '测试成功' : '测试失败'"
                :type="testResult.success ? 'success' : 'error'"
                show-icon
              />
              <div v-if="!testResult.success" class="error-message">
                <h4>错误信息：</h4>
                <p>{{ testResult.message }}</p>
              </div>
              <div class="result-details">
                <h4>测试详情：</h4>
                <pre>{{ JSON.stringify(testResult, null, 2) }}</pre>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { configApi } from '../api/configApi'

const flows = ref([])
const testFormRef = ref(null)
const testResult = ref(null)

const testForm = ref({
  businessTypes: [],
  flowId: '',
  testData: '{"orderNo": "ORD1234567890", "amount": 100, "userId": "123"}'
})

const testRules = ref({
  businessTypes: [
    { required: true, message: '请选择业务类型', trigger: 'change' }
  ],
  flowId: [
    { required: true, message: '请选择流程ID', trigger: 'change' }
  ],
  testData: [
    { required: true, message: '请输入测试数据', trigger: 'blur' }
  ]
})

// 根据业务类型过滤流程
const filteredFlows = computed(() => {
  if (!testForm.value.businessTypes || testForm.value.businessTypes.length === 0) {
    return flows.value
  }
  return flows.value.filter(flow => {
    if (!flow.businessTypes) return false
    const flowBusinessTypes = flow.businessTypes.split(',')
    return testForm.value.businessTypes.some(bt => flowBusinessTypes.includes(bt))
  })
})

// 业务类型变化时重置流程ID
const handleBusinessTypeChange = () => {
  testForm.value.flowId = ''
}

// 加载流程配置
const loadFlows = async () => {
  try {
    const response = await configApi.getActiveFlows()
    flows.value = response.data
  } catch (error) {
    console.error('获取流程配置失败:', error)
  }
}

// 开始测试
const handleTest = async () => {
  if (!testFormRef.value) return
  await testFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 这里需要调用测试API，暂时模拟结果
        // 实际项目中应该调用后端提供的测试接口
        const testData = JSON.parse(testForm.value.testData)
        
        // 模拟测试结果
        testResult.value = {
          success: true,
          message: '校验通过',
          businessTypes: testForm.value.businessTypes,
          flowId: testForm.value.flowId,
          testData: testData,
          timestamp: new Date().toISOString()
        }
        
        ElMessage.success('测试成功')
      } catch (error) {
        console.error('测试失败:', error)
        testResult.value = {
          success: false,
          message: error.message || '测试失败',
          businessTypes: testForm.value.businessTypes,
          flowId: testForm.value.flowId,
          timestamp: new Date().toISOString()
        }
        ElMessage.error('测试失败')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (testFormRef.value) {
    testFormRef.value.resetFields()
  }
  testResult.value = null
}

onMounted(() => {
  loadFlows()
})
</script>

<style scoped>
.test-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-content {
  margin-top: 20px;
}

.test-result {
  margin-top: 20px;
}

.result-content {
  margin-top: 10px;
}

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
}

.result-details {
  margin-top: 15px;
}

.result-details pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin-top: 5px;
}
</style>
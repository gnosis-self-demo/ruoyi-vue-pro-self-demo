<template>
  <div class="home-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <div class="overview-content">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="info-card" :body-style="{ padding: '20px' }">
              <div class="info-item">
                <div class="info-label">系统状态</div>
                <div class="info-value">运行中</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="info-card" :body-style="{ padding: '20px' }">
              <div class="info-item">
                <div class="info-label">配置数量</div>
                <div class="info-value">{{ flowCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="info-card" :body-style="{ padding: '20px' }">
              <div class="info-item">
                <div class="info-label">模式类型</div>
                <div class="info-value">FLOW, HANDLER, HYBRID</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>快速操作</span>
        </div>
      </template>
      <div class="quick-actions">
        <el-button type="primary" @click="navigateTo('/flow-config')">
          <el-icon><Setting /></el-icon>
          流程配置管理
        </el-button>
        <el-button type="success" @click="navigateTo('/test')">
          <el-icon><Check /></el-icon>
          校验测试
        </el-button>
      </div>
    </el-card>

    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>系统说明</span>
        </div>
      </template>
      <div class="system-info">
        <h3>参数动态校验系统</h3>
        <p>本系统支持两种校验模式：</p>
        <ul>
          <li><strong>FLOW 模式</strong>：使用 LiteFlow 引擎通过数据库配置的 EL 表达式动态编排校验流程</li>
          <li><strong>HANDLER 模式</strong>：通过实现 IValidationHandler 接口编写高度定制化的校验逻辑</li>
          <li><strong>HYBRID 模式</strong>：先执行 LiteFlow 基础校验，再调用自定义 Handler 进行深度业务校验</li>
        </ul>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Setting, Check } from '@element-plus/icons-vue'
import { configApi } from '../api/configApi'

const router = useRouter()
const flowCount = ref(0)

const navigateTo = (path) => {
  router.push(path)
}

const loadFlowCount = async () => {
  try {
    const response = await configApi.getActiveFlows()
    flowCount.value = response.data.length
  } catch (error) {
    console.error('获取流程配置失败:', error)
  }
}

onMounted(() => {
  loadFlowCount()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-content {
  margin-top: 20px;
}

.info-card {
  height: 100%;
}

.info-item {
  text-align: center;
}

.info-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.info-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.quick-actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.system-info {
  margin-top: 10px;
  line-height: 1.6;
}

.system-info h3 {
  margin-bottom: 10px;
  color: #303133;
}

.system-info ul {
  margin-top: 10px;
  padding-left: 20px;
}

.system-info li {
  margin-bottom: 5px;
}
</style>
<template>
  <div class="business-type-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>业务类型管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增业务类型
          </el-button>
        </div>
      </template>
      <div class="business-type-table">
        <el-table :data="businessTypes" style="width: 100%">
          <el-table-column prop="code" label="业务类型编码" width="180" />
          <el-table-column prop="name" label="业务类型名称" width="200" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="createUserId" label="创建人ID" width="120" />
          <el-table-column label="创建时间" width="180">
            <template #default="scope">
              <span>{{ formatDateTime(scope.row.createdTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="updateUserId" label="更新人ID" width="120" />
          <el-table-column label="更新时间" width="180">
            <template #default="scope">
              <span>{{ formatDateTime(scope.row.updatedTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row.code)">
                删除
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
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业务类型编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入业务类型编码" />
        </el-form-item>
        <el-form-item label="业务类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入业务类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
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
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { configApi } from '../api/configApi'

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

const businessTypes = ref([
  { code: 'ORDER_CREATE', name: '订单创建', description: '订单创建相关的参数校验', createdTime: '2026-03-26 10:00:00' },
  { code: 'USER_REGISTER', name: '用户注册', description: '用户注册相关的参数校验', createdTime: '2026-03-26 10:00:00' },
  { code: 'BASIC_DATA', name: '基础数据录入', description: '基础数据录入相关的参数校验', createdTime: '2026-03-26 10:00:00' },
  { code: 'OTHER', name: '其他', description: '其他业务类型的参数校验', createdTime: '2026-03-26 10:00:00' }
])

const dialogVisible = ref(false)
const dialogTitle = ref('新增业务类型')
const formRef = ref(null)

const form = reactive({
  code: '',
  name: '',
  description: ''
})

const rules = reactive({
  code: [
    { required: true, message: '请输入业务类型编码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入业务类型名称', trigger: 'blur' }
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
      { code: 'ORDER_CREATE', name: '订单创建', description: '订单创建相关的参数校验', createdTime: '2026-03-26 10:00:00' },
      { code: 'USER_REGISTER', name: '用户注册', description: '用户注册相关的参数校验', createdTime: '2026-03-26 10:00:00' },
      { code: 'BASIC_DATA', name: '基础数据录入', description: '基础数据录入相关的参数校验', createdTime: '2026-03-26 10:00:00' },
      { code: 'OTHER', name: '其他', description: '其他业务类型的参数校验', createdTime: '2026-03-26 10:00:00' }
    ]
  }
}

// 新增业务类型
const handleAdd = () => {
  dialogTitle.value = '新增业务类型'
  Object.assign(form, {
    code: '',
    name: '',
    description: ''
  })
  dialogVisible.value = true
}

// 编辑业务类型
const handleEdit = (row) => {
  dialogTitle.value = '编辑业务类型'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const businessTypeData = {
          ...form,
          createUserId: 'admin', // 实际项目中应该从登录用户获取
          updateUserId: 'admin' // 实际项目中应该从登录用户获取
        }
        await configApi.saveBusinessType(businessTypeData)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadBusinessTypes()
      } catch (error) {
        console.error('保存失败:', error)
        ElMessage.error('保存失败')
      }
    }
  })
}

// 删除业务类型
const handleDelete = (code) => {
  ElMessageBox.confirm('确定要删除这个业务类型吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await configApi.deleteBusinessType(code)
      ElMessage.success('删除成功')
      loadBusinessTypes()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  loadBusinessTypes()
})
</script>

<style scoped>
.business-type-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.business-type-table {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
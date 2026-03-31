<template>
  <div class="app-container">
    <el-container style="height: 100vh">
      <el-aside width="200px" style="background-color: #f0f2f5;">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/flow-config">
            <el-icon><Setting /></el-icon>
            <span>流程配置管理</span>
          </el-menu-item>
          <el-menu-item index="/business-type">
            <el-icon><Collection /></el-icon>
            <span>业务类型管理</span>
          </el-menu-item>
          <el-menu-item index="/test">
            <el-icon><Check /></el-icon>
            <span>校验测试</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="background-color: #fff; border-bottom: 1px solid #e4e7ed; padding: 0 20px;">
          <div style="display: flex; justify-content: space-between; align-items: center; height: 60px;">
            <h1 style="margin: 0; font-size: 18px; color: #303133;">参数校验管理系统</h1>
            <div>
              <el-button type="primary" size="small">退出</el-button>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, Setting, Check, Collection } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)

const handleMenuSelect = (key) => {
  router.push(key)
}
</script>

<style>
.app-container {
  width: 100%;
  height: 100vh;
}

.el-menu {
  height: 100%;
  border-right: none;
}
</style>
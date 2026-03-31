import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/flow-config',
    name: 'FlowConfig',
    component: () => import('../views/FlowConfig.vue')
  },
  {
    path: '/business-type',
    name: 'BusinessType',
    component: () => import('../views/BusinessType.vue')
  },
  {
    path: '/test',
    name: 'Test',
    component: () => import('../views/Test.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/connect',
  },
  {
    path: '/connect',
    name: 'connect',
    component: () => import('@/views/connect/connectViews.vue'),
  },
  {
    path: '/cmd',
    name: 'cmd',
    component: () => import('@/views/cmd/cmdViews.vue'),
  },
  {
    path: '/memory',
    name: 'memory',
    component: () => import('@/views/memory/memoryView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router

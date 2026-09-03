<template>
  <div class="service-view">
    <div class="service-header">
      <h3>服务管理</h3>
      <el-button size="small" @click="refresh">刷新</el-button>
    </div>
    <div class="service-table">
      <el-table :data="tableData" style="width: 100%" border stripe :height="tableHeight">
        <el-table-column prop="unit" label="单元" width="220" show-overflow-tooltip fixed />
        <el-table-column prop="load" label="加载" width="90" />
        <el-table-column prop="active" label="状态" width="90" />
        <el-table-column prop="sub" label="子状态" width="110" />
        <el-table-column prop="description" label="描述" min-width="260" show-overflow-tooltip />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import '@/views/system/view.css'
import http, { API } from '@/api/http'

interface ServiceInfo {
  unit: string
  load: string
  active: string
  sub: string
  description: string
}

const tableData = ref<ServiceInfo[]>([])
const tableHeight = ref(window.innerHeight - 150)

const fetchServiceData = async () => {
  try {
    const response = await http.get(API.system)
    const body = response.data
    console.log('服务接口响应:', body)
    if (body && body.code === 200 && Array.isArray(body.data)) {
      tableData.value = body.data
    } else {
      ElMessage.error(body?.message || '查询服务信息失败')
    }
  } catch (error) {
    console.error('Error fetching service data:', error)
    ElMessage.error('查询服务信息失败')
  }
}

const updateHeight = () => {
  tableHeight.value = window.innerHeight - 140
}

const onResize = () => {
  updateHeight()
}

onMounted(() => {
  window.addEventListener('resize', onResize)
  fetchServiceData()
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
})

const refresh = () => {
  fetchServiceData()
}
</script>

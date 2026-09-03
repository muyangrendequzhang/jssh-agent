<template>
  <div class="process-view">
    <div class="process-header">
      <h3>进程管理</h3>
      <el-button size="small" @click="refresh">刷新</el-button>
    </div>

    <el-table :data="tableData" style="width: 100%" border stripe :height="tableHeight">
      <el-table-column prop="pid" label="PID" width="90" />
      <el-table-column prop="parentPid" label="PPID" width="90" />
      <el-table-column prop="userName" label="用户" width="120" />
      <el-table-column prop="name" label="进程名" min-width="180" show-overflow-tooltip />
      <el-table-column prop="cpuUsage" label="CPU%" width="90" />
      <el-table-column prop="memoryUsage" label="内存%" width="90" />
      <el-table-column fixed="right" label="操作" width="110">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="showDetail(row)">Detail</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailVisible" title="进程详情" width="520px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="PID">{{ current.pid }}</el-descriptions-item>
        <el-descriptions-item label="父进程 PID">{{ current.parentPid }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ current.userName }}</el-descriptions-item>
        <el-descriptions-item label="进程名">{{ current.name }}</el-descriptions-item>
        <el-descriptions-item label="完整命令行">{{ current.commandLine }}</el-descriptions-item>
        <el-descriptions-item label="启动时间">{{ current.startTime }}</el-descriptions-item>
        <el-descriptions-item label="CPU 使用率">{{ current.cpuUsage }}%</el-descriptions-item>
        <el-descriptions-item label="内存使用率">{{ current.memoryUsage }}%</el-descriptions-item>
        <el-descriptions-item label="虚拟内存">{{ formatKB(current.virtualMemory) }}</el-descriptions-item>
        <el-descriptions-item label="物理内存 RSS">{{ formatKB(current.residentMemory) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ current.status }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ current.priority }}</el-descriptions-item>
        <el-descriptions-item label="Nice 值">{{ current.niceValue }}</el-descriptions-item>
        <el-descriptions-item label="终端">{{ current.terminal }}</el-descriptions-item>
        <el-descriptions-item label="CPU 时间">{{ current.cpuTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import '@/views/process/views.css'
import http, { API } from '@/api/http'

interface ProcessInfo {
  pid: number
  parentPid: number
  userName: string
  name: string
  commandLine: string
  startTime: string
  cpuUsage: number
  memoryUsage: number
  virtualMemory: number
  residentMemory: number
  status: string
  priority: number
  niceValue: number
  terminal: string
  cpuTime: string
}

const tableData = ref<ProcessInfo[]>([])
const tableHeight = ref(600)
const detailVisible = ref(false)
const current = ref<ProcessInfo>({} as ProcessInfo)

const fetchProcess = async () => {
  try {
    const res = await http.get(API.process)
    const body = res.data
    console.log('进程接口响应:', body)
    if (body && body.code === 200 && Array.isArray(body.data)) {
      console.log('进程数据条数:', body.data.length, '首条:', body.data[0])
      tableData.value = body.data
    } else {
      ElMessage.error(body?.message || '查询进程失败')
    }
  } catch (error) {
    console.error('加载进程失败:', error)
    ElMessage.error('加载进程失败')
  }
}

const refresh = () => {
  fetchProcess()
}

const showDetail = (row: ProcessInfo) => {
  current.value = row
  detailVisible.value = true
}

const formatKB = (kb?: number): string => {
  const v = Number(kb) || 0
  if (v >= 1024 * 1024) return (v / 1024 / 1024).toFixed(2) + ' GB'
  if (v >= 1024) return (v / 1024).toFixed(1) + ' MB'
  return v + ' KB'
}

const updateHeight = () => {
  tableHeight.value = window.innerHeight - 140
}

onMounted(() => {
  updateHeight()
  fetchProcess()
})

onUnmounted(() => {
  window.removeEventListener('resize', updateHeight)
})

window.addEventListener('resize', updateHeight)
</script>

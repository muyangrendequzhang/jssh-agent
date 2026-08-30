<template>
  <div class="memory-monitor">
    <div class="chart-header">
      <h3>内存占用实时监控</h3>
      <div class="controls">
        <el-button size="small" @click="refreshOnce">刷新一次</el-button>
        <el-button size="small" type="primary" @click="startMonitoring">开始监控</el-button>
        <el-button size="small" @click="stopMonitoring">停止</el-button>
        <el-button size="small" @click="clearData">清空</el-button>
      </div>
    </div>

    <div ref="chartRef" class="chart-container"></div>

    <div class="stats">
      <span>当前占用: <strong>{{ currentUsed }}%</strong></span>
      <span>平均值: <strong>{{ average }}%</strong></span>
      <span>最大值: <strong>{{ max }}%</strong></span>
      <span>数据点: <strong>{{ dataCount }}</strong></span>
    </div>

    <el-collapse v-model="activeNames" class="detail-collapse">
      <el-collapse-item title="总量统计" name="1">
        <div class="kv"><span>总物理内存 (MemTotal)</span><b>{{ fmtKB(info.totalMemory) }}</b></div>
        <div class="kv"><span>总交换分区 (SwapTotal)</span><b>{{ fmtKB(info.totalSwap) }}</b></div>
      </el-collapse-item>

      <el-collapse-item title="使用 / 空闲分布" name="2">
        <div class="kv"><span>已用内存 (usedMemory)</span><b>{{ fmtKB(info.usedMemory) }}</b></div>
        <div class="kv"><span>空闲物理内存 (MemFree)</span><b>{{ fmtKB(info.freeMemory) }}</b></div>
        <div class="kv"><span>可用内存 (MemAvailable)</span><b>{{ fmtKB(info.availableMemory) }}</b></div>
        <div class="kv"><span>内存使用率 (usagePercent)</span><b>{{ info.usagePercent }}%</b></div>
        <div class="kv"><span>已用交换分区 (usedSwap)</span><b>{{ fmtKB(info.usedSwap) }}</b></div>
        <div class="kv"><span>空闲交换分区 (SwapFree)</span><b>{{ fmtKB(info.freeSwap) }}</b></div>
        <div class="kv"><span>交换分区使用率</span><b>{{ info.swapUsagePercent }}%</b></div>
      </el-collapse-item>

      <el-collapse-item title="内核与缓存细节" name="3">
        <div class="kv"><span>缓冲 (Buffers)</span><b>{{ fmtKB(info.bufferedMemory) }}</b></div>
        <div class="kv"><span>缓存 (Cached)</span><b>{{ fmtKB(info.cachedMemory) }}</b></div>
        <div class="kv"><span>页表占用 (PageTables)</span><b>{{ fmtKB(info.pageTables) }}</b></div>
        <div class="kv"><span>内核slab缓存 (Slab)</span><b>{{ fmtKB(info.slab) }}</b></div>
        <div class="kv"><span>内核栈 (KernelStack)</span><b>{{ fmtKB(info.kernelStack) }}</b></div>
      </el-collapse-item>

      <el-collapse-item title="系统信息" name="4">
        <div class="kv"><span>主机名</span><b>{{ info.hostname || '-' }}</b></div>
        <div class="kv"><span>采集时间</span><b>{{ formatTime(info.timestamp) }}</b></div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import type { CollapseModelValue } from 'element-plus'

interface MemoryInfo {
  totalMemory: number
  totalSwap: number
  freeMemory: number
  freeSwap: number
  availableMemory: number
  bufferedMemory: number
  cachedMemory: number
  pageTables: number
  slab: number
  kernelStack: number
  usedMemory: number
  usagePercent: number
  usedSwap: number
  swapUsagePercent: number
  hostname: string
  timestamp: number
}

const MEMORY_API = 'http://localhost:8080/memory'

const activeNames = ref<string[]>(['1', '2'])
const handleChange = (_val: CollapseModelValue) => {
  // 预留：折叠面板变化处理
}

const info = ref<MemoryInfo>({
  totalMemory: 0, totalSwap: 0, freeMemory: 0, freeSwap: 0, availableMemory: 0,
  bufferedMemory: 0, cachedMemory: 0, pageTables: 0, slab: 0, kernelStack: 0,
  usedMemory: 0, usagePercent: 0, usedSwap: 0, swapUsagePercent: 0,
  hostname: '', timestamp: 0,
})

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null
let timer: number | null = null

const timeData = ref<string[]>([])
const memoryData = ref<number[]>([])
const currentUsed = ref(0)
const average = ref(0)
const max = ref(0)
const dataCount = ref(0)
const MAX_POINTS = 60

const fmtKB = (kb: number): string => {
  const v = Number(kb) || 0
  if (v >= 1024 * 1024) return (v / 1024 / 1024).toFixed(2) + ' GB'
  if (v >= 1024) return (v / 1024).toFixed(1) + ' MB'
  return v + ' KB'
}

const formatTime = (ts: number): string => {
  return ts ? new Date(ts).toLocaleString() : '-'
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)

  chartInstance.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: timeData.value,
      axisLabel: { color: '#606266' },
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100,
      axisLabel: { formatter: '{value}%' },
    },
    series: [
      {
        name: '内存占用',
        type: 'line',
        smooth: true,
        symbol: 'none',
        lineStyle: { width: 3, color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.01)' },
          ]),
        },
        data: memoryData.value,
      },
    ],
    visualMap: {
      show: false,
      dimension: 0,
      pieces: [
        { lte: 70, color: '#67C23A' },
        { gt: 70, lte: 85, color: '#E6A23C' },
        { gt: 85, color: '#F56C6C' },
      ],
    },
  })

  window.addEventListener('resize', onResize)
}

const onResize = () => {
  chartInstance?.resize()
}

const fetchMemoryData = async () => {
  try {
    const res = await fetch(MEMORY_API)
    const body = await res.json()
    if (!body || body.code !== 200 || !body.data) {
      return
    }
    const data = body.data as MemoryInfo
    info.value = data

    currentUsed.value = Math.round(data.usagePercent * 10) / 10

    const now = new Date()
    const timeStr = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`
    timeData.value.push(timeStr)
    memoryData.value.push(currentUsed.value)

    if (timeData.value.length > MAX_POINTS) {
      timeData.value.shift()
      memoryData.value.shift()
    }

    dataCount.value = memoryData.value.length
    max.value = Math.round(Math.max(...memoryData.value) * 10) / 10
    average.value = Math.round(
      (memoryData.value.reduce((a, b) => a + b, 0) / memoryData.value.length) * 10,
    ) / 10

    updateChart()
  } catch (error) {
    console.error('获取内存数据失败:', error)
  }
}

const updateChart = () => {
  if (!chartInstance) return
  chartInstance.setOption({
    xAxis: { data: timeData.value },
    series: [{ data: memoryData.value }],
  })
}

const startMonitoring = () => {
  if (timer) return
  fetchMemoryData()
  timer = window.setInterval(fetchMemoryData, 1000)
  ElMessage.success('开始监控')
}

const stopMonitoring = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
    ElMessage.success('已停止监控')
  }
}

const refreshOnce = () => {
  fetchMemoryData()
}

const clearData = () => {
  timeData.value = []
  memoryData.value = []
  currentUsed.value = 0
  average.value = 0
  max.value = 0
  dataCount.value = 0
  updateChart()
  ElMessage.success('已清空数据')
}

onMounted(() => {
  initChart()
  startMonitoring()
})

onUnmounted(() => {
  stopMonitoring()
  window.removeEventListener('resize', onResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.memory-monitor {
  padding: 20px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  margin: 0;
  color: #303133;
}

.controls {
  display: flex;
  gap: 8px;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.stats {
  display: flex;
  gap: 30px;
  margin: 16px 0;
  padding: 12px 20px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stats span {
  font-size: 14px;
  color: #606266;
}

.stats strong {
  color: #303133;
  font-size: 16px;
}

.detail-collapse {
  margin-top: 8px;
}

.kv {
  display: flex;
  justify-content: space-between;
  padding: 8px 4px;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
  color: #606266;
}

.kv b {
  color: #303133;
}
</style>

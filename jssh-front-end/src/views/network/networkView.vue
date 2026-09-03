<template>
  <div class="network-view">
    <h3>网络管理</h3>
    <el-button size="small" @click="refresh">刷新</el-button>
  </div>

  <div class="network-table">
    <el-table :data="tableDate" style="width: 100%" border stripe :height="tableHeight">
      <el-table-column prop="interfaceName" label="接口" width="110" fixed />
      <el-table-column prop="interfaceType" label="类型" width="100" />
      <el-table-column prop="flags" label="状态" min-width="160" show-overflow-tooltip />
      <el-table-column prop="mtu" label="MTU" width="80" />
      <el-table-column prop="txQueueLength" label="发送队列" width="90" />
      <el-table-column prop="macAddress" label="MAC" width="150" />
      <el-table-column prop="ipv4Address" label="IPv4" width="140" />
      <el-table-column prop="ipv4Netmask" label="掩码" width="130" />
      <el-table-column prop="ipv4Broadcast" label="广播" width="140" />
      <el-table-column prop="ipv6Addresses" label="IPv6" min-width="200" show-overflow-tooltip />
      <el-table-column prop="ipv6PrefixLengths" label="IPv6前缀" width="100" />
      <el-table-column prop="rxPackets" label="收包数" width="100" />
      <el-table-column prop="rxBytes" label="收字节" width="110" />
      <el-table-column prop="txPackets" label="发包数" width="100" />
      <el-table-column prop="txBytes" label="发字节" width="110" />
      <el-table-column prop="rxErrors" label="收错误" width="90" />
      <el-table-column prop="txErrors" label="发错误" width="90" />
      <el-table-column prop="rxDropped" label="收丢包" width="90" />
      <el-table-column prop="txDropped" label="发丢包" width="90" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import '@/views/network/view.css'
import http, { API } from '@/api/http'

interface NetworkInfo {
  interfaceName: string
  flags: string
  mtu: number
  interfaceType: string
  txQueueLength: number
  macAddress: string
  ipv4Address: string
  ipv4Netmask: string
  ipv4Broadcast: string
  ipv6Addresses: string
  ipv6PrefixLengths: string
  ipv6ScopeIds: string
  rxPackets: number
  rxBytes: number
  rxErrors: number
  rxDropped: number
  rxOverruns: number
  rxFrameErrors: number
  txPackets: number
  txBytes: number
  txErrors: number
  txDropped: number
  txOverruns: number
  txCarrierLosses: number
  txCollisions: number
}

const tableHeight = ref<number>(window.innerHeight - 140)
const tableDate = ref<NetworkInfo[]>([])

const fetchNetworkData = async () => {
  try {
    const response = await http.get(API.network)
    console.log('网络接口响应:', response.data)
    if (response.data && response.data.code === 200 && Array.isArray(response.data.data)) {
      tableDate.value = response.data.data
      console.log('网卡条数:', response.data.data.length, '首条:', response.data.data[0])
    } else {
      ElMessage.error(response.data?.message || '查询网络信息失败')
    }
  } catch (error) {
    console.error('获取网络信息失败:', error)
    ElMessage.error('查询网络信息失败')
  }
}

const refresh = () => {
  fetchNetworkData()
}

const updateHeight = () => {
  tableHeight.value = window.innerHeight - 140
}

const onResize = () => {
  updateHeight()
}

onMounted(() => {
  fetchNetworkData()
  updateHeight()
  window.addEventListener('resize', onResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', onResize)
})
</script>

<template>
  <div class="connection-page">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="connectName" label="Connect Name" width="180" />
      <el-table-column prop="host" label="Host" width="180" />
      <el-table-column prop="port" label="Port" width="180" />
      <el-table-column prop="user" label="User" width="180" />
      <el-table-column prop="password" label="Password" width="180" />
      <el-table-column prop="privateKeyPath" label="Private Key Path" width="180" />
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElTable, ElTableColumn } from 'element-plus'
import 'element-plus/dist/index.css'
import './view.css'

interface ConnectionInfo {
  connectName: string
  host: string
  port: number
  user: string
  password: string
  privateKeyPath: string
}
const tableData = ref<ConnectionInfo[]>([])

const fetchData = async () => {
  try {
    const response = await fetch('http://localhost:8080/connection')
    const result = await response.json()
    if (result.code === 200) {
      tableData.value = result.data
    }
  } catch (error) {
    console.error('Error fetching data:', error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

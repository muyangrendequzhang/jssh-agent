<template>
  <div class="connection-page">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column prop="connectName" label="Connect Name" width="180" />
      <el-table-column prop="host" label="Host" width="180" />
      <el-table-column prop="port" label="Port" width="180" />
      <el-table-column prop="user" label="User" width="180" />
      <el-table-column prop="password" label="Password" width="180" />
      <el-table-column prop="privateKeyPath" label="Private Key Path" width="180" />
      <el-table-column label="Operations" width="180">
        <template #default="scope">
          <el-button
            size="small"
            type="danger"
            @click="connect(scope.$index, scope.row as ConnectionInfo)"
          >
            连接
          </el-button>
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.$index, scope.row as ConnectionInfo)"
          >
            Delete
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElTable, ElTableColumn, ElMessage, ElMessageBox } from 'element-plus'
import 'element-plus/dist/index.css'
import './view.css'
import { useRouter } from 'vue-router'

const router = useRouter()

type ConnectionInfo = {
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

const connect = async (index: number, row: ConnectionInfo) => {
  try {
    const response = await fetch('http://localhost:8080/base/connect', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(row),
    })
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success(result.message || '连接成功')
      router.push('/cmd')
    } else {
      ElMessage.error(result.message || '连接失败')
    }
  } catch (error) {
    console.error('Error connecting to server:', error)
    ElMessage.error('连接失败')
  }
}

const handleDelete = async (index: number, row: ConnectionInfo) => {
  try {
    // ✅ 加上 await，等待用户确认
    await ElMessageBox.confirm('确定要删除该连接吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    // ✅ 用户点击「确定」后才会执行到这里
    const response = await fetch(`http://localhost:8080/connection/${encodeURIComponent(row.connectName)}`, {
      method: 'DELETE',
    })
    const result = await response.json()

    if (result.code === 200) {
      ElMessage.success(result.message || '删除成功')
      tableData.value.splice(index, 1) // 从列表中移除
    } else {
      ElMessage.error(result.message || '删除失败')
    }
  } catch (error) {
    // ✅ 用户点击「取消」或发生网络错误都会进到这里
    // 如果是用户取消，不显示错误提示
    if (error !== 'cancel') {
      console.error('Error deleting connection:', error)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchData()
})
</script>

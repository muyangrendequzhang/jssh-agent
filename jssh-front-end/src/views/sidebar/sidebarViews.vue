<template>
  <el-aside width="220px">
    <el-scrollbar>
      <el-menu :default-active="route.path" router>
        <el-menu-item index="/cmd">
          <el-icon><Monitor /></el-icon>
          <span>终端交互</span>
        </el-menu-item>

        <el-menu-item index="/memory">
          <el-icon><Odometer /></el-icon>
          <span>系统监控（内存/CPU/网络）</span>
        </el-menu-item>

        <el-menu-item index="/process">
          <el-icon><Management /></el-icon>
          <span>进程管理</span>
        </el-menu-item>

        <el-menu-item index="/files">
          <el-icon><Folder /></el-icon>
          <span>文件管理</span>
        </el-menu-item>

        <el-menu-item index="/network">
          <el-icon><Connection /></el-icon>
          <span>网络管理</span>
        </el-menu-item>

        <el-menu-item index="/system">
          <el-icon><Setting /></el-icon>
          <span>服务管理（systemctl）</span>
        </el-menu-item>

        <el-menu-item index="/connection">
          <el-icon><Connection /></el-icon>
          <span>连接管理</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import { Monitor, Odometer, Management, Folder, Connection, Setting } from '@element-plus/icons-vue'
import '@/views/sidebar/views.css'
import { el } from 'element-plus/es/locales.mjs'

const route = useRoute()
</script>

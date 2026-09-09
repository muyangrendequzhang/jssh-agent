<template>
  <el-container style="height: 100vh">
    <sidebarViews />
    <el-main>
      <router-view v-slot="{ Component }">
        <keep-alive :include="keepAliveRoutes">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import sidebarViews from '@/views/sidebar/sidebarViews.vue'

// 需要长期运行（缓存不销毁）的页面路由 name
const keepAliveRoutes = ['chat', 'cmd']
</script>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
}

#app .el-container {
  height: 100%;
}

#app .el-main {
  padding: 0;
  overflow: auto;
}
</style>

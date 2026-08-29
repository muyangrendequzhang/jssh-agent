<template>
  <el-container style="height: 100vh">
    <sidebarViews />
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import sidebarViews from '@/views/sidebar/sidebarViews.vue'
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
  overflow: hidden;
}
</style>

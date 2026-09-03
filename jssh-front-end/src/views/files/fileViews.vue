<template>
  <div class="file-view">
    <el-tree lazy :props="props" :load="loadNode" :node-key="'path'" class="file-tree" />
  </div>
</template>

<script setup lang="ts">
import type { LoadFunction } from 'element-plus'
import '@/views/files/views.css'
import http, { API } from '@/api/http'

interface FileNode {
  name: string
  path: string
}

const props = {
  label: 'name',
  isLeaf: 'leaf',
}

const loadNode: LoadFunction = async (node, resolve) => {
  const path = node.level === 0 ? '/' : node.data.path
  try {
    const res = await http.get(API.file, { params: { path } })
    const body = res.data
    if (body && body.code === 200 && body.data) {
      const children: FileNode[] = (body.data.childrenFiles || []).map((child: FileNode) => ({
        name: child.name,
        path: child.path,
      }))
      resolve(children)
    } else {
      resolve([])
    }
  } catch (error) {
    console.error('加载文件列表失败:', error)
    resolve([])
  }
}
</script>

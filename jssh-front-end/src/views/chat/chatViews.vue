<template>
  <div class="chat-page">
    <div class="chat-container">
      <div class="chat-header">
        <span class="chat-title">AI 助手</span>
        <el-button size="small" text type="primary" @click="clearMessages">清空会话</el-button>
      </div>

      <div ref="messageListRef" class="message-list">
        <div v-for="(item, index) in messages" :key="index" class="message-row" :class="item.role">
          <div class="message-bubble">
            <div v-if="item.role === 'assistant' && item.toolEvents.length" class="tool-events">
              <div v-for="(evt, i) in item.toolEvents" :key="i" class="tool-event">
                <el-icon><Tools /></el-icon>
                <span>{{ evt }}</span>
              </div>
            </div>
            <span class="message-text">{{ item.content }}</span>
          </div>
        </div>
        <div v-if="loading" class="message-row assistant">
          <div class="message-bubble">
            <el-icon class="is-loading"><Loading /></el-icon>
          </div>
        </div>
      </div>

      <div class="input-area">
        <el-input
          v-model="input"
          type="textarea"
          :rows="2"
          resize="none"
          placeholder="输入你的问题，回车发送"
          :disabled="loading"
          @keydown.enter.exact.prevent="send"
        />
        <el-button type="primary" :loading="loading" :disabled="!input.trim()" @click="send">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onBeforeUnmount } from 'vue'
import { Loading, Tools } from '@element-plus/icons-vue'
import './chat.css'
import { chatStream } from '@/api/agent'

defineOptions({ name: 'chat' })

type ToolEvent = {
  name: string
  responseData: string
}

type ChatMessage = {
  role: 'user' | 'assistant'
  content: string
  toolEvents: string[]
}

const messages = ref<ChatMessage[]>([])
const input = ref('')
const loading = ref(false)
const messageListRef = ref<HTMLElement>()
let abortController: AbortController | null = null

const scrollToBottom = async () => {
  await nextTick()
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const send = async () => {
  const text = input.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text, toolEvents: [] })
  const assistantMsg: ChatMessage = { role: 'assistant', content: '', toolEvents: [] }
  messages.value.push(assistantMsg)
  input.value = ''
  loading.value = true
  abortController = new AbortController()

  try {
    await chatStream(
      text,
      (data: any) => {
        const type = data?.messageType
        if (type === 'ASSISTANT' && typeof data.text === 'string') {
          assistantMsg.content += data.text
          scrollToBottom()
        } else if (type === 'TOOL' && Array.isArray(data.responses)) {
          for (const resp of data.responses as ToolEvent[]) {
            if (resp.name) {
              assistantMsg.toolEvents.push(`已调用工具：${resp.name}`)
            }
          }
          scrollToBottom()
        }
      },
      abortController.signal,
    )
  } catch (error) {
    if ((error as Error).name !== 'AbortError') {
      assistantMsg.content += '\n[请求失败，请检查 agent 服务是否可用]'
    }
  } finally {
    loading.value = false
    abortController = null
  }
}

const clearMessages = () => {
  messages.value = []
}

onBeforeUnmount(() => {
  abortController?.abort()
})
</script>
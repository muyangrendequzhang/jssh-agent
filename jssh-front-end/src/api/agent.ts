import { API_BASE_URL } from './http'

/** agent 服务地址（流式对话接口在 agent 端，端口 8081） */
export const AGENT_BASE_URL = import.meta.env.VITE_AGENT_BASE_URL || 'http://localhost:8081'

/**
 * 调用 agent 的流式对话接口（POST /chat/stream，SSE）。
 * 通过 fetch 读取 text/event-stream，逐个 SSE 事件回调解析出的 Message JSON。
 */
export async function chatStream(
  message: string,
  onMessage: (data: unknown) => void,
  signal?: AbortSignal,
): Promise<void> {
  const response = await fetch(`${AGENT_BASE_URL}/chat/stream`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message }),
    signal,
  })

  if (!response.ok || !response.body) {
    throw new Error(`chat stream request failed: ${response.status}`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) break

    buffer += decoder.decode(value, { stream: true })

    // SSE 事件以空行分隔，一条事件可能是多行 data:
    const events = buffer.split('\n\n')
    buffer = events.pop() || ''

    for (const event of events) {
      for (const line of event.split('\n')) {
        if (!line.startsWith('data:')) continue
        const payload = line.slice(5).trim()
        if (!payload) continue
        try {
          onMessage(JSON.parse(payload))
        } catch {
          // 忽略无法解析的事件
        }
      }
    }
  }
}
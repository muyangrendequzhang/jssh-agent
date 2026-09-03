<template>
  <div>
    <div ref="terminal" />
  </div>
</template>

<script>
import 'xterm/css/xterm.css'
import { Terminal } from 'xterm'
import { FitAddon } from 'xterm-addon-fit'
import { AttachAddon } from 'xterm-addon-attach'
import { WS_BASE_URL, API } from '@/api/http'

export default {
  name: 'terminal',
  data() {
    return {
      term: null,
      socketUri: WS_BASE_URL + API.wsPod,
      socket: null,
      accessToken: 'token',
    }
  },
  mounted() {
    this.initTerm()
  },
  // Vue 3 用 beforeUnmount，Vue 2 的 beforeDestroy 已失效
  beforeUnmount() {
    this.socket && this.socket.close()
    this.term && this.term.dispose()
  },
  methods: {
    initTerm() {
      // 1.xterm终端初始化
      const term = new Terminal({
        rendererType: 'canvas', //渲染类型
        rows: 40, //行数
        cols: 100, // 不指定行数，自动回车后光标从下一行开始
        convertEol: true, //启用时，光标将设置为下一行的开头
        // scrollback: 50, //终端中的回滚量
        disableStdin: false, //是否应禁用输入
        windowsMode: true, // 根据窗口换行
        cursorStyle: 'underline', //光标样式
        cursorBlink: true, //光标闪烁
        theme: {
          foreground: '#ECECEC', //字体
          background: '#000000', //背景色
          cursor: 'help', //设置光标
          lineHeight: 20,
        },
      })
      // 先 open 终端，再挂载插件（插件需要基于已打开的终端）
      term.open(this.$refs.terminal)
      this.term = term

      // 2.插件：先 FitAddon 自适应，再 AttachAddon 绑定 WebSocket
      const fitAddon = new FitAddon() // 全屏插件
      term.loadAddon(fitAddon)
      fitAddon.fit()
      term.focus()

      // 3.webSocket初始化
      if (this.socketUri === '') return
      // token 走查询参数（后端暂未解析该参数）
      this.socket = new WebSocket(`${this.socketUri}?token=${encodeURIComponent(this.accessToken)}`)
      this.socket.binaryType = 'arraybuffer'
      // 4.绑定数据：socket 与 xterm 双向传输
      term.loadAddon(new AttachAddon(this.socket))
      this.socket.onerror = (e) => {
        console.error('WebSocket 错误:', e)
      }
    },
  },
}
</script>

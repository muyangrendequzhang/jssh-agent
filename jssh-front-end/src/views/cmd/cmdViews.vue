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

export default {
  name: 'terminal',
  data() {
    return {
      term: null,
      socketUri: 'ws://127.0.0.1:8080/podname',
      socket: '',
      accessToken: 'token',
    }
  },
  mounted() {
    this.initTerm()
  },
  beforeDestroy() {
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
      // 2.webSocket初始化
      if (this.socketUri === '') return
      this.socket = new WebSocket(this.socketUri, this.accessToken) // 带 token 发起连接
      // 3.websocket集成的插件,这里要注意,网上写了很多websocket相关代码.xterm4版本没必要.
      const attachAddon = new AttachAddon(this.socket)
      const fitAddon = new FitAddon() // 全屏插件
      term.loadAddon(attachAddon)
      term.loadAddon(fitAddon)
      term.open(this.$refs.terminal)
      fitAddon.fit()
      term.focus()
      this.term = term
    },
  },
}
</script>

import axios from 'axios'

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
})

export const WS_BASE_URL = API_BASE_URL.replace(/^http/, 'ws')

export const API = {
  connect: '/base/connect',
  connection: '/connection',
  uploadKey: '/uploadKey',
  memory: '/memory',
  process: '/process',
  network: '/network',
  system: '/system',
  file: '/file',
  wsPod: '/podname',
}

export default http

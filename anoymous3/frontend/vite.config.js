// frontend/vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { fileURLToPath } from 'url'

// ESM 환경에서 __dirname 대체
const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 환경변수 기반 프록시 설정 (없으면 기본값)
const API_PREFIX = process.env.VITE_API_PREFIX || '/api'
const API_TARGET = process.env.VITE_API_PROXY_TARGET || 'http://localhost:8080'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '@css': path.resolve(__dirname, './src/css'),
    },
  },
  server: {
    port: 3000,
    strictPort: true,   // 포트 점유 시 실패(자동 3001로 안 넘어가게)
    open: true,
    cors: true,
    hmr: { overlay: true },
    proxy: {
      [API_PREFIX]: {
        target: API_TARGET,
        changeOrigin: true,
        // 필요 시 쿠키 도메인/헤더 정리 등의 옵션 추가 가능
        // secure: false,   // https 셀프사인 대상일 때만 사용
        // ws: true,        // 웹소켓 프록시가 필요할 때만
      },
    },
  },
})

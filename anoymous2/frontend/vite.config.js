import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // src 기본
      '@': fileURLToPath(new URL('./src', import.meta.url)),

      // SCSS 경로
      '@css': fileURLToPath(new URL('./src/css', import.meta.url)),
      '@cssAdmin': fileURLToPath(new URL('./src/css/admin', import.meta.url)),
      '@cssBase': fileURLToPath(new URL('./src/css/base', import.meta.url)),
      '@cssCommon': fileURLToPath(new URL('./src/css/common', import.meta.url)),
      '@cssComponent': fileURLToPath(new URL('./src/css/component', import.meta.url)),
      '@cssEvent': fileURLToPath(new URL('./src/css/event', import.meta.url)),
      '@cssLayouts': fileURLToPath(new URL('./src/css/layouts', import.meta.url)),
      '@cssMixin': fileURLToPath(new URL('./src/css/mixin', import.meta.url)),
      '@cssMypage': fileURLToPath(new URL('./src/css/mypage', import.meta.url)),
      '@cssPages': fileURLToPath(new URL('./src/css/pages', import.meta.url)),
      '@cssServices': fileURLToPath(new URL('./src/css/services', import.meta.url)),

      // Vue 폴더
      '@components': fileURLToPath(new URL('./src/components', import.meta.url)),
      '@pages': fileURLToPath(new URL('./src/pages', import.meta.url)),
      '@layouts': fileURLToPath(new URL('./src/layouts', import.meta.url)),
      '@router': fileURLToPath(new URL('./src/router', import.meta.url))
    },
    extensions: ['.js', '.vue', '.json', '.scss']
  },
  server: {
    port: 3000,
    open: true,
    cors: true,
    proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
  }
})
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// SCSS 전역
import '@/css/_Base.scss'
store.dispatch('auth/initFromStorage') // ✅ 앱 시작 시 저장된 토큰/아이디 복원

createApp(App).use(router).use(store).mount('#app')

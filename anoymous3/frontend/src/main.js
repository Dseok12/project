import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// SCSS 전역
import '@/css/_Base.scss'
store.dispatch('initFromStorage')

createApp(App).use(router).use(store).mount('#app')

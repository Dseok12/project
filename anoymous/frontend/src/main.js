import { createApp } from 'vue'
import './_Base.scss';
import router from './router/index.js';
import App from './App.vue';

createApp(App).use(router).mount('#app')

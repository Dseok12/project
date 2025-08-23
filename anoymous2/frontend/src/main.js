import { createApp } from 'vue'
import App from './App.vue';
import './assets/styles/_Base.scss';

import store from './store';      // Vuex store
import router from './router';    // Vue Router

createApp(App)
  .use(store)
  .use(router)
  .mount('#app');

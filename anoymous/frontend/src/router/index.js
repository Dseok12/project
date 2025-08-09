// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

import Home from '../pages/home.vue'
import Join from '../pages/join.vue'
import Login from '../pages/login.vue'

import BoardListView from '../pages/BoardListView.vue'
import BoardPostView from '../pages/BoardPostView.vue'
import BoardDetailView from '../pages/BoardDetailView.vue'
import BoardEditView from '../pages/BoardEditView.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/join', component: Join },
  { path: '/login', component: Login },
  { path: '/board', component: BoardListView },
  { path: '/board/write', component: BoardPostView },
  { path: '/board/:id', component: BoardDetailView, props: true },
  { path: '/board/:id/edit', component: BoardEditView, props: true }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

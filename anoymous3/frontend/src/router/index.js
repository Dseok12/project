import { createRouter, createWebHistory } from 'vue-router'

import Home from '@/pages/Home.vue'
import Notices from '@/pages/Notices.vue'
import Posts from '@/pages/Posts.vue'
import PostDetail from '@/pages/PostDetail.vue'
import NewPost from '@/pages/NewPost.vue'
import EditPost from '@/pages/EditPost.vue'   // ✅ 추가
import Signup from '@/pages/Signup.vue'
import Login from '@/pages/Login.vue'
import MyPage from '@/pages/MyPage.vue'

// ✅ Vuex 스토어 직접 임포트
import store from '@/store'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/notices', name: 'notices', component: Notices },
  { path: '/posts', name: 'posts', component: Posts },
  { path: '/posts/new', name: 'new-post', component: NewPost, meta: { requiresAuth: true } },
  { path: '/posts/:id', name: 'post-detail', component: PostDetail, props: true },
  { path: '/posts/:id/edit', name: 'edit-post', component: EditPost, meta: { requiresAuth: true }, props: true }, // ✅ 추가
  { path: '/signup', name: 'signup', component: Signup, meta: { guestOnly: true } }, // ✅ 로그인 상태 접근 차단
  { path: '/login', name: 'login', component: Login, meta: { guestOnly: true } },   // ✅ 로그인 상태 접근 차단
  { path: '/mypage/:activityId', name: 'mypage', component: MyPage, meta: { requiresAuth: true }, props: true },
  // (선택) 404 처리를 홈으로 보냄
  { path: '/:pathMatch(.*)*', redirect: { name: 'home' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 페이지 이동 시 항상 상단으로
  scrollBehavior() {
    return { left: 0, top: 0 }
  }
})

// ✅ Vuex 기반 네비게이션 가드
router.beforeEach((to) => {
  const authed = store.getters.isAuthed

  // 보호 라우트 접근 시
  if (to.meta.requiresAuth && !authed) {
    return {
      name: 'login',
      query: { redirect: to.fullPath } // 로그인 후 되돌아올 위치
    }
  }

  // 로그인/회원가입은 게스트만 접근
  if (to.meta.guestOnly && authed) {
    return { name: 'home' }
  }

  return true
})

export default router

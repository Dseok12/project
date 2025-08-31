// src/router/index.js
import { createRouter, createWebHistory } from "vue-router"

import Home from "@/pages/Home.vue"
import Notices from "@/pages/Notices.vue"
import Posts from "@/pages/Posts.vue"
import PostDetail from "@/pages/PostDetail.vue"
import NewPost from "@/pages/NewPost.vue"
import EditPost from "@/pages/EditPost.vue"
import Signup from "@/pages/Signup.vue"
import Login from "@/pages/Login.vue"
import MyPage from "@/pages/MyPage.vue"

// ✅ Vuex 스토어 직접 임포트
import store from "@/store"

const routes = [
  { path: "/", name: "home", component: Home },
  { path: "/notices", name: "notices", component: Notices },
  { path: "/posts", name: "posts", component: Posts },
  {
    path: "/posts/new",
    name: "new-post",
    component: NewPost,
    meta: { requiresAuth: true },
  },
  {
    path: "/posts/:id",
    name: "post-detail",
    component: PostDetail,
    props: true,
  },
  {
    path: "/posts/:id/edit",
    name: "edit-post",
    component: EditPost,
    meta: { requiresAuth: true },
    props: true,
  },
  {
    path: "/signup",
    name: "signup",
    component: Signup,
    meta: { guestOnly: true },
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { guestOnly: true },
  },
  {
    path: "/mypage/:activityId",
    name: "mypage",
    component: MyPage,
    meta: { requiresAuth: true },
    props: true,
  },
  // ✅ 관리자 대시보드
  { path: "/admin", name: "admin", component: () => import("@/pages/admin/AdminDashboard.vue"), meta: { admin: true } },

  // 404는 홈으로
  { path: "/:pathMatch(.*)*", redirect: { name: "home" } },

  // 예: 이용약관
  {
    path: "/terms",
    name: "terms",
    component: () => import("@/pages/Terms.vue"),
    meta: { title: "이용약관 - 어노이" },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() { return { left: 0, top: 0 } },
})

// ✅ 전역 가드
router.beforeEach((to) => {
  const authed = store.getters.isAuthed
  const isAdmin = store.getters.isAdmin

  // 보호 라우트
  if (to.meta.requiresAuth && !authed) {
    return { name: "login", query: { redirect: to.fullPath } }
  }

  // 게스트 전용
  if (to.meta.guestOnly && authed) {
    return { name: "home" }
  }

  // ✅ 관리자 전용
  if (to.meta.admin) {
    if (!authed) {
      return { name: "login", query: { redirect: to.fullPath } }
    }
    if (!isAdmin) {
      return { name: "home" }
    }
  }

  return true
})

export default router

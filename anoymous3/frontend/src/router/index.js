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

// ✅ Vuex 스토어
import store from "@/store"

const routes = [
  { path: "/", name: "home", component: Home, meta: { title: "홈 - 어노이" } },
  { path: "/notices", name: "notices", component: Notices, meta: { title: "공지사항 - 어노이" } },
  { path: "/posts", name: "posts", component: Posts, meta: { title: "게시물 - 어노이" } },
  {
    path: "/posts/new",
    name: "new-post",
    component: NewPost,
    meta: { requiresAuth: true, title: "새 게시물 - 어노이" },
  },
  {
    path: "/posts/:id",
    name: "post-detail",
    component: PostDetail,
    props: true,
    meta: { title: "게시물 상세 - 어노이" },
  },
  {
    path: "/posts/:id/edit",
    name: "edit-post",
    component: EditPost,
    meta: { requiresAuth: true, title: "게시물 수정 - 어노이" },
    props: true,
  },
  {
    path: "/signup",
    name: "signup",
    component: Signup,
    meta: { guestOnly: true, title: "회원가입 - 어노이" },
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { guestOnly: true, title: "로그인 - 어노이" },
  },
  {
    path: "/mypage/:activityId",
    name: "mypage",
    component: MyPage,
    meta: { requiresAuth: true, title: "마이페이지 - 어노이" },
    props: true,
  },

  /* ===== 관리자 영역 ===== */
  {
    path: "/admin",
    name: "admin",
    component: () => import("@/pages/admin/AdminDashboard.vue"),
    meta: { admin: true, title: "관리자 대시보드 - 어노이" },
  },
  {
    path: "/admin/posts",
    name: "admin-posts",
    component: () => import("@/pages/admin/AdminPosts.vue"),
    meta: { admin: true, title: "관리자 · 게시물 - 어노이" },
  },
  {
    path: "/admin/notices",
    name: "admin-notices",
    component: () => import("@/pages/admin/AdminNotices.vue"),
    meta: { admin: true, title: "관리자 · 공지 - 어노이" },
  },
  {
    path: "/admin/users",
    name: "admin-users",
    component: () => import("@/pages/admin/AdminUsers.vue"),
    meta: { admin: true, title: "관리자 · 사용자 - 어노이" },
  },

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

/* ===== 가드 ===== */

// 첫 내비게이션 때 스토리지 복원 (동기 액션)
let didInit = false
router.beforeEach((to) => {
  if (!didInit) {
    store.dispatch("initFromStorage")
    didInit = true
  }

  // 페이지 타이틀
  if (to.meta?.title) {
    document.title = to.meta.title
  }

  const authed = store.getters.isAuthed
  const isAdmin = store.getters.isAdmin

  // 보호 라우트
  if (to.meta?.requiresAuth && !authed) {
    return { name: "login", query: { redirect: to.fullPath } }
  }

  // 게스트 전용
  if (to.meta?.guestOnly && authed) {
    return { name: "home" }
  }

  // 관리자 전용
  if (to.meta?.admin) {
    if (!authed) {
      return { name: "login", query: { redirect: to.fullPath } }
    }
    if (!isAdmin) {
      // 권한 없음 → 홈으로
      return { name: "home" }
    }
  }

  return true
})

export default router

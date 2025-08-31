<script setup>
import { ref, computed, watch } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

const store = useStore()
const router = useRouter()
const route = useRoute()

const isAuthed = computed(() => store.getters.isAuthed)
const isAdmin  = computed(() => store.getters.isAdmin)     // ✅ 추가
const activityId = computed(() => store.state.activityId)

// 모바일 메뉴 열림/닫힘
const menuOpen = ref(false)
const toggleMenu = () => { menuOpen.value = !menuOpen.value }

// 라우트 변경 시 메뉴 자동 닫기
watch(() => route.fullPath, () => { menuOpen.value = false })

const logout = async () => {
  await store.dispatch('logout')   // ✅ 스토리지/스토어 모두 정리
  router.push('/')                 // ✅ 메인 홈으로 이동
}
</script>

<template>
  <header class="nav-wrap">
    <nav class="nav">
      <!-- 브랜드 -->
      <router-link to="/" aria-label="logo" class="brand">
        <span class="logo">ANOY</span>
        <span class="badge">beta</span>
      </router-link>

      <!-- 데스크톱 링크 -->
      <div class="links">
        <router-link to="/notices" class="link">공지사항</router-link>
        <router-link to="/posts" class="link">게시물</router-link>
        <!-- ✅ 관리자 전용 링크(데스크톱) -->
        <router-link v-if="isAdmin" to="/admin" class="link admin-link">관리자</router-link>
      </div>

      <!-- 우측 액션 (데스크톱) -->
      <div class="actions">
        <template v-if="!isAuthed">
          <router-link to="/signup" class="btn btn-soft">회원가입</router-link>
          <router-link to="/login" class="btn btn-primary">로그인</router-link>
        </template>
        <template v-else>
          <router-link :to="`/mypage/${activityId}`" class="btn btn-ghost">
            마이페이지(<b>{{ activityId }}</b>)
          </router-link>
          <button @click="logout" class="btn btn-danger">로그아웃</button>
        </template>
      </div>

      <!-- 모바일 토글 -->
      <button
        class="hamburger"
        :aria-expanded="menuOpen ? 'true' : 'false'"
        aria-label="Menu"
        @click="toggleMenu"
      >
        <span class="bar" />
        <span class="bar" />
        <span class="bar" />
      </button>
    </nav>

    <!-- 모바일 드롭다운 -->
    <div class="mobile" v-show="menuOpen">
      <div class="mobile-links">
        <router-link to="/notices" class="m-link">공지사항</router-link>
        <router-link to="/posts" class="m-link">게시물</router-link>
        <!-- ✅ 관리자 전용 링크(모바일) -->
        <router-link v-if="isAdmin" to="/admin" class="m-link admin-link">관리자</router-link>
      </div>
      <div class="mobile-actions">
        <template v-if="!isAuthed">
          <router-link to="/signup" class="m-btn m-soft">회원가입</router-link>
          <router-link to="/login" class="m-btn m-primary">로그인</router-link>
        </template>
        <template v-else>
          <router-link :to="`/mypage/${activityId}`" class="m-btn m-ghost">
            마이페이지(<b>{{ activityId }}</b>)
          </router-link>
          <button @click="logout" class="m-btn m-danger">로그아웃</button>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.admin-link{ font-weight:800; color:#ef4444; }
/* 컨테이너 */
.nav-wrap {
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: saturate(140%) blur(6px);
  background: var(--nav-bg, rgba(255,255,255,0.85));
  border-bottom: 1px solid var(--nav-border, rgba(234,234,234,0.9));
  box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

/* 상단 바 */
.nav {
  display: grid;
  grid-template-columns: auto 1fr auto auto;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  max-width: 1120px;
  margin: 0 auto;
}

/* 로고 */
.brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}
.logo {
  font-weight: 900;
  letter-spacing: -0.02em;
  font-size: 18px;
  color: #0f172a;
}
.badge {
  font-size: 10px;
  color: #1d4ed8;
  background: #dbeafe;
  border: 1px solid #bfdbfe;
  padding: 1px 6px;
  border-radius: 999px;
}

/* 링크 */
.links { display: flex; gap: 12px; align-items: center; }
.link {
  position: relative;
  padding: 6px 8px;
  color: #374151;
  text-decoration: none;
  font-weight: 600;
  border-radius: 8px;
  transition: color .18s ease, background .18s ease;
}
.link:hover { color: #111827; background: #f3f4f6; }
.link.router-link-active { color: #1d4ed8; }
.link.router-link-active::after {
  content: ""; position: absolute; left: 8px; right: 8px; bottom: -6px;
  height: 2px; border-radius: 2px; background: #60a5fa;
}

/* 우측 버튼 */
.actions { display: none; gap: 8px; align-items: center; justify-content: flex-end; }

/* 버튼 공통 */
.btn {
  appearance: none; border: 1px solid transparent; border-radius: 10px;
  padding: 8px 12px; font-size: 13px; line-height: 1; cursor: pointer;
  transition: transform .03s, background .2s, color .2s, border-color .2s, box-shadow .2s;
  user-select: none;
}
.btn:active { transform: translateY(1px); }
.btn-primary { background: #2563eb; color: #fff; border-color: #2563eb; box-shadow: 0 8px 18px rgba(37,99,235,0.25); }
.btn-primary:hover { background: #1d4ed8; border-color: #1d4ed8; }
.btn-soft { background: #f1f5f9; color: #0f172a; border: 1px solid #e2e8f0; }
.btn-soft:hover { background: #e2e8f0; }
.btn-ghost { background: transparent; color: #374151; border: 1px solid #e5e7eb; }
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }
.btn-danger { background: #ef4444; color: #fff; border-color: #ef4444; }
.btn-danger:hover { background: #dc2626; border-color: #dc2626; }

/* 햄버거 */
.hamburger {
  display: inline-flex; flex-direction: column; justify-content: center; gap: 4px;
  width: 42px; height: 36px; border-radius: 10px; border: 1px solid #e5e7eb;
  background: #fff; cursor: pointer; transition: background .2s, border-color .2s;
}
.hamburger:hover { background: #f9fafb; border-color: #d1d5db; }
.bar { height: 2px; width: 20px; background: #374151; border-radius: 2px; margin: 0 auto; }

/* 모바일 드롭다운 */
.mobile {
  display: grid; gap: 8px; padding: 8px 16px 12px;
  border-top: 1px solid var(--nav-border, rgba(234,234,234,0.9));
  background: var(--nav-bg, rgba(255,255,255,0.9));
}
.mobile-links { display: grid; gap: 6px; }
.m-link {
  padding: 10px 8px; border-radius: 10px; text-decoration: none; color: #374151; font-weight: 600;
}
.m-link.router-link-active { color: #1d4ed8; background: #eff6ff; }

.mobile-actions {
  display: grid; gap: 8px; grid-template-columns: repeat(2, minmax(0,1fr)); margin-top: 4px;
}
.m-btn {
  padding: 10px 12px; border-radius: 10px; border: 1px solid transparent; font-size: 13px; cursor: pointer; text-align: center;
}
.m-primary { background: #2563eb; color: #fff; border-color: #2563eb; }
.m-soft { background: #f1f5f9; color: #0f172a; border-color: #e2e8f0; }
.m-ghost { background: transparent; color: #374151; border-color: #e5e7eb; }
.m-danger { background: #ef4444; color: #fff; border-color: #ef4444; }

/* 반응형: md 이상일 때 데스크톱 레이아웃 */
@media (min-width: 768px) {
  .actions { display: flex; }
  .hamburger { display: none; }
  .mobile { display: none !important; }
  .nav { grid-template-columns: auto auto 1fr; }
  .links { margin-left: 6px; }
  .actions { margin-left: auto; }
}

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .nav-wrap { --nav-bg: rgba(11,18,32,0.82); --nav-border: rgba(31,41,55,0.9); color: #e5e7eb; }
  .logo { color: #e5e7eb; }
  .badge { color: #93c5fd; background: #0b1220; border-color: #1f2937; }
  .link { color: #e5e7eb; }
  .link:hover { background: #0f172a; }
  .bar { background: #e5e7eb; }
  .btn-ghost { color: #e5e7eb; border-color: #334155; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
  .btn-soft { background: #0b1220; color: #e5e7eb; border-color: #334155; }
  .btn-soft:hover { background: #0f172a; }
  .hamburger { background: #0b1220; border-color: #334155; }
  .hamburger:hover { background: #0f172a; border-color: #475569; }
  .mobile { --nav-bg: rgba(11,18,32,0.9); --nav-border: rgba(31,41,55,0.9); }
  .m-ghost { color: #e5e7eb; border-color: #334155; }
  .m-soft { background: #0b1220; color: #e5e7eb; border-color: #334155; }
}
</style>

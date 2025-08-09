<template>
  <header class="site-header">
    <div class="header-container">
      <!-- 로고 클릭 시 홈으로 이동 -->
      <h1 class="logo">
        <router-link to="/">🌊 홈페이지</router-link>
      </h1>

      <nav class="nav-links">
        <!-- 기본 메뉴 -->
        <router-link to="/">홈</router-link>
        <router-link to="/board">게시판</router-link>

        <!-- 로그인 여부에 따른 메뉴 -->
        <template v-if="isLoggedIn">
          <span class="user-name">👋 {{ username }} 님</span>
          <button class="nav-btn" @click="logout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/join">회원가입</router-link>
          <router-link to="/login">로그인</router-link>
        </template>
      </nav>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 로그인 여부와 사용자 이름 상태
const isLoggedIn = ref(false)
const username = ref('')

// 컴포넌트 마운트 시 localStorage에서 로그인 정보 읽기
onMounted(() => {
  isLoggedIn.value = !!localStorage.getItem('token')
  username.value = localStorage.getItem('username') || ''
})

// 로그아웃 처리 + 새로고침
const logout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    isLoggedIn.value = false
    username.value = ''
    // 새로고침해서 UI 전체 반영
    window.location.reload()
  }
}
</script>

<style scoped>
.site-header {
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  padding: 14px 20px;
  color: white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.header-container {
  max-width: 1100px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 1.4rem;
  font-weight: 700;
  margin: 0;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 14px;
}

.nav-links a {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 6px 10px;
  border-radius: 8px;
  transition: 0.2s;
}

.nav-links a:hover {
  background: rgba(255,255,255,0.15);
}

.user-name {
  font-weight: bold;
  font-size: 0.95rem;
}

.nav-btn {
  background: white;
  color: #4cafef;
  border: none;
  border-radius: 8px;
  padding: 6px 12px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.nav-btn:hover {
  background: #f0f0f0;
}
</style>

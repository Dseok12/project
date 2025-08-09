<template>
  <div class="home-wrapper">
    <!-- 히어로 섹션 -->
    <section class="hero">
      <h1>
        <span class="logo">🌊 ANoYmous</span>
        <br />
        <span class="gradient">익명으로 자유롭게 소통하세요!</span>
      </h1>
      <p>커뮤니티, 자동화, 그리고 나만의 수익 창출 공간</p>

      <div class="cta-block">
        <template v-if="isLoggedIn">
          <span class="username-msg">안녕하세요, <b>{{ username }}</b>님!</span>
          <button class="logout-btn" @click="logout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/join" class="cta">지금 바로 회원가입</router-link>
        </template>
      </div>
    </section>

    <!-- 서비스 카드 섹션 -->
    <section class="features">
      <div class="feature-card">
        <span class="icon">📝</span>
        <h2>익명 게시판</h2>
        <p>자유로운 글 작성과 커뮤니티 활동</p>
      </div>
      <div class="feature-card">
        <span class="icon">🤖</span>
        <h2>자동화 수익</h2>
        <p>반복 작업을 자동화해 수익을 만들어보세요</p>
      </div>
      <div class="feature-card">
        <span class="icon">🔒</span>
        <h2>보안</h2>
        <p>안심하고 사용할 수 있는 개인 정보 보호</p>
      </div>
    </section>

    <footer>
      <span>&copy; 2025 ANoYmous | Powered by Spring Boot & Vue.js</span>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const isLoggedIn = ref(false)
const username = ref('')

onMounted(() => {
  isLoggedIn.value = !!localStorage.getItem('token')
  username.value = localStorage.getItem('username') || ''
})

const logout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    isLoggedIn.value = false
    username.value = ''
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
@use "@cssPages/home.scss";

.cta-block {
  margin-top: 18px;
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
}

.username-msg {
  font-size: 1.08rem;
  font-weight: 600;
  color: #1976d2;
}

.logout-btn {
  background: white;
  color: #4cafef;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
  box-shadow: 0 1px 5px rgba(76,175,239,0.06);
}
.logout-btn:hover {
  background: #f0f0f0;
}

.cta {
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: white;
  font-weight: 600;
  padding: 12px 22px;
  border-radius: 12px;
  text-decoration: none;
  font-size: 1.12rem;
  box-shadow: 0 2px 10px rgba(76,175,239,0.05);
  transition: 0.2s;
}
.cta:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}
</style>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1>로그인 🔐</h1>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="email" placeholder="이메일을 입력하세요" required />
        </div>
        <div class="input-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" placeholder="비밀번호를 입력하세요" required />
        </div>
        <button type="submit" class="login-btn">로그인</button>
      </form>

      <p v-if="message" class="message">{{ message }}</p>
      <p class="signup-link">
        계정이 없으신가요?
        <router-link to="/join">회원가입</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const message = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/login', {
      email: email.value,
      password: password.value
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('username', res.data.username)
    message.value = '로그인 성공!'
    router.push('/board')
  } catch (error) {
    message.value = error.response?.data || '로그인 실패'
  }
}
</script>

<style scoped>
/* 전체 페이지 중앙 정렬 & 배경 */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  font-family: 'Pretendard', sans-serif;
}

/* 카드형 로그인 박스 */
.login-card {
  background: #fff;
  padding: 2rem 2.5rem;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 380px;
  text-align: center;
  animation: fadeIn 0.6s ease-in-out;
}

.login-card h1 {
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  color: #333;
}

/* 입력 그룹 스타일 */
.input-group {
  text-align: left;
  margin-bottom: 1.2rem;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  font-size: 0.95rem;
  color: #555;
}

input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 0.95rem;
  outline: none;
  transition: 0.2s;
}

input:focus {
  border-color: #4cafef;
  box-shadow: 0 0 6px rgba(76, 175, 239, 0.2);
}

/* 로그인 버튼 */
.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: white;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.login-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* 메시지 텍스트 */
.message {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: red;
}

/* 회원가입 링크 */
.signup-link {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.signup-link a {
  color: #4cafef;
  font-weight: 600;
  text-decoration: none;
}

.signup-link a:hover {
  text-decoration: underline;
}

/* fade-in 애니메이션 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

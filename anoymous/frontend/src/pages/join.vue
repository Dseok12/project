<template>
  <div class="join-page">
    <div class="join-card">
      <h1>회원가입 ✨</h1>
      <form @submit.prevent="handleJoin">
        <div class="input-group">
          <label for="email">이메일</label>
          <input type="email" id="email" v-model="email" required />
        </div>
        <div class="input-group">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <div class="input-group">
          <label for="username">닉네임</label>
          <input type="text" id="username" v-model="username" required />
        </div>
        <button type="submit" class="join-btn">회원가입</button>
      </form>
      <p v-if="message" class="message">{{ message }}</p>
      <p class="login-link">
        이미 계정이 있으신가요?
        <router-link to="/login">로그인</router-link>
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
const username = ref('')
const message = ref('')
const router = useRouter()

const handleJoin = async () => {
  try {
    await axios.post('http://localhost:8080/api/register', {
      email: email.value,
      password: password.value,
      username: username.value
    })
    message.value = '회원가입 성공! 로그인 페이지로 이동합니다.'
    setTimeout(() => router.push('/login'), 1000)
  } catch (error) {
    message.value = error.response?.data || '회원가입 실패'
  }
}
</script>

<style scoped>
/* 전체 페이지 배경 및 중앙 정렬 */
.join-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%);
  font-family: 'Pretendard', sans-serif;
}

/* 카드형 회원가입 박스 */
.join-card {
  background: #fff;
  padding: 2rem 2.5rem;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 380px;
  text-align: center;
  animation: fadeIn 0.6s ease-in-out;
}

.join-card h1 {
  margin-bottom: 1.5rem;
  font-size: 1.8rem;
  color: #333;
}

/* 입력 그룹 */
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

/* 회원가입 버튼 */
.join-btn {
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

.join-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* 메시지 스타일 */
.message {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: red;
}

/* 로그인 링크 */
.login-link {
  margin-top: 1rem;
  font-size: 0.9rem;
}

.login-link a {
  color: #4cafef;
  font-weight: 600;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

/* fade-in 애니메이션 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

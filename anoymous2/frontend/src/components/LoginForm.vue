<template>
  <div class="login-form">
    <h2>로그인</h2>
    <form @submit.prevent="submitForm">
      <div>
        <label for="email">이메일</label>
        <input id="email" v-model="email" type="email" required />
      </div>
      <div>
        <label for="password">비밀번호</label>
        <input id="password" v-model="password" type="password" required />
      </div>
      <button type="submit">로그인</button>
    </form>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
  </div>
</template>

<script>
import apiClient from '@/api'; // Axios 인스턴스 설정 파일

export default {
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
    };
  },
  methods: {
    async submitForm() {
      this.errorMessage = '';
      try {
        const response = await apiClient.post('/api/users/login', {
          email: this.email,
          password: this.password,
        });
        // 로그인 성공 시 처리 (예: 상태관리, 토큰 저장 등)
        console.log('로그인 성공', response.data);
      } catch (error) {
        this.errorMessage = error.response?.data || '로그인 중 오류가 발생했습니다.';
      }
    },
  },
};
</script>

<style scoped>
.login-form {
  max-width: 400px;
  margin: 50px auto;
}

.login-form div {
  margin-bottom: 15px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>

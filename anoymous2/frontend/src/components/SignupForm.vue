<template>
  <div class="signup-form">
    <h2>회원가입</h2>
    <form @submit.prevent="submitForm">
      <div>
        <label for="email">이메일</label>
        <input id="email" v-model="email" type="email" required />
      </div>
      <div>
        <label for="password">비밀번호</label>
        <input id="password" v-model="password" type="password" required />
      </div>
      <button type="submit">가입하기</button>
    </form>
    <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
    <div v-if="successMessage" class="success">{{ successMessage }}</div>
  </div>
</template>

<script>
import apiClient from '@/api';  // Axios 인스턴스 파일

export default {
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
      successMessage: '',
    };
  },
  methods: {
    async submitForm() {
      this.errorMessage = '';
      this.successMessage = '';
      try {
        const response = await apiClient.post('/api/users/signup', {
          email: this.email,
          password: this.password,
        });
        this.successMessage = response.data;
        this.email = '';
        this.password = '';
      } catch (error) {
        this.errorMessage = error.response?.data || '회원가입 중 오류가 발생했습니다.';
      }
    },
  },
};
</script>

<style scoped>
.signup-form {
  max-width: 400px;
  margin: 50px auto;
}

.signup-form div {
  margin-bottom: 15px;
}

.error {
  color: red;
  margin-top: 10px;
}

.success {
  color: green;
  margin-top: 10px;
}
</style>

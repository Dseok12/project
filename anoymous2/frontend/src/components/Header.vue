<template>
  <header class="header">
    <div class="logo">MainLogo</div>
    <nav>
      <ul>
        <li><router-link to="/posts">게시물</router-link></li>
        <template v-if="!isLoggedIn">
          <li><router-link to="/login">로그인</router-link></li>
          <li><router-link to="/signup">회원가입</router-link></li>
        </template>
        <template v-else>
          <li><router-link to="/posts/create">게시물작성</router-link></li>
          <li><router-link to="/mypage">마이페이지</router-link></li>
          <li class="username" :title="user.username">{{ truncatedUsername }}</li>
        </template>
      </ul>
    </nav>
  </header>
</template>

<script>
export default {
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn;
    },
    user() {
      return this.$store.state.user || {};
    },
    truncatedUsername() {
      if (!this.user.username) return '';
      const name = this.user.username;
      return name.length > 5 ? name.slice(0, 5) + '...' : name;
    }
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  padding: 10px 20px;
  background-color: #f5f5f5;
}

nav ul {
  list-style: none;
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 0;
  margin: 0;
}

.username {
  max-width: 100px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

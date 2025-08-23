<template>
  <div class="main-container">
    <section class="hero-section">
      <h1>Welcome to Anoymus Community</h1>
      <p>Explore and share posts with friends.</p>
      <router-link to="/posts" class="btn-primary">게시물 보러가기</router-link>
    </section>
    <section class="features">
      <div class="feature-card">
        <h2>게시물</h2>
        <p>다양한 주제의 게시물을 자유롭게 공유하세요.</p>
      </div>
      <div class="feature-card">
        <h2>커뮤니티</h2>
        <p>회원들과 소통하고 친구를 만들어 보세요.</p>
      </div>
      <div class="feature-card">
        <h2>마이페이지</h2>
        <p>내가 작성한 게시물을 관리할 수 있습니다.</p>
      </div>
    </section>
    <footer class="site-footer">
      © 2025 Anoymus Community
    </footer>
  </div>
</template>

<script>

export default {
};
</script>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.hero-section {
  text-align: center;
  padding: 80px 20px;
  background: linear-gradient(135deg, #6a8efb, #82d0ff);
  color: white;
}

.hero-section h1 {
  font-size: 3.2rem;
  margin-bottom: 20px;
}

.hero-section p {
  font-size: 1.3rem;
  margin-bottom: 40px;
}

.btn-primary {
  padding: 12px 30px;
  background-color: #2563eb;
  color: white;
  border-radius: 5px;
  font-weight: bold;
  text-decoration: none;
}

.btn-primary:hover {
  background-color: #1d4ed8;
}

.features {
  display: flex;
  justify-content: center;
  gap: 30px;
  padding: 50px 20px;
  max-width: 900px;
  margin: auto;
}

.feature-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgb(0 0 0 / 0.1);
  padding: 30px;
  flex: 1;
  text-align: center;
}

.feature-card h2 {
  font-size: 1.6rem;
  margin-bottom: 15px;
  color: #2563eb;
}

.feature-card p {
  font-size: 1rem;
  color: #555;
}

.site-footer {
  margin-top: auto;
  padding: 20px;
  text-align: center;
  background-color: #f1f1f1;
  color: #666;
}
</style>

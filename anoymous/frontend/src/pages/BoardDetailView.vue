<template>
  <div class="board-detail-card" v-if="post">
    <h1 class="page-title">{{ post.title }}</h1>
    <p class="post-meta"><b>작성자:</b> {{ post.author }}</p>
    <div class="post-content" v-html="post.content"></div>
    <div v-if="post.imageUrl" class="post-image">
      <img :src="'http://localhost:8080' + post.imageUrl" />
    </div>

    <div class="btn-group" v-if="post.author === username">
      <router-link :to="`/board/${post.id}/edit`" class="form-btn">수정</router-link>
      <button @click="deletePost" class="form-btn danger">삭제</button>
    </div>

    <router-link to="/board" class="back-link">← 목록으로</router-link>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const username = localStorage.getItem('username') || ''

onMounted(async () => {
  const res = await axios.get(`http://localhost:8080/api/posts/${route.params.id}`)
  post.value = res.data
})

const deletePost = async () => {
  if (confirm('삭제하시겠습니까?')) {
    const token = localStorage.getItem('token')
    await axios.delete(`http://localhost:8080/api/posts/${post.value.id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('삭제되었습니다.')
    router.push('/board')
  }
}
</script>
<style scoped>
.board-form-card,
.board-detail-card {
  background: #fff;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0,0,0,0.06);
  max-width: 800px;
  margin: 40px auto;
  font-family: 'Pretendard', sans-serif;
  animation: fadeIn 0.5s ease-in-out;
}

.page-title {
  font-size: 1.6rem;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 15px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 0.95rem;
  outline: none;
  transition: 0.2s;
}
textarea {
  min-height: 150px;
  resize: vertical;
}
input:focus,
textarea:focus {
  border-color: #4cafef;
  box-shadow: 0 0 6px rgba(76, 175, 239, 0.2);
}

.form-btn {
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: white;
  font-weight: 600;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
  text-decoration: none;
  display: inline-block;
}
.form-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* 삭제 버튼 스타일 */
.form-btn.danger {
  background: linear-gradient(90deg, #ff6b6b, #ff8787);
}
.form-btn.danger:hover {
  background: linear-gradient(90deg, #fa5252, #ff6b6b);
}

.btn-group {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

.post-meta {
  margin-bottom: 1rem;
  color: #666;
}

.post-content {
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.post-image img {
  max-width: 100%;
  border-radius: 10px;
  border: 1px solid #eee;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.back-link {
  display: inline-block;
  margin-top: 10px;
  color: #4cafef;
  font-weight: 600;
  text-decoration: none;
}
.back-link:hover {
  text-decoration: underline;
}

/* Fade-in 효과 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

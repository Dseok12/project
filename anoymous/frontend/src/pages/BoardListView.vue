<template>
  <div class="board-list-page">
    <div class="board-header">
      <h1>📋 게시글 목록</h1>
      <router-link to="/board/write" class="write-btn">✏ 글 작성하기</router-link>
    </div>

    <ul v-if="posts.length" class="post-list">
      <li v-for="post in posts" :key="post.id">
        <router-link :to="`/board/${post.id}`" class="post-card">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-meta">
            작성자: {{ post.author }} | 작성일:
            {{ new Date(post.createdDate).toLocaleDateString() }}
          </p>
        </router-link>
      </li>
    </ul>

    <p v-else class="no-post">등록된 글이 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const posts = ref([])

onMounted(async () => {
  const res = await axios.get('http://localhost:8080/api/posts')
  posts.value = res.data.content
})
</script>


<style scoped>
.board-list-page {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0,0,0,0.06);
  animation: fadeIn 0.4s ease-in-out;
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.board-header h1 {
  font-size: 1.6rem;
  font-weight: 700;
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.write-btn {
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: #fff;
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  text-decoration: none;
  transition: 0.2s;
}
.write-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* 게시글 리스트 */
.post-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.post-list li {
  margin-bottom: 14px;
}

.post-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  border: 1px solid #eee;
  border-radius: 12px;
  background: #fafafa;
  padding: 16px;
  text-decoration: none;
  color: #333;
  transition: all 0.2s ease;
  position: relative; /* 버튼 배치용 */
}

.post-card:hover {
  background: #f0f8ff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transform: translateY(-2px);
}

.post-title {
  font-size: 1.05rem;
  font-weight: 600;
  margin-bottom: 6px;
}

.post-meta {
  font-size: 0.85rem;
  color: #666;
}

/* 수정/삭제 버튼 */
.action-buttons {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 6px;
}

.edit-btn,
.delete-btn {
  border: none;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.75rem;
  cursor: pointer;
}

.edit-btn {
  background: #e0f2ff;
  color: #1976d2;
}

.delete-btn {
  background: #ffe5e5;
  color: #d32f2f;
}

.edit-btn:hover {
  background: #bbdefb;
}

.delete-btn:hover {
  background: #ffcccc;
}

/* 글 없을 때 */
.no-post {
  text-align: center;
  color: #aaa;
  padding: 20px 0;
  font-size: 0.95rem;
}

/* Fade-in 효과 */
@keyframes fadeIn {
  from {opacity: 0; transform: translateY(-6px);}
  to {opacity: 1; transform: translateY(0);}
}
</style>


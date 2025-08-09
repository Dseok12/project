<template>
  <div class="board-list">
    <h2 class="list-title">📝 글 목록</h2>

    <ul v-if="posts.length" class="post-list">
      <li v-for="post in posts" :key="post.id" @click="$emit('select', post)">
        <div class="post-card">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-meta">작성자: {{ post.author }}</p>
        </div>
      </li>
    </ul>

    <p class="no-post" v-else>작성된 글이 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const posts = ref([])

// 게시글 목록 불러오기
const fetchPosts = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/posts')
    posts.value = res.data.content
  } catch (err) {
    console.error('게시글 목록 조회 실패:', err)
  }
}

onMounted(fetchPosts)
defineExpose({ fetchPosts })
</script>

<style scoped>
.board-list {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.05);
  animation: fadeIn 0.4s ease-in-out;
}

.list-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 15px;
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.post-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.post-list li {
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.2s ease;
}

.post-card {
  background: #fafafa;
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid #eee;
  transition: all 0.2s ease;
}

.post-card:hover {
  background: #f0f8ff;
  box-shadow: 0 4px 14px rgba(0,0,0,0.05);
  transform: translateY(-2px);
}

.post-title {
  font-size: 1.05rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.post-meta {
  font-size: 0.85rem;
  color: #666;
}

.no-post {
  color: #999;
  text-align: center;
  padding: 20px 0;
}

/* Fade-in animation */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

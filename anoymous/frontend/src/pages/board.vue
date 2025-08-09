<template>
  <div class="board-wrapper">
    <h1 class="board-title">📋 게시판</h1>

    <!-- 글 작성 -->
    <div class="post-section">
      <BoardPost @created="refreshList" />
    </div>

    <hr class="divider" />

    <!-- 글 목록 -->
    <div v-if="!selectedPost" class="board-list-container">
      <BoardList ref="boardListRef" @select="selectPost" />
    </div>

    <!-- 글 상세보기 -->
    <div v-else class="board-detail-container">
      <BoardDetail :post="selectedPost" @close="closeDetail" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

import BoardList from '../components/BoardList.vue'
import BoardPost from '../components/BoardPost.vue'
import BoardDetail from '../components/BoardDetail.vue'

const selectedPost = ref(null)
const boardListRef = ref(null)

const selectPost = async (post) => {
  try {
    const res = await axios.get(`http://localhost:8080/api/posts/${post.id}`)
    selectedPost.value = res.data
  } catch (err) {
    console.error('게시글 상세 조회 실패:', err)
  }
}

const closeDetail = () => {
  selectedPost.value = null
}

const refreshList = () => {
  if (boardListRef.value) {
    boardListRef.value.fetchPosts()
  }
}
</script>

<style lang="scss" scoped>
.board-wrapper {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
}

.board-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 25px;
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.post-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.06);
}

.divider {
  border: none;
  height: 1px;
  background-color: #e5e5e5;
  margin: 28px 0;
}

.board-list-container,
.board-detail-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

/* 반응형 */
@media (max-width: 768px) {
  .board-wrapper {
    padding: 10px;
  }
  .board-title {
    font-size: 1.6rem;
  }
  .post-section,
  .board-list-container,
  .board-detail-container {
    padding: 15px;
  }
}
</style>

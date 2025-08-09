<template>
  <div class="post-detail-card" v-if="post">
    <h2 class="post-title">{{ post.title }}</h2>
    <p class="post-author"><b>작성자:</b> {{ post.author }}</p>

    <div class="post-content" v-html="post.content"></div>

    <div v-if="post.imageUrl" class="post-image">
      <img :src="'http://localhost:8080' + post.imageUrl" alt="첨부 이미지" />
    </div>

    <button class="back-btn" @click="$emit('close')">목록으로</button>
  </div>
</template>

<script setup>
defineProps({
  post: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
/* 카드형 컨테이너 */
.post-detail-card {
  background: #fff;
  padding: 2rem;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.06);
  max-width: 800px;
  margin: 30px auto;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  animation: fadeIn 0.5s ease-in-out;
}

/* 제목 스타일 */
.post-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
}

/* 작성자 */
.post-author {
  font-size: 0.95rem;
  font-weight: 500;
  color: #666;
  margin-bottom: 20px;
}

/* 본문 내용 */
.post-content {
  line-height: 1.65;
  font-size: 1rem;
  color: #444;
  margin-bottom: 20px;
  word-break: break-word;
}

/* 이미지 */
.post-image {
  margin: 20px 0;
  text-align: center;
}
.post-image img {
  max-width: 100%;
  border-radius: 10px;
  border: 1px solid #eee;
  box-shadow: 0 4px 10px rgba(0,0,0,0.03);
}

/* 목록으로 버튼 */
.back-btn {
  display: inline-block;
  padding: 12px 20px;
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: white;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}
.back-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* 부드러운 등장 애니메이션 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

<template>
  <div class="board-form-card">
    <h1 class="page-title">✏ 게시글 작성</h1>
    <form @submit.prevent="createPost">
      <input v-model="title" placeholder="제목" required />
      <textarea v-model="content" placeholder="내용" required></textarea>
      <input type="file" @change="handleFileUpload" />
      <button type="submit" class="form-btn">등록</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const title = ref('')
const content = ref('')
const imageFile = ref(null)
const router = useRouter()

const handleFileUpload = (e) => {
  imageFile.value = e.target.files[0]
}

const createPost = async () => {
  const formData = new FormData()
  formData.append('title', title.value)
  formData.append('content', content.value)
  formData.append('author', localStorage.getItem('username'))
  if (imageFile.value) formData.append('image', imageFile.value)

  await axios.post('http://localhost:8080/api/posts', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  alert('게시글이 등록되었습니다.')
  router.push('/board')
}
</script>

<style scoped>
.board-form-card {
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
input, textarea {
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
input:focus, textarea:focus {
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
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

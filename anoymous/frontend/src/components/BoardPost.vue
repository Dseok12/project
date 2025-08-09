<template>
  <div class="post-form-card">
    <h2 class="form-title">✏️ 새 글 작성</h2>

    <form @submit.prevent="createPost">
      <div class="input-group">
        <input v-model="newPost.title" placeholder="제목" required />
      </div>
      <div class="input-group">
        <textarea v-model="newPost.content" placeholder="내용" required></textarea>
      </div>
      <div class="input-group">
        <input v-model="newPost.author" placeholder="작성자" required />
      </div>
      <div class="input-group file-upload">
        <input type="file" @change="handleFileUpload" />
      </div>
      <button type="submit" class="submit-btn">등록</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const emit = defineEmits(['created'])

const newPost = ref({
  title: '',
  content: '',
  author: ''
})
const imageFile = ref(null)

const handleFileUpload = (event) => {
  imageFile.value = event.target.files[0]
}

const createPost = async () => {
  try {
    const formData = new FormData()
    formData.append('title', newPost.value.title)
    formData.append('content', newPost.value.content)
    formData.append('author', newPost.value.author)
    if (imageFile.value) {
      formData.append('image', imageFile.value)
    }

    await axios.post('http://localhost:8080/api/posts', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    alert('게시글이 등록되었습니다.')
    newPost.value = { title: '', content: '', author: '' }
    imageFile.value = null
    emit('created')
  } catch (err) {
    console.error('게시글 작성 실패:', err)
  }
}
</script>

<style scoped>
.post-form-card {
  background: #fff;
  padding: 1.6rem 2rem;
  border-radius: 16px;
  box-shadow: 0 6px 30px rgba(0, 0, 0, 0.06);
  margin-bottom: 25px;
  font-family: 'Pretendard', 'Noto Sans KR', sans-serif;
  animation: fadeIn 0.5s ease-in-out;
}

.form-title {
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(90deg, #4cafef, #42a5f5);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 입력 그룹 */
.input-group {
  margin-bottom: 14px;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  font-size: 0.95rem;
  transition: 0.2s;
}

textarea {
  min-height: 120px;
  resize: vertical;
}

input:focus,
textarea:focus {
  border-color: #4cafef;
  box-shadow: 0 0 6px rgba(76, 175, 239, 0.2);
}

/* 파일 업로드 */
.file-upload input[type="file"] {
  padding: 6px;
  border: none;
}

/* 등록 버튼 */
.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(90deg, #4cafef, #6ec6ff);
  color: white;
  font-weight: 600;
  font-size: 1rem;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.submit-btn:hover {
  background: linear-gradient(90deg, #42a5f5, #4cafef);
  transform: translateY(-2px);
}

/* fade-in animation */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-6px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

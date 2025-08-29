<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import client from '@/api/client'

const route = useRoute()
const router = useRouter()

const title = ref('')
const content = ref('')
const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')

const load = async () => {
  try {
    loading.value = true
    const { data } = await client.get(`/posts/${route.params.id}`)
    title.value = data.title || ''
    content.value = data.content || ''
  } catch (e) {
    console.error(e)
    errorMsg.value = '게시글을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  if (!title.value.trim() || !content.value.trim()) {
    return alert('제목과 내용을 입력하세요.')
  }
  try {
    saving.value = true
    await client.put(`/posts/${route.params.id}`, {
      title: title.value.trim(),
      content: content.value
    })
    alert('수정되었습니다.')
    router.push(`/posts/${route.params.id}`)
  } catch (e) {
    console.error(e)
    const msg = e?.response?.status === 403
      ? '수정 권한이 없습니다.'
      : (e?.response?.data?.error || '수정에 실패했습니다.')
    alert(msg)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <section>
    <h2>게시글 수정</h2>

    <div v-if="loading">불러오는 중...</div>
    <div v-else>
      <label>제목</label>
      <input v-model="title" />

      <label style="margin-top:8px;">내용</label>
      <textarea v-model="content" rows="10" style="width:100%; white-space:pre-wrap;"></textarea>

      <div style="margin-top:12px;">
        <button @click="save" :disabled="saving">{{ saving ? '저장중...' : '저장' }}</button>
        <button @click="$router.back()" style="margin-left:8px;">취소</button>
      </div>

      <div v-if="errorMsg" style="color:crimson; margin-top:8px;">{{ errorMsg }}</div>
    </div>
  </section>
</template>

<style scoped>
input, textarea { width: 100%; }
</style>

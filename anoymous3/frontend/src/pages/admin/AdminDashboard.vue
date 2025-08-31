<template>
  <section class="admin">
    <h2>관리자 대시보드</h2>

    <div class="card">
      <h3>공지 관리</h3>
      <input v-model="noticeTitle" class="input" placeholder="공지 제목" />
      <textarea v-model="noticeBody" class="input" rows="6" placeholder="공지 본문(마크다운)"></textarea>
      <button class="btn btn-primary" @click="createNotice">공지 등록</button>
    </div>

    <div class="card">
      <h3>게시물 관리</h3>
      <ul class="list">
        <li v-for="p in posts" :key="p.id" class="list-item">
          <span v-if="p.notice" class="badge">공지</span>
          #{{ p.id }} {{ p.title }}
          <span class="muted">· {{ new Date(p.createdAt).toLocaleString() }}</span>
          <span class="grow"></span>
          <button class="btn sm" @click="toggleNotice(p)">{{ p.notice ? '공지 해제' : '공지 지정' }}</button>
          <button class="btn sm btn-danger" @click="adminDeletePost(p.id)">삭제</button>
        </li>
      </ul>
    </div>

    <div class="card">
      <h3>유저 관리</h3>
      <ul class="list">
        <li v-for="u in users" :key="u.id" class="list-item">
          {{u.activityId}} <span class="muted">({{u.email}})</span> · {{u.status}}
          <span class="grow"></span>
          <button class="btn sm" @click="suspend(u.id)">정지</button>
          <button class="btn sm" @click="unsuspend(u.id)">해제</button>
          <button class="btn sm btn-danger" @click="hardDelete(u.id)">탈퇴(삭제)</button>
        </li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import client from '@/api/client'

const posts = ref([]), users = ref([])
const noticeTitle = ref(''), noticeBody = ref('')

const load = async () => {
  // 게시물(관리자용 전체 목록) — 필요 시 별도 파라미터/엔드포인트
  const { data: ps } = await client.get('/posts', { params: { all: true, sort: 'createdAt,desc' } })
  posts.value = ps?.content ?? ps ?? []

  // (선택) 관리자 유저 목록 API가 있다면 사용
  try {
    const { data: us } = await client.get('/api/admin/users')
    users.value = Array.isArray(us) ? us : []
  } catch { users.value = [] }
}

const createNotice = async () => {
  if (!noticeTitle.value.trim()) return alert('제목을 입력하세요.')
  await client.post('/posts', { title: noticeTitle.value, content: noticeBody.value, notice: true })
  noticeTitle.value = ''; noticeBody.value = ''; await load()
}
const toggleNotice = async (p) => {
  await client.patch(`/api/admin/posts/${p.id}/notice`, { notice: !p.notice }); await load()
}
const adminDeletePost = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  await client.delete(`/api/admin/posts/${id}`); await load()
}

const suspend = async (id) => {
  await client.patch(`/api/admin/users/${id}/status`, { status: 'SUSPENDED' })
  await load()
}
const unsuspend = async (id) => {
  await client.patch(`/api/admin/users/${id}/status`, { status: 'ACTIVE' })
  await load()
}
const hardDelete = async (id) => {
  if (!confirm('정말 탈퇴(삭제)하시겠습니까?')) return
  await client.delete(`/api/admin/users/${id}`); await load()
}

onMounted(load)
</script>

<style scoped>
.admin{ max-width: 960px; margin: 24px auto; padding: 0 16px; }
.card{ border:1px solid #eaeaea; border-radius:12px; padding:16px; margin-bottom:16px; background:#fff; }
.input{ width:100%; padding:10px; border:1px solid #e5e7eb; border-radius:10px; margin-bottom:8px; }
.btn{ padding:8px 12px; border:1px solid #e5e7eb; border-radius:8px; background:#fff; cursor:pointer; }
.btn.sm{ padding:6px 10px; font-size:12px; margin-left:6px; }
.btn-primary{ background:#2563eb; color:#fff; border-color:#2563eb; }
.btn-danger{ background:#ef4444; color:#fff; border-color:#ef4444; margin-left:6px; }
.list{ list-style:none; margin:0; padding:0; }
.list-item{ display:flex; align-items:center; gap:8px; padding:10px 0; border-bottom:1px solid #f1f5f9; }
.badge{ font-size:11px; font-weight:900; padding:2px 6px; border-radius:999px; background:#ffedd5; color:#9a3412; border:1px solid #fed7aa; }
.muted{ color:#6b7280; font-size:12px; }
.grow{ flex:1; }
@media (prefers-color-scheme: dark){
  .card{ background:#0b1220; border-color:#1f2937; color:#e5e7eb; }
  .input{ background:#0b1220; color:#e5e7eb; border-color:#334155; }
  .btn{ border-color:#334155; color:#e5e7eb; background:#0b1220; }
  .btn-primary{ background:#1d4ed8; border-color:#1d4ed8; }
  .btn-danger{ background:#dc2626; border-color:#dc2626; }
  .list-item{ border-color:#1f2937; }
}
</style>

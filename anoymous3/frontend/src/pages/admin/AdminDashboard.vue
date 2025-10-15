<!-- frontend/src/pages/admin/AdminDashboard.vue -->
<template>
  <section class="admin">
    <h2>관리자 대시보드</h2>

    <!-- 공지 관리 -->
    <div class="card">
      <h3>공지 관리</h3>
      <input v-model="noticeTitle" class="input" placeholder="공지 제목" />
      <textarea v-model="noticeBody" class="input" rows="6" placeholder="공지 본문(마크다운)"></textarea>
      <button class="btn btn-primary" :disabled="noticeLoading" @click="createNotice">
        {{ noticeLoading ? '등록 중…' : '공지 등록' }}
      </button>
    </div>

    <!-- 게시물 관리 -->
    <div class="card">
      <h3>게시물 관리</h3>
      <ul class="list">
        <li v-for="p in posts" :key="p.id" class="list-item">
          <span v-if="p.notice" class="badge">공지</span>
          #{{ p.id }} {{ p.title }}
          <span class="muted">· {{ toLocal(p.createdAt) }}</span>
          <span class="grow"></span>
          <button class="btn sm" :disabled="postToggleLoadingId===p.id" @click="toggleNotice(p)">
            {{ p.notice ? '공지 해제' : '공지 지정' }}
          </button>
          <button class="btn sm btn-danger" :disabled="postDeleteLoadingId===p.id" @click="adminDeletePost(p.id)">
            삭제
          </button>
        </li>
      </ul>
      <div v-if="!posts.length" class="muted">게시물이 없습니다.</div>
    </div>

    <!-- 유저 관리 -->
    <div class="card">
      <h3>유저 관리</h3>

      <div class="user-toolbar">
        <input
          v-model.trim="q"
          class="input"
          placeholder="닉네임(activityId) 또는 이메일 검색"
          @keyup.enter="loadUsers"
        />
        <button class="btn" :disabled="usersLoading" @click="loadUsers">
          {{ usersLoading ? '검색 중…' : '검색' }}
        </button>
      </div>

      <ul class="list">
        <li v-for="u in users" :key="u.id" class="list-item">
          <b>{{ u.activityId }}</b>
          <span class="muted">({{ u.email }})</span>
          <span class="muted">· {{ u.role }}</span>
          <span class="muted" v-if="u.status">· {{ u.status }}</span>
          <span class="muted" v-if="u.suspendedUntil">· 정지 해제: {{ toLocal(u.suspendedUntil) }}</span>

          <span class="grow"></span>

          <!-- 역할(권한) -->
          <div class="btn-group">
            <button
              class="btn sm"
              v-if="u.role !== 'ADMIN'"
              :disabled="roleLoadingId===u.id"
              @click="setRole(u.id, 'ADMIN')"
              title="관리자로 승격"
            >
              관리자 승격
            </button>
            <button
              class="btn sm"
              v-else
              :disabled="roleLoadingId===u.id"
              @click="setRole(u.id, 'USER')"
              title="관리자 권한 해제"
            >
              관리자 해제
            </button>
          </div>

          <!-- 기간 정지/해제/탈퇴 -->
          <div class="btn-group">
            <button class="btn sm" :disabled="suspendLoadingId===u.id" @click="suspendDays(u.id, 3)">3일</button>
            <button class="btn sm" :disabled="suspendLoadingId===u.id" @click="suspendDays(u.id, 7)">7일</button>
            <button class="btn sm" :disabled="suspendLoadingId===u.id" @click="suspendDays(u.id, 15)">15일</button>
            <button class="btn sm" :disabled="suspendLoadingId===u.id" @click="suspendDays(u.id, null)">영구</button>
            <button class="btn sm" :disabled="suspendLoadingId===u.id" @click="unsuspend(u.id)">해제</button>
            <button class="btn sm btn-danger" :disabled="deleteUserLoadingId===u.id" @click="hardDelete(u.id)">탈퇴</button>
          </div>
        </li>
      </ul>
      <div v-if="!users.length" class="muted">검색 결과가 없습니다.</div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import client from '@/api/client'
import useAuth from '@/composables/useAuth'

const router = useRouter()
const { isAdmin: isAdminRef } = useAuth()
const isAdmin = computed(() => isAdminRef.value === true)

// 목록 상태
const posts = ref([])
const users = ref([])
const postsLoading = ref(false)
const usersLoading = ref(false)

// 폼 상태
const noticeTitle = ref('')
const noticeBody = ref('')
const noticeLoading = ref(false)

// 검색어
const q = ref('')

// 버튼 개별 로딩
const postToggleLoadingId = ref(null)
const postDeleteLoadingId = ref(null)
const roleLoadingId = ref(null)
const suspendLoadingId = ref(null)
const deleteUserLoadingId = ref(null)

// 유틸
const unwrap = (data) => (Array.isArray(data) ? data : (data?.content ?? []))
const toLocal = (d) => (d ? new Date(d).toLocaleString() : '')
const isoAfterDays = (days) =>
  new Date(Date.now() + days * 24 * 60 * 60 * 1000).toISOString()

/* ===== 가드 ===== */
onMounted(async () => {
  if (!isAdmin.value) {
    alert('관리자만 접근할 수 있습니다.')
    router.replace('/')
    return
  }
  await load()
})

/* ===== 게시물 ===== */
const loadPosts = async () => {
  postsLoading.value = true
  try {
    const { data } = await client.get('/admin/posts', { params: { size: 100, sort: 'createdAt,desc' } })
    posts.value = unwrap(data)
  } catch (e) {
    console.error('loadPosts error', e)
    posts.value = []
  } finally {
    postsLoading.value = false
  }
}

const createNotice = async () => {
  if (!noticeTitle.value.trim()) return alert('제목을 입력하세요.')
  try {
    noticeLoading.value = true
    await client.post('/admin/notices', { title: noticeTitle.value, content: noticeBody.value })
    noticeTitle.value = ''
    noticeBody.value = ''
    await loadPosts()
  } catch (e) {
    console.error('createNotice error', e)
    alert(e?.response?.data?.error || '공지 등록 실패')
  } finally {
    noticeLoading.value = false
  }
}

const toggleNotice = async (p) => {
  try {
    postToggleLoadingId.value = p.id
    await client.patch(`/admin/posts/${p.id}/notice`, { notice: !p.notice })
    await loadPosts()
  } catch (e) {
    console.error('toggleNotice error', e)
    alert(e?.response?.data?.error || '공지 토글 실패')
  } finally {
    postToggleLoadingId.value = null
  }
}

const adminDeletePost = async (id) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    postDeleteLoadingId.value = id
    await client.delete(`/admin/posts/${id}`)
    await loadPosts()
  } catch (e) {
    console.error('adminDeletePost error', e)
    alert(e?.response?.data?.error || '삭제 실패')
  } finally {
    postDeleteLoadingId.value = null
  }
}

/* ===== 유저 ===== */
const loadUsers = async () => {
  usersLoading.value = true
  try {
    const { data } = await client.get('/admin/users', {
      params: { q: q.value, size: 50, sort: 'id,desc' }
    })
    users.value = unwrap(data)
  } catch (e) {
    console.error('loadUsers error', e)
    users.value = []
    alert('유저 목록을 불러오지 못했습니다.')
  } finally {
    usersLoading.value = false
  }
}

// 역할(권한) 변경: USER ↔ ADMIN
const setRole = async (id, role) => {
  if (!confirm(`이 사용자의 역할을 ${role}로 변경할까요?`)) return
  try {
    roleLoadingId.value = id
    await client.patch(`/admin/users/${id}/role`, { role })
    await loadUsers()
  } catch (e) {
    console.error('setRole error', e)
    alert(e?.response?.data?.error || '권한 변경 실패')
  } finally {
    roleLoadingId.value = null
  }
}

// 기간 정지(3/7/15일) & 영구정지(null)
const suspendDays = async (id, daysOrNull) => {
  const confirmMsg = daysOrNull == null ? '이 사용자를 영구 정지할까요?' : `${daysOrNull}일 정지할까요?`
  if (!confirm(confirmMsg)) return
  const body = daysOrNull == null
    ? { status: 'SUSPENDED', suspendedUntil: null }
    : { status: 'SUSPENDED', suspendedUntil: isoAfterDays(daysOrNull) }
  try {
    suspendLoadingId.value = id
    await client.patch(`/admin/users/${id}/status`, body)
    await loadUsers()
  } catch (e) {
    console.error('suspendDays error', e)
    alert('정지 처리 실패')
  } finally {
    suspendLoadingId.value = null
  }
}

const unsuspend = async (id) => {
  if (!confirm('정지를 해제할까요?')) return
  try {
    suspendLoadingId.value = id
    await client.patch(`/admin/users/${id}/status`, { status: 'ACTIVE' })
    await loadUsers()
  } catch (e) {
    console.error('unsuspend error', e)
    alert('해제 처리 실패')
  } finally {
    suspendLoadingId.value = null
  }
}

const hardDelete = async (id) => {
  if (!confirm('정말 탈퇴(삭제)하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return
  try {
    deleteUserLoadingId.value = id
    await client.delete(`/admin/users/${id}`)
    await loadUsers()
  } catch (e) {
    console.error('hardDelete error', e)
    alert('탈퇴(삭제) 실패')
  } finally {
    deleteUserLoadingId.value = null
  }
}

const load = async () => {
  await Promise.all([loadPosts(), loadUsers()])
}
</script>

<style scoped>
.admin{ max-width: 960px; margin: 24px auto; padding: 0 16px; }
.card{ border:1px solid #eaeaea; border-radius:12px; padding:16px; margin-bottom:16px; background:#fff; }
.input{ width:100%; padding:10px; border:1px solid #e5e7eb; border-radius:10px; margin-bottom:8px; }
.btn{ padding:8px 12px; border:1px solid #e5e7eb; border-radius:8px; background:#fff; cursor:pointer; }
.btn:disabled{ opacity:.6; cursor:not-allowed; }
.btn.sm{ padding:6px 10px; font-size:12px; margin-left:6px; }
.btn-primary{ background:#2563eb; color:#fff; border-color:#2563eb; }
.btn-danger{ background:#ef4444; color:#fff; border-color:#ef4444; margin-left:6px; }
.list{ list-style:none; margin:0; padding:0; }
.list-item{ display:flex; align-items:center; gap:8px; padding:10px 0; border-bottom:1px solid #f1f5f9; }
.badge{ font-size:11px; font-weight:900; padding:2px 6px; border-radius:999px; background:#ffedd5; color:#9a3412; border:1px solid #fed7aa; }
.muted{ color:#6b7280; font-size:12px; }
.grow{ flex:1; }
.user-toolbar{ display:flex; gap:8px; align-items:center; margin-bottom:8px; }
.btn-group{ display:flex; gap:6px; flex-wrap:wrap; }

@media (prefers-color-scheme: dark){
  .card{ background:#0b1220; border-color:#1f2937; color:#e5e7eb; }
  .input{ background:#0b1220; color:#e5e7eb; border-color:#334155; }
  .btn{ border-color:#334155; color:#e5e7eb; background:#0b1220; }
  .btn-primary{ background:#1d4ed8; border-color:#1d4ed8; }
  .btn-danger{ background:#dc2626; border-color:#dc2626; }
  .list-item{ border-color:#1f2937; }
}
</style>

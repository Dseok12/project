<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import client from '@/api/client'

const store = useStore()
const route = useRoute()
const router = useRouter()

// 탭
const tabs = ['profile', 'password', 'posts', 'activity', 'account'] // 소개/비번/내글/활동로그/아이디
const activeTab = ref('profile')

// 기본 정보
const me = ref({ email: '', activityId: '', intro: '' })
const loadingMe = ref(false)
const loadError = ref('')

// 비번 변경
const curPw = ref('')
const newPw = ref('')
const newPw2 = ref('')
const pwLoading = ref(false)

// 내 글
const myPosts = ref([])
const postsLoading = ref(false)
const postsError = ref('')

// 활동 로그
const logs = ref([])
const logsLoading = ref(false)
const logsError = ref('')

// 아이디 변경
const newActivityId = ref('')
const idCheckMsg = ref('')
const idOk = ref(false)
const idLoading = ref(false)

// 로그인 여부/현재 아이디
const isAuthed = computed(() => store.getters.isAuthed)
const storeActivityId = computed(() => store.state.activityId)

// 공통 헬퍼
const toLocal = (iso) => (iso ? new Date(iso).toLocaleString() : '')
const apiCatch = (e, fallbackMsg = '요청에 실패했습니다.') => {
  console.error(e)
  alert(e?.response?.data?.error || fallbackMsg)
}

// 내 정보 로드
const fetchMe = async () => {
  if (!isAuthed.value) {
    alert('로그인이 필요합니다.')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  loadingMe.value = true
  loadError.value = ''
  try {
    const { data } = await client.get('/users/me')
    me.value = data || { email: '', activityId: '', intro: '' }
    // 라우트 activityId와 다르면 URL 정정
    if (route.params.activityId !== me.value.activityId) {
      router.replace(`/mypage/${me.value.activityId}`)
    }
  } catch (e) {
    loadError.value = '내 정보를 불러오지 못했습니다.'
    console.error(e)
  } finally {
    loadingMe.value = false
  }
}

// 내 소개 저장
const saveIntro = async () => {
  try {
    await client.put('/users/me/profile', { intro: me.value.intro ?? '' })
    alert('소개가 저장되었습니다.')
  } catch (e) {
    apiCatch(e, '소개 저장에 실패했습니다.')
  }
}

// 비밀번호 변경
const changePassword = async () => {
  if (!curPw.value || !newPw.value || !newPw2.value) return alert('모든 항목을 입력하세요.')
  if (newPw.value !== newPw2.value) return alert('새 비밀번호 확인이 일치하지 않습니다.')
  pwLoading.value = true
  try {
    await client.post('/users/me/password', {
      currentPassword: curPw.value,
      newPassword: newPw.value
    })
    alert('비밀번호가 변경되었습니다. 다시 로그인해 주세요.')
    await store.dispatch('logout')
    router.push('/login')
  } catch (e) {
    apiCatch(e, '비밀번호 변경에 실패했습니다.')
  } finally {
    pwLoading.value = false
  }
}

// 내 글 가져오기
const fetchMyPosts = async () => {
  postsLoading.value = true
  postsError.value = ''
  try {
    const { data } = await client.get('/posts', { params: { mine: true, sort: 'createdAt,desc' } })
    // 페이지형/리스트형 모두 호환
    myPosts.value = Array.isArray(data?.content) ? data.content : (Array.isArray(data) ? data : [])
  } catch (e) {
    postsError.value = '내 글을 불러오지 못했습니다.'
    console.error(e)
  } finally {
    postsLoading.value = false
  }
}

// 활동 로그 가져오기
const fetchLogs = async () => {
  logsLoading.value = true
  logsError.value = ''
  try {
    const { data } = await client.get('/users/me/activity-logs', { params: { limit: 50 } })
    logs.value = Array.isArray(data) ? data : []
  } catch (e) {
    logsError.value = '활동 로그를 불러오지 못했습니다.'
    console.error(e)
  } finally {
    logsLoading.value = false
  }
}

// 아이디 중복 확인
const checkNewId = async () => {
  const id = (newActivityId.value || '').trim()
  if (!id) {
    idOk.value = false
    idCheckMsg.value = '아이디를 입력하세요.'
    return
  }
  idLoading.value = true
  try {
    const { data } = await client.get('/auth/activity-id/available', { params: { activityId: id } })
    idOk.value = !!data?.available
    idCheckMsg.value = idOk.value ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.'
  } catch (e) {
    idOk.value = false
    idCheckMsg.value = '중복 확인 실패. 잠시 후 다시 시도하세요.'
    console.error(e)
  } finally {
    idLoading.value = false
  }
}

// 아이디 변경
const changeActivityId = async () => {
  const id = (newActivityId.value || '').trim()
  if (!id) return alert('아이디를 입력하세요.')
  await checkNewId()
  if (!idOk.value) return alert('사용 가능한 아이디를 입력하세요.')
  try {
    const { data } = await client.patch('/users/me/activity-id', { activityId: id })
    // 스토리지/스토어 갱신
    await store.dispatch('login', {
      token: store.state.token,
      activityId: data?.activityId || id,
      persist: (sessionStorage.getItem('token') ? 'session' : 'local')
    })
    alert('활동 아이디가 변경되었습니다.')
    router.replace(`/mypage/${data?.activityId || id}`)
    await fetchMe()
  } catch (e) {
    apiCatch(e, '아이디 변경에 실패했습니다.')
  }
}

onMounted(async () => {
  await fetchMe()
  await fetchMyPosts()
  await fetchLogs()
})
</script>

<template>
  <section class="mypage">
    <header class="header">
      <div class="who">
        <h2 class="title">마이페이지 — {{ me.activityId || route.params.activityId }}</h2>
        <span class="email">{{ me.email }}</span>
      </div>
    </header>

    <!-- 탭 -->
    <div class="tabs" role="tablist" aria-label="mypage tabs">
      <button
        v-for="t in tabs" :key="t"
        class="tab"
        :class="{ active: activeTab===t }"
        role="tab"
        :aria-selected="activeTab===t"
        @click="activeTab=t"
      >
        {{ t === 'profile' ? '내 소개'
           : t === 'password' ? '비밀번호 변경'
           : t === 'posts' ? '내 글 보기'
           : t === 'activity' ? '활동 로그'
           : '아이디 변경' }}
      </button>
    </div>

    <!-- 카드 컨테이너 -->
    <div class="card">
      <!-- 내 소개 -->
      <div v-if="activeTab==='profile'">
        <div v-if="loadingMe" class="skeleton-set">
          <div class="sk-line w60"></div>
          <div class="sk-area"></div>
        </div>
        <div v-else-if="loadError" class="error-box">{{ loadError }}</div>
        <div v-else class="form">
          <label class="label">소개</label>
          <textarea
            v-model="me.intro"
            rows="6"
            class="textarea"
            placeholder="자기 소개를 입력하세요."
          />
          <div class="actions">
            <button class="btn btn-primary" @click="saveIntro">저장</button>
          </div>
        </div>
      </div>

      <!-- 비밀번호 변경 -->
      <div v-else-if="activeTab==='password'" class="form">
        <label class="label">현재 비밀번호</label>
        <input v-model="curPw" type="password" autocomplete="current-password" class="input" />
        <label class="label">새 비밀번호</label>
        <input v-model="newPw" type="password" autocomplete="new-password" class="input" />
        <label class="label">새 비밀번호 확인</label>
        <input v-model="newPw2" type="password" autocomplete="new-password" class="input" />
        <div class="actions">
          <button class="btn btn-primary" :disabled="pwLoading" @click="changePassword">
            {{ pwLoading ? '변경 중…' : '비밀번호 변경' }}
          </button>
        </div>
      </div>

      <!-- 내 글 보기 -->
      <div v-else-if="activeTab==='posts'">
        <div v-if="postsLoading" class="skeleton-list">
          <div v-for="i in 5" :key="i" class="sk-card">
            <div class="sk-title"></div>
            <div class="sk-meta"></div>
          </div>
        </div>
        <div v-else-if="postsError" class="error-box">{{ postsError }}</div>
        <ul v-else class="list">
          <li v-for="p in myPosts" :key="p.id" class="list-item" @click="$router.push(`/posts/${p.id}`)" role="button" tabindex="0">
            <div class="item-title">{{ p.title }}</div>
            <div class="item-meta">#{{ p.id }} · {{ toLocal(p.createdAt) }}</div>
          </li>
          <li v-if="!myPosts.length" class="empty">작성한 글이 없습니다.</li>
        </ul>
      </div>

      <!-- 활동 로그 -->
      <div v-else-if="activeTab==='activity'">
        <div v-if="logsLoading" class="skeleton-list">
          <div v-for="i in 6" :key="i" class="sk-card">
            <div class="sk-title w40"></div>
            <div class="sk-line"></div>
          </div>
        </div>
        <div v-else-if="logsError" class="error-box">{{ logsError }}</div>
        <ul v-else class="list">
          <li v-for="l in logs" :key="l.id || l.createdAt" class="list-item">
            <div class="item-title">{{ l.type || 'activity' }}</div>
            <div class="item-desc">{{ l.detail || l.message || '-' }}</div>
            <div class="item-meta">{{ toLocal(l.createdAt) }}</div>
          </li>
          <li v-if="!logs.length" class="empty">활동 로그가 없습니다.</li>
        </ul>
      </div>

      <!-- 아이디 변경 -->
      <div v-else class="form">
        <p class="hint">현재 아이디: <b>{{ me.activityId }}</b></p>
        <div class="row">
          <input v-model="newActivityId" class="input" placeholder="새 활동 아이디" />
          <button class="btn btn-ghost" :disabled="idLoading" @click="checkNewId">
            {{ idLoading ? '확인 중…' : '중복 확인' }}
          </button>
          <button class="btn btn-primary" :disabled="!idOk" @click="changeActivityId">아이디 변경</button>
        </div>
        <div class="id-msg" :class="{ ok: idOk }">{{ idCheckMsg }}</div>
        <p class="sub">* 아이디 변경 시 URL이 <code>/mypage/새아이디</code>로 바뀝니다.</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.mypage {
  max-width: 760px;
  margin: 24px auto;
  padding: 0 16px;
}
.header .who {
  display: flex; align-items: baseline; gap: 8px; margin-bottom: 8px;
}
.title { margin: 0; font-size: 22px; font-weight: 800; letter-spacing: -0.01em; }
.email { color: #6b7280; font-size: 12px; }

/* 탭 */
.tabs {
  display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 12px;
}
.tab {
  padding: 8px 12px;
  border-radius: 999px;
  border: 1px solid #e5e7eb; /* gray-200 */
  background: #fff;
  color: #374151; /* gray-700 */
  cursor: pointer;
  font-size: 13px;
  transition: background .2s, border-color .2s, color .2s, box-shadow .2s;
}
.tab:hover { background: #f9fafb; border-color: #d1d5db; }
.tab.active {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 6px 16px rgba(37,99,235,0.2);
}

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
}

/* 폼 공통 */
.form { display: grid; gap: 10px; }
.label { font-size: 12px; color: #374151; font-weight: 600; }
.input, .textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff; color: #111827;
  font-size: 14px; outline: none;
  transition: border-color .2s, box-shadow .2s, background .2s;
}
.input:focus, .textarea:focus {
  border-color: #93c5fd;
  box-shadow: 0 0 0 4px rgba(59,130,246,0.15);
}
.actions { display: flex; gap: 8px; justify-content: flex-end; }

/* 버튼 */
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px; line-height: 1;
  cursor: pointer; user-select: none;
  transition: transform .03s, box-shadow .2s, background .2s, color .2s, border-color .2s;
}
.btn:active { transform: translateY(1px); }
.btn-primary {
  background: #2563eb; color: #fff;
  box-shadow: 0 8px 20px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; }
.btn-ghost {
  background: transparent; color: #374151; border: 1px solid #e5e7eb;
}
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }

/* 리스트 */
.list { padding: 0; margin: 0; list-style: none; }
.list-item {
  padding: 12px 0; border-bottom: 1px solid #eee; cursor: pointer;
  transition: color .2s;
}
.list-item:hover .item-title { color: #2563eb; }
.item-title { font-weight: 700; color: #0f172a; }
.item-meta { font-size: 12px; color: #6b7280; margin-top: 4px; }
.item-desc { color: #374151; margin-top: 4px; }

/* 상태 */
.empty { color: #6b7280; text-align: center; padding: 16px 8px; }
.error-box {
  border: 1px solid #fecaca; background: #fff1f2; color: #991b1b;
  padding: 10px 12px; border-radius: 12px; font-size: 13px;
}

/* 아이디 변경 메시지 */
.row { display: flex; gap: 8px; align-items: center; flex-wrap: wrap; }
.id-msg { min-height: 20px; font-size: 13px; color: crimson; }
.id-msg.ok { color: green; }
.hint { margin: 0 0 6px; color: #6b7280; }
.sub { color: #6b7280; font-size: 12px; margin: 4px 0 0; }

/* 스켈레톤 */
@keyframes shimmer { 0% { background-position: -400px 0; } 100% { background-position: 400px 0; } }
.skeleton-set .sk-line,
.skeleton-list .sk-card { position: relative; overflow: hidden; }
.skeleton-set .sk-line::before,
.skeleton-list .sk-card::before {
  content: ""; position: absolute; inset: 0;
  background: linear-gradient(90deg, rgba(0,0,0,0.03) 0px, rgba(0,0,0,0.06) 40px, rgba(0,0,0,0.03) 80px);
  background-size: 600px 100%; animation: shimmer 1.2s infinite linear;
}
.sk-line { height: 16px; width: 90%; margin: 6px 0; background: transparent; }
.sk-line.w60 { width: 60%; }
.sk-area { height: 120px; border-radius: 12px; background: #f3f4f6; margin-top: 8px; }
.skeleton-list { display: grid; gap: 10px; }
.sk-card { border-radius: 14px; padding: 16px; background: #fff; border: 1px solid #eaeaea; }
.sk-title { height: 16px; width: 50%; background: transparent; margin-bottom: 8px; }
.sk-title.w40 { width: 40%; }
.sk-meta, .sk-line { height: 12px; width: 30%; background: transparent; }

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card { --card-bg: #0b1220; --card-border: #1f2937; color: #e5e7eb; }
  .email, .hint, .sub { color: #9ca3af; }
  .tab { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .tab:hover { background: #0f172a; border-color: #475569; }
  .input, .textarea { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .input:focus, .textarea:focus { border-color: #60a5fa; box-shadow: 0 0 0 4px rgba(96,165,250,0.2); }
  .btn-ghost { border-color: #334155; color: #e5e7eb; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
  .list-item { border-color: #1f2937; }
  .item-title { color: #e5e7eb; }
  .item-desc { color: #cbd5e1; }
  .sk-card { background: #0b1220; border-color: #1f2937; }
}
</style>

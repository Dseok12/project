<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import client from '@/api/client'

const route = useRoute()
const router = useRouter()
const store = useStore()

const post = ref(null)
const loading = ref(false)
const deleting = ref(false)
const errorMsg = ref('')

/** 작성자 아이디 안전 추출 */
const getAuthorId = (p) =>
  p?.authorActivityId ??
  p?.authorId ??
  p?.author?.activityId ??
  p?.activityId ??
  null

/** 아이디 마스킹 */
const maskId = (id) => {
  if (!id) return '익명'
  const s = String(id)
  if (s.length <= 2) return s
  return s[0] + '*'.repeat(s.length - 2) + s[s.length - 1]
}

const maskedAuthor = computed(() => maskId(getAuthorId(post.value)))
const meAid = computed(() => store.state.activityId)
const isOwner = computed(() => {
  const aid = getAuthorId(post.value)
  return !!aid && !!meAid.value && aid === meAid.value
})

const goEdit = () => router.push(`/posts/${route.params.id}/edit`)
const goList = () => router.push({ path: '/posts', query: router.currentRoute.value.query })

const doDelete = async () => {
  if (!confirm('정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) return
  try {
    deleting.value = true
    await client.delete(`/posts/${route.params.id}`)
    alert('삭제되었습니다.')
    goList()
  } catch (e) {
    console.error(e)
    const st = e?.response?.status
    if (st === 403) alert('삭제 권한이 없습니다.')
    else if (st === 404) alert('게시글을 찾을 수 없습니다.')
    else if (st === 401) alert('로그인이 필요합니다.')
    else alert('삭제에 실패했습니다.')
  } finally {
    deleting.value = false
  }
}

onMounted(async () => {
  try {
    loading.value = true
    const { data } = await client.get(`/posts/${route.params.id}`)
    post.value = data
  } catch (e) {
    console.error(e)
    errorMsg.value = '게시글을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="post-detail">
    <!-- 헤더: 뒤로가기 -->
    <div class="topbar">
      <button class="btn btn-ghost" @click="goList" aria-label="목록으로">← 목록</button>
    </div>

    <!-- 로딩 -->
    <div v-if="loading" class="card">
      <div class="skeleton title"></div>
      <div class="meta">
        <span class="skeleton chip"></span>
        <span class="skeleton small"></span>
      </div>
      <div class="skeleton body"></div>
      <div class="skeleton body"></div>
    </div>

    <!-- 에러 -->
    <div v-else-if="errorMsg" class="error-box">
      {{ errorMsg }}
    </div>

    <!-- 내용 -->
    <div v-else-if="post" class="card">
      <h2 class="title">{{ post.title }}</h2>

      <div class="meta">
        <span class="chip" title="작성자">{{ maskedAuthor }}</span>
        <span class="dot">•</span>
        <span class="time" :title="new Date(post.createdAt).toISOString()">
          {{ new Date(post.createdAt).toLocaleString() }}
        </span>
      </div>

      <article class="content" v-text="post.content"></article>

      <div class="actions">
        <button class="btn btn-ghost" @click="goList">목록</button>
        <template v-if="isOwner">
          <button class="btn btn-primary" @click="goEdit">수정</button>
          <button class="btn btn-danger" @click="doDelete" :disabled="deleting">
            {{ deleting ? '삭제 중…' : '삭제' }}
          </button>
        </template>
      </div>
    </div>
  </section>
</template>

<style scoped>
.post-detail {
  max-width: 760px;
  margin: 24px auto;
  padding: 0 16px;
}

.topbar {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 16px;
  padding: 20px 20px 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
}

/* 제목 */
.title {
  margin: 0 0 8px;
  line-height: 1.25;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.01em;
}

/* 메타 */
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280; /* slate-500 */
  font-size: 12px;
  margin-bottom: 12px;
}

.chip {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
  background: #f1f5f9; /* slate-100 */
  color: #334155;      /* slate-700 */
  border: 1px solid #e2e8f0; /* slate-200 */
}

.dot { opacity: .6; }
.time { opacity: .9; }

/* 본문 */
.content {
  margin-top: 12px;
  white-space: pre-line;
  line-height: 1.7;
  font-size: 15px;
  color: #111827; /* gray-900 */
}

/* 버튼 */
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  transition: transform .03s ease, box-shadow .2s ease, background .2s ease, color .2s ease, border-color .2s ease;
  user-select: none;
}
.btn:active { transform: translateY(1px); }

.btn-ghost {
  background: transparent;
  border-color: #e5e7eb; /* gray-200 */
  color: #374151;        /* gray-700 */
}
.btn-ghost:hover {
  background: #f9fafb;   /* gray-50 */
  border-color: #d1d5db; /* gray-300 */
}

.btn-primary {
  background: #2563eb;   /* blue-600 */
  color: #fff;
  box-shadow: 0 6px 16px rgba(37,99,235,0.25);
}
.btn-primary:hover {
  background: #1d4ed8;   /* blue-700 */
}

.btn-danger {
  background: #ef4444;   /* red-500 */
  color: #fff;
  box-shadow: 0 6px 16px rgba(239,68,68,0.25);
}
.btn-danger:hover {
  background: #dc2626;   /* red-600 */
}

/* 액션 바 */
.actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

/* 에러 */
.error-box {
  border: 1px solid #fecaca;      /* red-200 */
  background: #fff1f2;            /* rose-50 */
  color: #991b1b;                 /* red-800 */
  padding: 12px 14px;
  border-radius: 12px;
}

/* 스켈레톤 로딩 */
@keyframes shimmer {
  0% { background-position: -400px 0; }
  100% { background-position: 400px 0; }
}
.skeleton {
  border-radius: 10px;
  background: #f3f4f6; /* gray-100 */
  background-image: linear-gradient(90deg, #f3f4f6 0px, #e5e7eb 40px, #f3f4f6 80px);
  background-size: 600px 100%;
  animation: shimmer 1.2s infinite linear;
}
.skeleton.title { height: 28px; width: 70%; margin-bottom: 10px; }
.skeleton.small { height: 16px; width: 180px; }
.skeleton.chip { height: 22px; width: 90px; border-radius: 999px; }
.skeleton.body { height: 14px; width: 100%; margin-top: 12px; }
.skeleton.body + .skeleton.body { width: 85%; }

/* 다크 모드 대응 */
@media (prefers-color-scheme: dark) {
  .card {
    --card-bg: #0b1220;
    --card-border: #1f2937;
    color: #e5e7eb;
  }
  .content { color: #e5e7eb; }
  .chip { background: #0f172a; border-color: #1f2937; color: #e2e8f0; }
  .btn-ghost { border-color: #334155; color: #e5e7eb; }
  .btn-ghost:hover { background: #0b1220; border-color: #475569; }
  .skeleton { background: #111827; background-image: linear-gradient(90deg, #111827 0px, #1f2937 40px, #111827 80px); }
}
</style>

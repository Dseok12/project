<script setup>
import { onMounted, ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import client from '@/api/client'

const store = useStore()
const route = useRoute()
const router = useRouter()

const isAuthed = computed(() => store.getters.isAuthed)

const posts = ref([])
const loading = ref(false)
const errorMsg = ref('')

// ===== 페이징 상태 =====
const pageSize = 20
const currentPage = ref(Number(route.query.page || 1)) // UI는 1-based
const totalPages = ref(1)
const totalElements = ref(0)

// 작성자 필드 안전 추출
const getAuthorId = (p) => (
  p?.authorActivityId ??
  p?.authorId ??
  p?.author?.activityId ??
  p?.activityId ??
  null
)

// 아이디 마스킹
const maskId = (id) => {
  if (!id) return '익명'
  const s = String(id)
  if (s.length <= 2) return s
  return s[0] + '*'.repeat(s.length - 2) + s[s.length - 1]
}

// 페이지 이동 헬퍼(라우터 쿼리 동기화)
const goPage = (page) => {
  const safe = Math.max(1, Math.min(page, totalPages.value || 1))
  if (safe === currentPage.value) return
  router.push({ path: '/posts', query: { page: safe } })
}

// 응답 파싱: Page<T> / List<T> 호환
const parsePostsResponse = (data) => {
  if (data && Array.isArray(data.content)) {
    return {
      items: data.content,
      totalPages: data.totalPages ?? 1,
      totalElements: data.totalElements ?? data.content.length,
      pageNumber: (data.number ?? 0) + 1, // 0-based -> 1-based
    }
  }
  if (Array.isArray(data)) {
    return {
      items: data,
      totalPages: 1,
      totalElements: data.length,
      pageNumber: 1,
    }
  }
  return { items: [], totalPages: 1, totalElements: 0, pageNumber: 1 }
}

const fetchPosts = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const { data } = await client.get('/posts', {
      params: {
        page: Math.max(0, currentPage.value - 1), // 서버는 0-based
        size: pageSize,
        sort: 'createdAt,desc',
      }
    })
    const parsed = parsePostsResponse(data)
    posts.value = parsed.items
    totalPages.value = parsed.totalPages
    totalElements.value = parsed.totalElements
    currentPage.value = parsed.pageNumber

    // 쿼리 범위를 넘어가면 마지막 페이지로 보정
    if (currentPage.value > totalPages.value) {
      goPage(totalPages.value)
    }
  } catch (e) {
    console.error('fetchPosts error', e)
    errorMsg.value = e?.response?.status
      ? `목록 로드 실패 (HTTP ${e.response.status})`
      : '게시물을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

// URL 쿼리(page) 변경 시 다시 로드
watch(
  () => route.query.page,
  (v) => {
    const p = Number(v || 1)
    if (!Number.isFinite(p) || p < 1) return
    currentPage.value = p
    fetchPosts()
  }
)

onMounted(fetchPosts)
</script>

<template>
  <section class="posts">
    <header class="header">
      <h2 class="title">게시물(어노이)</h2>
      <router-link v-if="isAuthed" to="/posts/new">
        <button class="btn btn-primary">새 글</button>
      </router-link>
    </header>

    <!-- 로딩 -->
    <div v-if="loading" class="card-list">
      <div v-for="i in 6" :key="i" class="card skeleton">
        <div class="sk-title"></div>
        <div class="sk-meta"></div>
        <div class="sk-line"></div>
      </div>
    </div>

    <!-- 에러 -->
    <div v-else-if="errorMsg" class="error-box">
      {{ errorMsg }}
    </div>

    <!-- 목록 -->
    <template v-else>
      <div class="card-list">
        <article
          v-for="p in posts"
          :key="p.id"
          class="card item"
          @click="$router.push(`/posts/${p.id}`)"
          role="button"
          tabindex="0"
        >
          <h3 class="item-title">{{ p.title }}</h3>
          <div class="item-meta">
            <span class="badge">#{{ p.id }}</span>
            <span class="dot">•</span>
            <span class="time">{{ new Date(p.createdAt).toLocaleString() }}</span>
            <span class="dot">•</span>
            <span class="author" title="작성자">
              {{ maskId(getAuthorId(p)) }}
            </span>
          </div>
        </article>
      </div>

      <div v-if="!loading && posts.length === 0" class="empty">
        아직 게시물이 없습니다.
      </div>

      <!-- 페이지네이션 -->
      <nav v-if="totalPages > 1" class="pgn">
        <button class="pgn-btn" @click="goPage(currentPage-1)" :disabled="currentPage<=1">이전</button>

        <template v-for="n in totalPages" :key="n">
          <button
            v-if="Math.abs(n - currentPage) <= 3 || n===1 || n===totalPages"
            class="pgn-btn"
            :class="{ active: n===currentPage }"
            @click="goPage(n)"
            :disabled="n===currentPage"
          >
            {{ n }}
          </button>
          <span v-else-if="n===currentPage-4 || n===currentPage+4" class="pgn-ellipsis">…</span>
        </template>

        <button class="pgn-btn" @click="goPage(currentPage+1)" :disabled="currentPage>=totalPages">다음</button>

        <span class="pgn-total">총 {{ totalElements.toLocaleString() }}개</span>
      </nav>
    </template>
  </section>
</template>

<style scoped>
.posts {
  max-width: 760px;
  margin: 24px auto;
  padding: 0 16px;
}

.header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}
.title {
  margin: 0;
  margin-right: auto;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.01em;
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
.btn-primary {
  background: #2563eb;   /* blue-600 */
  color: #fff;
  box-shadow: 0 6px 16px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; } /* blue-700 */

/* 카드 리스트 */
.card-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
}

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 14px;
  padding: 16px 16px 14px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.05);
  cursor: pointer;
}

/* 아이템 카드 */
.item {
  transition: transform .06s ease, box-shadow .2s ease, border-color .2s ease;
}
.item:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 28px rgba(0,0,0,0.08);
  border-color: #dbeafe; /* blue-100 */
}
.item:focus { outline: 2px solid #93c5fd; } /* focus-visible 보조 */

/* 아이템 타이틀/메타 */
.item-title {
  margin: 0 0 6px;
  font-size: 16px;
  font-weight: 700;
  color: #0f172a; /* slate-900 */
}
.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280; /* slate-500 */
  font-size: 12px;
}
.badge {
  background: #f1f5f9;  /* slate-100 */
  color: #334155;       /* slate-700 */
  border: 1px solid #e2e8f0;
  padding: 1px 8px;
  border-radius: 999px;
  font-weight: 600;
}
.dot { opacity: .6; }
.time { opacity: .9; }
.author { font-weight: 600; color: #334155; }

/* 빈 상태 */
.empty {
  color: #6b7280;
  text-align: center;
  padding: 16px 8px;
}

/* 에러 */
.error-box {
  border: 1px solid #fecaca;      /* red-200 */
  background: #fff1f2;            /* rose-50 */
  color: #991b1b;                 /* red-800 */
  padding: 12px 14px;
  border-radius: 12px;
}

/* 페이지네이션 */
.pgn {
  display: flex;
  gap: 6px;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 14px;
}
.pgn-btn {
  min-width: 36px;
  height: 32px;
  padding: 0 10px;
  border-radius: 8px;
  border: 1px solid #e5e7eb; /* gray-200 */
  background: #fff;
  color: #374151; /* gray-700 */
  cursor: pointer;
  transition: background .2s ease, border-color .2s ease, color .2s ease;
}
.pgn-btn:hover { background: #f9fafb; border-color: #d1d5db; }
.pgn-btn:disabled {
  background: #f3f4f6; color: #9ca3af; cursor: not-allowed;
}
.pgn-btn.active {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 6px 16px rgba(37,99,235,0.25);
}
.pgn-ellipsis { color: #9ca3af; padding: 0 2px; }
.pgn-total { margin-left: 8px; font-size: 12px; color: #6b7280; }

/* 스켈레톤 */
@keyframes shimmer { 0% { background-position: -400px 0; } 100% { background-position: 400px 0; } }
.skeleton {
  position: relative;
  overflow: hidden;
}
.skeleton::before {
  content: "";
  position: absolute; inset: 0;
  background: linear-gradient(90deg, rgba(0,0,0,0.03) 0px, rgba(0,0,0,0.06) 40px, rgba(0,0,0,0.03) 80px);
  background-size: 600px 100%;
  animation: shimmer 1.2s infinite linear;
}
.sk-title { height: 18px; width: 60%; background: transparent; margin-bottom: 8px; }
.sk-meta  { height: 12px; width: 30%; background: transparent; margin-bottom: 10px; }
.sk-line  { height: 12px; width: 90%; background: transparent; }

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card {
    --card-bg: #0b1220;
    --card-border: #1f2937;
    color: #e5e7eb;
  }
  .item-title { color: #e5e7eb; }
  .item:hover { border-color: #1e293b; box-shadow: 0 12px 32px rgba(0,0,0,0.5); }
  .badge { background: #0f172a; border-color: #1f2937; color: #e2e8f0; }
  .pgn-btn { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .pgn-btn:hover { background: #0f172a; border-color: #475569; }
  .pgn-btn:disabled { background: #0b1220; color: #64748b; }
}
</style>

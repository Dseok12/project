<!-- frontend/src/pages/Notices.vue -->
<template>
  <section class="notice-wrap">
    <!-- 헤더 배너 -->
    <header class="hero">
      <div class="hero-inner">
        <h2 class="title">공지사항</h2>
        <p class="subtitle">운영 공지와 안내를 확인하세요.</p>

        <!-- 관리자만 노출되는 새 공지 버튼 (선택) -->
        <router-link
          v-if="isAdmin"
          to="/admin"
          class="btn btn-primary new-btn"
        >새 공지 등록</router-link>
      </div>
    </header>

    <!-- 테이블 카드 -->
    <div class="card">
      <!-- 로딩 -->
      <div v-if="loading" class="skeleton-table">
        <div class="row" v-for="i in 6" :key="i">
          <span class="sk sk-num"></span>
          <span class="sk sk-admin"></span>
          <span class="sk sk-title"></span>
          <span class="sk sk-date"></span>
        </div>
      </div>

      <!-- 오류 -->
      <div v-else-if="error" class="error-box">{{ error }}</div>

      <!-- 비어있음 -->
      <div v-else-if="!rows.length" class="empty">
        아직 등록된 공지사항이 없습니다.
      </div>

      <!-- 테이블 -->
      <div v-else class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th class="col-num">번호</th>
              <th class="col-admin">관리자</th>
              <th class="col-title">제목</th>
              <th class="col-date">작성날짜</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(n, idx) in rows"
              :key="n.id"
              class="row"
            >
              <td class="col-num" :title="String(numberOf(idx))">{{ numberOf(idx) }}</td>
              <td class="col-admin">
                <span class="chip chip-admin" :title="n.authorActivityId || '관리자'">
                  {{ mask(n.authorActivityId) || '관리자' }}
                </span>
              </td>
              <td class="col-title">
                <router-link
                  class="title-link"
                  :to="`/posts/${n.id}`"
                  :title="n.title"
                >
                  <span class="badge">공지</span>
                  <span class="txt">{{ n.title }}</span>
                </router-link>
              </td>
              <td class="col-date" :title="iso(n.createdAt)">{{ local(n.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="totalPages>1" class="pager">
        <button class="p-btn" :disabled="page===0" @click="go(0)">≪</button>
        <button class="p-btn" :disabled="page===0" @click="go(page-1)">‹</button>
        <span class="p-info">{{ page+1 }} / {{ totalPages }}</span>
        <button class="p-btn" :disabled="page>=totalPages-1" @click="go(page+1)">›</button>
        <button class="p-btn" :disabled="page>=totalPages-1" @click="go(totalPages-1)">≫</button>

        <select class="p-size" v-model.number="size" @change="reload()">
          <option :value="10">10개</option>
          <option :value="20">20개</option>
          <option :value="30">30개</option>
        </select>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import client from '@/api/client'
import useAuth from '@/composables/useAuth'

const { isAdmin } = useAuth()

/** 마스킹: a****1 같은 형태 */
const mask = (id) => {
  if (!id) return ''
  const s = String(id)
  if (s.length <= 2) return s
  return `${s[0]}${'*'.repeat(Math.max(1, s.length - 2))}${s[s.length - 1]}`
}

const page = ref(0)
const size = ref(10)
const total = ref(0)
const rows = ref([])
const loading = ref(false)
const error = ref('')

const totalPages = computed(() =>
  Math.max(1, Math.ceil(total.value / Math.max(1, size.value)))
)

const numberOf = (idx) => {
  // 페이지 전체 번호 (최신이 위, 큰 번호부터)
  const start = total.value - page.value * size.value
  return Math.max(1, start - idx)
}
const iso = (d) => (d ? new Date(d).toISOString() : '')
const local = (d) => (d ? new Date(d).toLocaleDateString() : '')

const fetchNotices = async () => {
  loading.value = true
  error.value = ''
  try {
    // Spring Pageable과 호환
    const { data } = await client.get('/posts', {
      params: { notice: true, page: page.value, size: size.value, sort: 'createdAt,desc' }
    })

    // 반환 형태 호환 처리: Page<T> 또는 Array<T>
    if (Array.isArray(data)) {
      rows.value = data
      total.value = data.length
    } else if (data?.content) {
      rows.value = data.content
      total.value = data.totalElements ?? rows.value.length
    } else {
      rows.value = data ?? []
      total.value = rows.value.length
    }
  } catch (e) {
    console.error(e)
    error.value = '공지사항을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const go = (p) => {
  page.value = Math.min(Math.max(0, p), totalPages.value - 1)
  fetchNotices()
}
const reload = () => {
  page.value = 0
  fetchNotices()
}

watch(size, () => reload())

onMounted(fetchNotices)
</script>

<style scoped>
/* 레이아웃 */
.notice-wrap { max-width: 960px; margin: 24px auto; padding: 0 16px; }

/* 배너 */
.hero {
  background: linear-gradient(180deg, #f97316, #f59e0b);
  border: 1px solid #fdba74;
  border-radius: 16px;
  color: #fff;
  box-shadow: 0 12px 28px rgba(249,115,22,0.25);
  margin-bottom: 14px;
}
.hero-inner {
  display: flex;
  align-items: center;justify-content: space-between;
  gap: 12px;
  padding: 18px 18px 16px;
  align-items: center;
}
.title { margin: 0; font-size: 20px; font-weight: 900; letter-spacing: -0.01em; }
.subtitle { margin: 4px 0 0; font-size: 13px; opacity: .95; }
.new-btn { justify-self: end; }

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
  overflow: hidden;
}

/* 테이블 */
.table-wrap { overflow-x: auto; }
.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}
.table thead th {
  background: #f8fafc;
  color: #334155;
  font-size: 12px;
  font-weight: 800;
  text-align: left;
  padding: 12px 14px;
  border-bottom: 1px solid #e5e7eb;
  letter-spacing: .02em;
}
.table tbody td {
  text-align: left;
  padding: 14px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
  font-size: 14px;
  color: #0f172a;
}
.table tbody tr:hover td { background: #fafafa; }

/* 컬럼 폭 */
.col-num   { width: 86px; text-align: center; }
.col-admin { width: 160px; }
.col-date  { width: 160px; text-align: center; }

/* 제목 링크 */
.title-link {
  display: inline-flex; align-items: center; gap: 8px;
  color: #0f172a; text-decoration: none; font-weight: 700;
}
.title-link .txt {
  max-width: 560px;
  display: inline-block;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.title-link:hover .txt { text-decoration: underline; }
.badge {
  display: inline-block;
  font-size: 11px; font-weight: 800;
  padding: 2px 6px; border-radius: 999px;
  color: #b45309; background: #fffbeb; border: 1px solid #fde68a;
}

/* 관리자 칩 */
.chip {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 4px 8px; border-radius: 999px;
  font-size: 12px; font-weight: 700;
  border: 1px solid #e5e7eb; background: #f8fafc; color: #334155;
}
.chip-admin::before {
  content: "운영"; font-size: 10px; font-weight: 900;
  padding: 2px 6px; border-radius: 999px;
  color: #1d4ed8; background: #dbeafe; border: 1px solid #bfdbfe;
}

/* 페이지네이션 */
.pager {
  display: flex; align-items: center; gap: 8px;
  padding: 12px; justify-content: flex-end;
}
.p-btn {
  min-width: 34px; height: 32px; padding: 0 8px;
  border: 1px solid #e5e7eb; background: #fff; border-radius: 8px;
  cursor: pointer;
}
.p-btn:disabled { opacity: .45; cursor: default; }
.p-info { font-size: 13px; color: #6b7280; margin: 0 6px; }
.p-size {
  height: 32px; border-radius: 8px; border: 1px solid #e5e7eb; padding: 0 8px;
}

/* 로딩 스켈레톤 */
.skeleton-table { padding: 8px 12px; }
.skeleton-table .row {
  display: grid; grid-template-columns: 86px 160px 1fr 160px; gap: 0;
  padding: 10px 2px; align-items: center;
}
.sk {
  display: block; height: 18px; border-radius: 8px;
  background: #f3f4f6;
  background-image: linear-gradient(90deg, #f3f4f6 0px, #e5e7eb 40px, #f3f4f6 80px);
  background-size: 600px 100%; animation: shimmer 1.2s infinite linear;
}
.sk-num { width: 52px; justify-self: center; }
.sk-admin { width: 120px; }
.sk-title { width: 92%; height: 20px; }
.sk-date { width: 120px; justify-self: center; }

@keyframes shimmer { 0%{background-position:-400px 0} 100%{background-position:400px 0} }

/* 상태 */
.error-box {
  padding: 14px 16px; color: #991b1b;
  background: #fff1f2; border-top: 1px solid #fecaca;
}
.empty {
  padding: 24px 16px; color: #6b7280; text-align: center;
}

/* 버튼 공통 */
.btn {
  appearance: none; border: 1px solid transparent; border-radius: 10px;
  padding: 10px 14px; font-size: 14px; line-height: 1; cursor: pointer;
  transition: transform .03s ease, background .2s ease, color .2s ease, border-color .2s ease, box-shadow .2s ease;
  user-select: none;
}
.btn:active { transform: translateY(1px); }
.btn-primary {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 8px 18px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; border-color: #1d4ed8; }

/* 반응형: 모바일에서는 관리자/날짜를 아래로 내려 카드처럼 보이게 */
@media (max-width: 640px) {
  .table thead { display: none; }
  .table tbody tr { display: grid; grid-template-columns: 1fr; gap: 4px; padding: 10px 8px; }
  .table tbody td { border: 0; padding: 4px 8px; text-align: left; } /* ← fix */
  .col-num   { order: 1; font-weight: 800; color: #111827; }
  .col-title { order: 2; }
  .col-admin { order: 3; }
  .col-date  { order: 4; color: #6b7280; }
  .title-link .txt { max-width: 100%; }
}
@media (prefers-color-scheme: dark) {
  .hero { border-color: #b45309; }
  .card { --card-bg:#0b1220; --card-border:#1f2937; color:#e5e7eb; }
  .table thead th { background:#0f172a; color:#e5e7eb; border-color:#1f2937; }
  .table tbody td { color:#e5e7eb; border-color:#1f2937; }
  .badge { color:#f59e0b; background:#1f2937; border-color:#374151; }
  .chip { background:#0b1220; color:#e2e8f0; border-color:#334155; }
  .chip-admin::before { color:#93c5fd; background:#0b1220; border-color:#334155; }
  .p-btn { background:#0b1220; border-color:#334155; color:#e5e7eb; }
  .p-size { background:#0b1220; border-color:#334155; color:#e5e7eb; }
  .skeleton-table .sk { background:#111827; background-image: linear-gradient(90deg,#111827 0,#1f2937 40px,#111827 80px); }
}
</style>

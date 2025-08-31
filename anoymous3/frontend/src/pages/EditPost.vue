<script setup>
import { onMounted, onBeforeUnmount, ref, computed, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import client from '@/api/client'

const route = useRoute()
const router = useRouter()

const MAX = 100000000 // 1e8

const title = ref('')
const content = ref('')
const loading = ref(false)
const saving = ref(false)
const errorMsg = ref('')
const dirty = ref(false)

const textareaRef = ref(null)

const trimmedTitle = computed(() => title.value.trim())
const trimmedContent = computed(() => content.value.trim())
const canSave = computed(() => !saving.value && trimmedTitle.value.length > 0 && trimmedContent.value.length > 0)

const contentLen = computed(() => content.value.length)
const contentPct = computed(() => Math.min(100, Math.floor((contentLen.value / MAX) * 100)))

function autoResize() {
  const el = textareaRef.value
  if (!el) return
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 1200) + 'px' // 너무 커지지 않게 상한선
}

const load = async () => {
  try {
    loading.value = true
    const { data } = await client.get(`/posts/${route.params.id}`)
    title.value = data?.title || ''
    content.value = data?.content || ''
    await nextTick()
    autoResize()
  } catch (e) {
    console.error(e)
    errorMsg.value = '게시글을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const save = async () => {
  if (!canSave.value) return
  try {
    saving.value = true
    await client.put(`/posts/${route.params.id}`, {
      title: trimmedTitle.value,
      content: content.value   // 내용은 공백 포함 그대로 저장
    })
    dirty.value = false
    alert('수정되었습니다.')
    router.push(`/posts/${route.params.id}`)
  } catch (e) {
    console.error(e)
    const st = e?.response?.status
    const msg =
      st === 403 ? '수정 권한이 없습니다.'
      : st === 401 ? '로그인이 필요합니다.'
      : (e?.response?.data?.error || '수정에 실패했습니다.')
    alert(msg)
  } finally {
    saving.value = false
  }
}

function goBack() {
  if (dirty.value && !confirm('변경사항이 저장되지 않았습니다. 나가시겠어요?')) return
  router.back()
}

// 변경 감지
watch([title, content], () => { dirty.value = true })
watch(content, () => nextTick(autoResize))

// 단축키: Ctrl/Cmd + S
function onKeydown(e) {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
    e.preventDefault()
    save()
  }
}

// 닫을 때 경고
function beforeUnload(e) {
  if (!dirty.value) return
  e.preventDefault()
  e.returnValue = ''
}

onMounted(() => {
  load()
  window.addEventListener('keydown', onKeydown)
  window.addEventListener('beforeunload', beforeUnload)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
  window.removeEventListener('beforeunload', beforeUnload)
})
</script>

<template>
  <section class="edit-wrap">
    <!-- 상단 툴바 -->
    <header class="topbar">
      <button class="btn btn-ghost" @click="goBack" aria-label="뒤로">←</button>
      <h1 class="title">게시글 수정</h1>
      <div class="grow"></div>
      <button class="btn btn-primary" :disabled="!canSave" @click="save">
        <span v-if="saving" class="spinner"></span>
        {{ saving ? '저장 중...' : '저장' }}
      </button>
    </header>

    <!-- 카드 -->
    <div class="card">
      <!-- 로딩 스켈레톤 -->
      <div v-if="loading" class="skeleton">
        <div class="sk sk-title"></div>
        <div class="sk sk-line"></div>
        <div class="sk sk-line short"></div>
        <div class="sk sk-area"></div>
      </div>

      <!-- 에러 -->
      <div v-else-if="errorMsg" class="error-box">{{ errorMsg }}</div>

      <!-- 폼 -->
      <form v-else class="form" @submit.prevent="save">
        <label class="label" for="title">제목</label>
        <input
          id="title"
          v-model="title"
          class="input"
          type="text"
          placeholder="멋진 제목을 입력하세요"
          maxlength="255"
          autofocus
        />
        <p class="hint">
          {{ trimmedTitle.length }}/255
        </p>

        <label class="label" for="content">내용</label>
        <textarea
          id="content"
          ref="textareaRef"
          v-model="content"
          class="textarea"
          rows="12"
          :maxlength="MAX"
          placeholder="내용을 입력하세요"
          style="white-space:pre-wrap"
        ></textarea>

        <div class="meter">
          <div class="bar" :style="{ width: contentPct + '%' }"></div>
        </div>
        <div class="meta">
          <span>글자 수: <b>{{ contentLen.toLocaleString() }}</b> / {{ MAX.toLocaleString() }}</span>
          <span class="dot">•</span>
          <span class="muted">Ctrl/Cmd + S 로 저장</span>
        </div>

        <div class="actions">
          <button type="button" class="btn btn-ghost" @click="goBack">취소</button>
          <button type="submit" class="btn btn-primary" :disabled="!canSave">
            {{ saving ? '저장 중...' : '저장하기' }}
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>
/* 레이아웃 */
.edit-wrap {
  max-width: 860px;
  margin: 20px auto 40px;
  padding: 0 16px;
}

/* 상단바 */
.topbar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}
.title {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: -0.01em;
  margin: 0;
}
.grow { flex: 1; }

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #e5e7eb);
  border-radius: 18px;
  padding: 20px;
  box-shadow: 0 10px 26px rgba(0,0,0,0.06);
}

/* 폼 */
.form { display: grid; gap: 8px; }
.label {
  font-weight: 700;
  font-size: 13px;
  color: #334155;
  margin-top: 6px;
}
.input, .textarea {
  width: 100%;
  border: 1px solid #e5e7eb;
  background: #fff;
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 14px;
  outline: none;
  transition: border-color .2s, box-shadow .2s, background .2s;
}
.input::placeholder, .textarea::placeholder { color: #94a3b8; }
.input:focus, .textarea:focus {
  border-color: #93c5fd; /* blue-300 */
  box-shadow: 0 0 0 4px rgba(59,130,246,.15); /* blue-500/15 */
  background: #fff;
}
.textarea { line-height: 1.6; resize: none; min-height: 180px; }

/* 힌트/메타 */
.hint {
  margin: 0 0 4px;
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}
.meter {
  height: 6px;
  background: #f1f5f9;
  border-radius: 999px;
  overflow: hidden;
  margin-top: 6px;
}
.bar {
  height: 100%;
  width: 0%;
  background: linear-gradient(90deg, #60a5fa, #2563eb);
  transition: width .2s ease;
}
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
  margin-top: 6px;
}
.meta .muted { opacity: .9; }
.dot { opacity: .6; }

/* 버튼 */
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 12px;
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
  border-color: #e5e7eb;
  color: #374151;
}
.btn-ghost:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}
.btn-primary {
  background: #2563eb;
  color: #fff;
  box-shadow: 0 6px 16px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; }
.btn[disabled] {
  cursor: not-allowed;
  opacity: .6;
  box-shadow: none;
}

/* 액션 */
.actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

/* 에러 */
.error-box {
  border: 1px solid #fecaca;
  background: #fff1f2;
  color: #991b1b;
  padding: 12px 14px;
  border-radius: 12px;
}

/* 로딩 스켈레톤 */
@keyframes shimmer {
  0% { background-position: -400px 0; }
  100% { background-position: 400px 0; }
}
.skeleton .sk {
  border-radius: 10px;
  background: #f3f4f6;
  background-image: linear-gradient(90deg, #f3f4f6 0px, #e5e7eb 40px, #f3f4f6 80px);
  background-size: 600px 100%;
  animation: shimmer 1.2s infinite linear;
}
.sk-title { height: 28px; width: 70%; margin-bottom: 16px; }
.sk-line { height: 14px; width: 100%; margin-top: 8px; }
.sk-line.short { width: 60%; }
.sk-area { height: 160px; width: 100%; margin-top: 14px; }

/* 스피너 */
.spinner {
  display: inline-block;
  width: 16px; height: 16px;
  border: 2px solid rgba(255,255,255,.6);
  border-top-color: #fff;
  border-radius: 50%;
  margin-right: 6px;
  animation: spin .7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card {
    --card-bg: #0b1220;
    --card-border: #1f2937;
    color: #e5e7eb;
  }
  .label { color: #cbd5e1; }
  .input, .textarea { background: #0f172a; border-color: #334155; color: #e5e7eb; }
  .input::placeholder, .textarea::placeholder { color: #64748b; }
  .input:focus, .textarea:focus { border-color: #60a5fa; box-shadow: 0 0 0 4px rgba(59,130,246,.25); }
  .btn-ghost { border-color: #334155; color: #e5e7eb; }
  .btn-ghost:hover { background: #0b1220; border-color: #475569; }
  .meter { background: #0f172a; }
}
</style>

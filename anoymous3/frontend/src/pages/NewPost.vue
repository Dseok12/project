<script setup>
import { ref, computed } from 'vue'
import client from '@/api/client'
import { useRouter } from 'vue-router'

const router = useRouter()

const title = ref('')
const content = ref('')
const saving = ref(false)

// 표시용(선택) — 너무 길면 경고색
const TITLE_MAX = 100
const CONTENT_MAX = 5000
const titleLen = computed(() => title.value.length)
const contentLen = computed(() => content.value.length)
const titleOver = computed(() => titleLen.value > TITLE_MAX)
const contentOver = computed(() => contentLen.value > CONTENT_MAX)

// 저장 가능 여부
const canSubmit = computed(() =>
  !!title.value.trim() &&
  !!content.value.trim() &&
  !saving.value
)

const createPost = async () => {
  if (!title.value.trim() || !content.value.trim()) {
    alert('제목/내용을 입력하세요.')
    return
  }
  try {
    saving.value = true
    const { data } = await client.post('/posts', {
      title: title.value,
      content: content.value
    })
    router.push(`/posts/${data.id}`)
  } catch (e) {
    console.error(e)
    alert('저장에 실패했습니다.')
  } finally {
    saving.value = false
  }
}

const cancel = () => router.back()

// 단축키: Ctrl/⌘+Enter 저장, Esc 취소
const onKeydown = (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
    e.preventDefault()
    createPost()
  } else if (e.key === 'Escape') {
    e.preventDefault()
    cancel()
  }
}
</script>

<template>
  <section class="newpost" @keydown="onKeydown">
    <div class="bg"></div>

    <header class="head">
      <h2 class="title">새 게시물</h2>
      <p class="sub">
        엔터로 줄바꿈한 그대로 저장됩니다.
        <span class="kbd">Ctrl/⌘+Enter</span> 저장 · <span class="kbd">Esc</span> 취소
      </p>
    </header>

    <div class="posts grid">
      <!-- 에디터 카드 -->
      <form class="card editor" @submit.prevent="createPost">
        <label class="label" for="ptitle">제목</label>
        <input
          id="ptitle"
          v-model="title"
          class="input"
          :class="{ danger: titleOver }"
          placeholder="제목을 입력하세요"
          autocomplete="off"
        />
        <div class="meta">
          <span :class="{ warn: titleOver }">{{ titleLen }} / {{ TITLE_MAX }}</span>
        </div>

        <label class="label" for="pcontent" style="margin-top:8px;">내용</label>
        <textarea
          id="pcontent"
          v-model="content"
          class="textarea"
          :class="{ danger: contentOver }"
          rows="12"
          placeholder="내용을 입력하세요(엔터=줄바꿈)"
        />
        <div class="meta">
          <span :class="{ warn: contentOver }">{{ contentLen.toLocaleString() }} / {{ CONTENT_MAX.toLocaleString() }}</span>
        </div>

        <div class="actions">
          <button type="submit" class="btn btn-primary" :disabled="!canSubmit">
            {{ saving ? '저장 중…' : '완료' }}
          </button>
          <button type="button" class="btn btn-ghost" @click="cancel">취소</button>
        </div>
      </form>

      <!-- 미리보기 카드 -->
      <aside class="card preview">
        <div class="pv-head">
          <h3>미리보기</h3>
          <span class="pv-time">{{ new Date().toLocaleString() }}</span>
        </div>
        <h4 class="pv-title">{{ title || '제목 미입력' }}</h4>
        <article class="pv-body">{{ content || '내용 미입력' }}</article>
      </aside>
    </div>
  </section>
</template>

<style scoped>
.newpost {
  position: relative;
  min-height: calc(100vh - 120px);
  padding: 24px 16px 48px;
  display: grid;
  gap: 16px;
}
.bg {
  position: fixed; inset: 0;
  background: radial-gradient(1200px 600px at 50% -10%, #dbeafe 0%, transparent 60%),
              radial-gradient(800px 400px at 80% 20%, #e9d5ff 0%, transparent 60%),
              radial-gradient(700px 350px at 20% 10%, #fde68a 0%, transparent 60%);
  filter: blur(20px);
  opacity: .55; pointer-events: none;
}

.head { max-width: 1080px; margin: 0 auto; padding: 0 4px; }
.title { margin: 0; font-size: 22px; font-weight: 900; letter-spacing: -0.02em; color: #0f172a; }
.sub { margin: 6px 0 0; color: #475569; font-size: 13px; }
.kbd {
  display: inline-block; padding: 1px 6px; border: 1px solid #e5e7eb; border-radius: 6px;
  background: #f8fafc; color: #111827; font-family: ui-monospace, SFMono-Regular, Menlo, monospace; font-size: 12px;
}

.grid {
  max-width: 1080px; margin: 0 auto;
  display: grid; gap: 14px; grid-template-columns: 1fr;
}
@media (min-width: 960px) {
  .grid { grid-template-columns: 1fr 1fr; align-items: start; }
}

/* 카드 공통 */
.card {
  background: var(--card-bg, rgba(255,255,255,0.82));
  border: 1px solid var(--card-border, rgba(234,234,234,0.9));
  backdrop-filter: saturate(140%) blur(6px);
  border-radius: 18px;
  padding: 16px 14px;
  box-shadow: 0 12px 34px rgba(0,0,0,0.06);
}

/* 에디터 */
/* .editor { display: grid; gap: 8px; } */
.posts{display: block;}
.editor{margin-bottom: 1rem;}
.label { font-size: 12px; color: #374151; font-weight: 800; }
.input, .textarea {
  width: 100%;
  border: 1px solid #e5e7eb; border-radius: 12px;
  background: #fff; color: #111827; outline: none;
  font-size: 14px; padding: 12px;
  transition: border-color .2s, box-shadow .2s, background .2s;
}
.input:focus, .textarea:focus {
  border-color: #93c5fa; box-shadow: 0 0 0 4px rgba(59,130,246,0.15);
}
.textarea { resize: vertical; min-height: 200px; }
.input.danger, .textarea.danger { border-color: #fca5a5; box-shadow: 0 0 0 4px rgba(239,68,68,0.12); }

.meta {
  display: flex; justify-content: flex-end; font-size: 12px; color: #6b7280;
}
.meta .warn { color: #ef4444; font-weight: 700; }

.actions { display: flex; gap: 8px; margin-top: 6px; }

/* 버튼 */
.btn {
  appearance: none; border: 1px solid transparent; border-radius: 10px;
  padding: 10px 12px; font-size: 14px; line-height: 1; cursor: pointer;
  transition: transform .03s, background .2s, color .2s, border-color .2s, box-shadow .2s;
}
.btn:disabled { opacity: .6; cursor: not-allowed; }
.btn:active { transform: translateY(1px); }
.btn-primary {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 10px 24px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; border-color: #1d4ed8; }
.btn-ghost {
  background: transparent; color: #374151; border: 1px solid #e5e7eb;
}
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }

/* 미리보기 */
.preview { overflow: hidden; }
.pv-head {
  display: flex; align-items: baseline; gap: 8px; margin-bottom: 8px;
}
.pv-head h3 { margin: 0; font-size: 14px; font-weight: 900; color: #0f172a; }
.pv-time { margin-left: auto; font-size: 12px; color: #64748b; }
.pv-title {
  margin: 2px 0 6px; font-size: 18px; font-weight: 800; letter-spacing: -0.01em; color: #111827;
  word-break: break-word;
}
.pv-body {
  white-space: pre-wrap; word-break: break-word;
  font-size: 14px; color: #111827;
}

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .title { color: #e5e7eb; }
  .sub { color: #94a3b8; }
  .card { --card-bg: rgba(11,18,32,0.82); --card-border: rgba(31,41,55,0.9); color: #e5e7eb; }
  .input, .textarea { background: #0b1220; color: #e5e7eb; border-color: #334155; }
  .input:focus, .textarea:focus { border-color: #60a5fa; box-shadow: 0 0 0 4px rgba(96,165,250,0.2); }
  .btn-ghost { color: #e5e7eb; border-color: #334155; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
  .pv-head h3, .pv-title, .pv-body { color: #e5e7eb; }
}
</style>

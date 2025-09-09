<!-- frontend/src/pages/PostDetail.vue -->
<script setup>
import { onMounted, ref, computed, defineComponent, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import client from '@/api/client'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

/* -------------------- 공통 상태 -------------------- */
const route = useRoute()
const router = useRouter()
const store = useStore()

const post = ref(null)
const loading = ref(false)
const deleting = ref(false)
const errorMsg = ref('')

/* -------------------- 유틸 -------------------- */
const getAuthorId = (p) =>
  p?.authorActivityId ??
  p?.authorId ??
  p?.author?.activityId ??
  p?.activityId ??
  null

const maskId = (id) => {
  if (!id) return '익명'
  const s = String(id)
  if (s.length <= 2) return s
  return s[0] + '*'.repeat(Math.max(0, s.length - 2)) + s[s.length - 1]
}

const fmtDotDate = (iso) => {
  if (!iso) return ''
  const d = new Date(iso)
  return `${d.getFullYear()}. ${d.getMonth() + 1}. ${d.getDate()}.`
}

const maskedAuthor = computed(() => maskId(getAuthorId(post.value)))
const meAid = computed(() => store.state.activityId)
const isOwner = computed(() => {
  const aid = getAuthorId(post.value)
  return !!aid && !!meAid.value && aid === meAid.value
})
const isAuthed = computed(() => store.getters.isAuthed)

/* -------------------- 본문: 마크다운 + XSS -------------------- */
const sanitizedHtml = computed(() => {
  const raw = post.value?.content ?? ''
  const html = marked.parse(raw, { breaks: true, gfm: true })
  return DOMPurify.sanitize(html, { USE_PROFILES: { html: true } })
})

/* -------------------- 링크 복사 -------------------- */
const copying = ref(false)
const copyLink = async () => {
  try {
    copying.value = true
    const url = `${window.location.origin}/posts/${route.params.id}`
    await navigator.clipboard.writeText(url)
    alert('링크가 복사되었습니다!')
  } catch {
    alert('복사에 실패했습니다. 수동으로 복사해 주세요.')
  } finally {
    copying.value = false
  }
}

/* -------------------- 반응 -------------------- */
const reactions = ref({ likes: 0, dislikes: 0, my: null })
const rLoading = ref(false)

const fetchReactions = async () => {
  try {
    const { data } = await client.get(`/posts/${route.params.id}/reactions`)
    reactions.value = {
      likes: Number(data?.likes ?? 0),
      dislikes: Number(data?.dislikes ?? 0),
      my: data?.my ?? null,
    }
  } catch (e) {
    console.warn('reactions fetch failed', e)
  }
}

const react = async (type) => {
  if (!isAuthed.value) {
    alert('로그인이 필요합니다.')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  try {
    rLoading.value = true
    await client.post(`/posts/${route.params.id}/reactions`, { type })
    await fetchReactions()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.error || '반응 처리에 실패했습니다.')
  } finally {
    rLoading.value = false
  }
}

/* -------------------- 댓글 -------------------- */
// 서버 응답 예시 필드: id, postId, parentId, authorActivityId, content | contentRaw | contentHtml/html, createdAt, my
const comments = ref([])
const cLoading = ref(false)
const cError = ref('')
const totalCount = computed(() => comments.value.length)

const newComment = ref('')
const replyFor = ref(null)        // 현재 대댓글 입력 중인 comment id
const replyText = ref('')

const editingId = ref(null)       // 현재 수정 중인 comment id
const editingText = ref('')

/** HTML → 순수 텍스트(+개행)로 복원(편집 초기값용) */
const htmlToPlain = (html) => {
  if (!html) return ''
  const tmp = document.createElement('div')
  tmp.innerHTML = html
  // <br> 보존
  tmp.querySelectorAll('br').forEach(br => (br.outerHTML = '\n'))
  const text = tmp.textContent || ''
  return text.replace(/\u00A0/g, ' ').trimEnd()
}

const fetchComments = async () => {
  cLoading.value = true
  cError.value = ''
  try {
    const { data } = await client.get(`/posts/${route.params.id}/comments`)
    const raw = Array.isArray(data) ? data : []
    const me = store.state.activityId
    comments.value = raw.map(c => {
      const author = c?.authorActivityId ?? null
      const html = c?.contentHtml ?? c?.html ?? null
      return {
        ...c,
        // 백엔드가 my를 주지 않으므로 클라이언트에서 보정
        my: !!(author && me && author === me),
        // 편집 시작 시 자연스러운 기본값
        _plainForEdit: html ? htmlToPlain(html) : (c?.content ?? c?.contentRaw ?? '')
      }
    })
  } catch (e) {
    console.error(e)
    cError.value = '댓글을 불러오지 못했습니다.'
  } finally {
    cLoading.value = false
  }
}

// 계층 트리(오래된 → 최신) : 최신이 맨 아래
const commentTree = computed(() => {
  const byParent = new Map()
  for (const c of comments.value) {
    const pid = c.parentId ?? 0
    if (!byParent.has(pid)) byParent.set(pid, [])
    byParent.get(pid).push(c)
  }
  for (const arr of byParent.values()) {
    arr.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  }
  const build = (pid) =>
    (byParent.get(pid) || []).map(c => ({ ...c, children: build(c.id) }))
  return build(0)
})

// 렌더에 사용할 sanitize
const sanitizeComment = (input) => {
  const src =
    typeof input === 'string'
      ? input
      : (input?.contentHtml ?? input?.html ?? input?.content ?? input?.contentRaw ?? '')
  const looksHtml = typeof src === 'string' && (input?.contentHtml || input?.html)
  if (looksHtml) return DOMPurify.sanitize(src, { USE_PROFILES: { html: true } })
  const html = marked.parse(src || '', { breaks: true, gfm: true })
  return DOMPurify.sanitize(html, { USE_PROFILES: { html: true } })
}

// 등록/수정/삭제
const addTopComment = async () => {
  const body = (newComment.value || '').trim()
  if (!body) return alert('댓글을 입력하세요.')
  if (!isAuthed.value) {
    alert('로그인이 필요합니다.')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  try {
    await client.post(`/posts/${route.params.id}/comments`, { content: body })
    newComment.value = ''
    await fetchComments()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.error || '댓글 등록에 실패했습니다.')
  }
}

const sendReply = async (parentId) => {
  const body = (replyText.value || '').trim()
  if (!body) return alert('답글을 입력하세요.')
  if (!isAuthed.value) {
    alert('로그인이 필요합니다.')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  try {
    await client.post(`/posts/${route.params.id}/comments`, { content: body, parentId })
    replyText.value = ''
    replyFor.value = null
    await fetchComments()
  } catch (e) {
    console.error(e)
    alert(e?.response?.data?.error || '답글 등록에 실패했습니다.')
  }
}

const delComment = async (id) => {
  if (!confirm('이 댓글을 삭제하시겠습니까?')) return
  try {
    await client.delete(`/comments/${id}`)
    if (editingId.value === id) {
      editingId.value = null; editingText.value = ''
    }
    await fetchComments()
  } catch (e) {
    console.error(e)
    const st = e?.response?.status
    if (st === 403) alert('삭제 권한이 없습니다.')
    else alert('댓글 삭제에 실패했습니다.')
  }
}

const startEdit = (id, currentContent) => {
  replyFor.value = null
  editingId.value = id
  // 서버가 HTML을 주는 구조이므로 없을 때는 미리 추출해 둔 _plainForEdit 사용
  const fromList = comments.value.find(x => x.id === id)?._plainForEdit ?? ''
  editingText.value = (currentContent ?? fromList ?? '').trim()
}

const cancelEdit = () => {
  editingId.value = null
  editingText.value = ''
}

const saveEdit = async (id) => {
  const body = (editingText.value || '').trim()
  if (!body) return alert('내용을 입력하세요.')
  try {
    await client.patch(`/comments/${id}`, { content: body })
    editingId.value = null
    editingText.value = ''
    await fetchComments()
  } catch (e) {
    console.error(e)
    const st = e?.response?.status
    if (st === 403) alert('수정 권한이 없습니다.')
    else alert(e?.response?.data?.error || '수정에 실패했습니다.')
  }
}

/* -------------------- 게시글 삭제/이동 -------------------- */
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

/* -------------------- 재귀 댓글 컴포넌트 (render) -------------------- */
const CommentNode = defineComponent({
  name: 'CommentNode',
  props: {
    node: { type: Object, required: true },
    me: { type: String, default: null },
    replyFor: { type: Number, default: null },
    replyText: { type: String, default: '' },
    sanitize: { type: Function, required: true },
    mask: { type: Function, required: true },
    editingId: { type: Number, default: null },
    editingText: { type: String, default: '' },
  },
  emits: [
    'reply', 'delete',
    'send-reply', 'update:reply-text',
    'start-edit', 'cancel-edit', 'update:editing-text', 'save-edit'
  ],
  setup(props, { emit }) {
    const onReplyInput = (e) => emit('update:reply-text', e.target.value)
    const onReplyKey = (id, e) => {
      if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); emit('send-reply', id) }
    }
    const onEditInput = (e) => emit('update:editing-text', e.target.value)
    const onEditKey = (id, e) => {
      if (e.key === 'Escape') { e.preventDefault(); emit('cancel-edit') }
      else if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); emit('save-edit', id) }
    }
    return { onReplyInput, onReplyKey, onEditInput, onEditKey }
  },
  render() {
    const n = this.node
    const masked = this.mask(n.authorActivityId || '') || '익명'
    const isEditing = this.editingId === n.id
    const safeHtml = this.sanitize(n)

    // 자식
    const childrenNodes = (n.children || []).map(child =>
      h(CommentNode, {
        key: child.id,
        node: child,
        me: this.me,
        replyFor: this.replyFor,
        replyText: this.replyText,
        sanitize: this.sanitize,
        mask: this.mask,
        editingId: this.editingId,
        editingText: this.editingText,
        onReply: (id) => this.$emit('reply', id),
        onDelete: (id) => this.$emit('delete', id),
        onSendReply: (id) => this.$emit('send-reply', id),
        'onUpdate:replyText': (v) => this.$emit('update:reply-text', v),
        onStartEdit: (id, content) => this.$emit('start-edit', id, content),
        onCancelEdit: () => this.$emit('cancel-edit'),
        'onUpdate:editingText': (v) => this.$emit('update:editing-text', v),
        onSaveEdit: (id) => this.$emit('save-edit', id),
      })
    )

    // 헤더 (이름 왼쪽, 날짜 오른쪽)
    const header = h('div', { class: 'c-headline' }, [
      h('strong', { class: 'c-name' }, masked),
      h('span', { class: 'flex-1' }),
      h('span', { class: 'c-date' }, fmtDotDate(n.createdAt))
    ])

    // 본문/편집
    const body = isEditing
      ? h('div', { class: 'c-edit' }, [
          h('textarea', {
            class: 'input',
            rows: 3,
            value: this.editingText,
            onInput: this.onEditInput,
            placeholder: '내용을 수정하세요 (Enter 저장, Shift+Enter 줄바꿈, ESC 취소)',
            onKeydown: (e) => this.onEditKey(n.id, e),
          }),
          h('div', { class: 'line-actions' }, [
            h('button', { class: 'btn-ghost-sm', onClick: () => this.$emit('cancel-edit') }, '취소'),
            h('button', { class: 'btn-primary-sm', onClick: () => this.$emit('save-edit', n.id) }, '저장'),
          ])
        ])
      : h('div', { class: 'c-body markdown-body', innerHTML: safeHtml })

    // 푸터 액션 (답글/수정/삭제)
    const actions = h('div', { class: 'c-tail' }, [
      h('button', { class: 'link', onClick: () => this.$emit('reply', n.id) }, '답글'),
      n.my
        ? h('button', { class: 'link', onClick: () => this.$emit('start-edit', n.id, n.content ?? n.contentRaw ?? '') }, '수정')
        : null,
      n.my
        ? h('button', { class: 'link', onClick: () => this.$emit('delete', n.id) }, '삭제')
        : null
    ])

    // 답글 입력창
    const replyBox = (this.replyFor === n.id && !isEditing)
      ? h('div', { class: 'c-reply' }, [
          h('textarea', {
            class: 'input',
            rows: 3,
            value: this.replyText,
            onInput: this.onReplyInput,
            placeholder: '답글을 입력하세요 (Enter 등록, Shift+Enter 줄바꿈)',
            onKeydown: (e) => this.onReplyKey(n.id, e),
          }),
          h('div', { class: 'right' }, [
            h('button', { class: 'btn-primary-sm', onClick: () => this.$emit('send-reply', n.id) }, '등록')
          ])
        ])
      : null

    return h('li', { class: 'c-item' }, [
      header,
      body,
      actions,
      replyBox,
      childrenNodes.length ? h('ul', { class: 'c-children' }, childrenNodes) : null
    ])
  }
})

/* -------------------- 초기 로드 -------------------- */
onMounted(async () => {
  try {
    loading.value = true
    const { data } = await client.get(`/posts/${route.params.id}`)
    post.value = data
    await Promise.all([fetchReactions(), fetchComments()])
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
    <div class="topbar">
      <button class="btn btn-ghost" @click="goList" aria-label="목록으로">← 목록</button>
    </div>

    <div v-if="loading" class="card">
      <div class="skeleton title"></div>
      <div class="meta">
        <span class="skeleton chip"></span>
        <span class="skeleton small"></span>
      </div>
      <div class="skeleton body"></div>
      <div class="skeleton body"></div>
    </div>

    <div v-else-if="errorMsg" class="error-box">{{ errorMsg }}</div>

    <div v-else-if="post" class="card">
      <h2 class="title">{{ post.title }}</h2>

      <div class="meta">
        <span class="chip" title="작성자">{{ maskedAuthor }}</span>
        <span class="dot">•</span>
        <span class="time" :title="new Date(post.createdAt).toISOString()">
          {{ new Date(post.createdAt).toLocaleString() }}
        </span>
      </div>

      <article class="content markdown-body" v-html="sanitizedHtml"></article>

      <div class="toolbar">
        <button class="btn btn-like" :class="{ active: reactions.my==='LIKE' }" :disabled="rLoading" @click="react('LIKE')">
          👍 좋아요 <span class="count">{{ reactions.likes }}</span>
        </button>
        <button class="btn btn-dislike" :class="{ active: reactions.my==='DISLIKE' }" :disabled="rLoading" @click="react('DISLIKE')">
          👎 싫어요 <span class="count">{{ reactions.dislikes }}</span>
        </button>
        <div class="spacer" />
        <button class="btn btn-ghost" :disabled="copying" @click="copyLink">🔗 링크복사</button>
      </div>

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

    <!-- 댓글 영역 -->
    <div v-if="post" class="card comments">
      <!-- 입력창 -->
      <div class="c-editor">
        <textarea
          v-model="newComment"
          class="editor-text"
          rows="5"
          maxlength="500"
          placeholder="댓글을 입력하세요. (Enter 등록, Shift+Enter 줄바꿈)"
          @keydown.enter.exact.prevent="addTopComment"
        />
        <div class="editor-side">
          <button class="editor-submit" @click="addTopComment" :disabled="!newComment.trim()">등록</button>
          <div class="total">전체 {{ totalCount }}</div>
        </div>
      </div>

      <!-- 목록 -->
      <div v-if="cLoading" class="c-loading">불러오는 중…</div>
      <div v-else-if="cError" class="error-box">{{ cError }}</div>
      <template v-else>
          <!-- 실제 목록 -->
          <ul class="c-list smooth">
            <CommentNode
              v-for="node in commentTree"
              :key="node.id"
              :node="node"
              :me="meAid"
              :mask="maskId"
              @reply="(id) => { replyFor = id; editingId = null; replyText = '' }"
              @delete="delComment"
              @send-reply="sendReply"
              :reply-for="replyFor"
              :reply-text="replyText"
              @update:reply-text="(v) => replyText = v"
              :sanitize="sanitizeComment"
              :editing-id="editingId"
              :editing-text="editingText"
              @start-edit="(id, content) => { editingId = id; editingText = (content ?? '').trim() }"
              @cancel-edit="cancelEdit"
              @update:editing-text="(v) => editingText = v"
              @save-edit="saveEdit"
            />
          </ul>
        <div v-if="!commentTree.length" class="empty">첫 댓글을 남겨보세요.</div>
      </template>
    </div>
  </section>
</template>

<style scoped>
.post-detail { max-width: 760px; margin: 24px auto; padding: 0 16px; }
.topbar { display: flex; align-items: center; margin-bottom: 8px; }

/* 카드 */
.card {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 16px;
  padding: 20px 20px 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
}

/* 제목/메타 */
.title { margin: 0 0 8px; line-height: 1.25; font-size: 22px; font-weight: 800; letter-spacing: -0.01em; }
.meta { display: flex; align-items: center; gap: 8px; color: #6b7280; font-size: 12px; margin-bottom: 12px; }
.chip { display: inline-flex; align-items: center; padding: 2px 8px; border-radius: 999px; font-weight: 600; background: #f1f5f9; color: #334155; border: 1px solid #e2e8f0; }
.dot { opacity: .6; } .time { opacity: .9; }

/* 본문 */
.content { margin-top: 12px; line-height: 1.7; font-size: 15px; color: #111827; }
.markdown-body :where(h1,h2,h3){ margin: 16px 0 8px; }
.markdown-body p { margin: 10px 0; }
.markdown-body pre, .markdown-body code {
  background: #0f172a0d; padding: 2px 6px; border-radius: 6px;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  white-space: pre-wrap; word-break: break-word;
}
.markdown-body pre { padding: 10px; overflow:auto; }
.markdown-body a { color: #2563eb; text-decoration: underline; }

/* 툴바 */
.toolbar { display: flex; gap: 8px; align-items: center; margin-top: 14px; }
.spacer { flex: 1; }
.btn {
  appearance: none; border: 1px solid transparent; border-radius: 10px;
  padding: 10px 14px; font-size: 14px; line-height: 1; cursor: pointer;
  transition: transform .03s ease, box-shadow .2s ease, background .2s ease, color .2s ease, border-color .2s ease;
  user-select: none;
}
.btn:active { transform: translateY(1px); }
.btn-ghost { background: transparent; border-color: #e5e7eb; color: #374151; }
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }
.btn-primary { background: #2563eb; color: #fff; box-shadow: 0 6px 16px rgba(37,99,235,0.25); }
.btn-primary:hover { background: #1d4ed8; }
.btn-danger { background: #ef4444; color: #fff; box-shadow: 0 6px 16px rgba(239,68,68,0.25); }
.btn-danger:hover { background: #dc2626; }
.btn-like, .btn-dislike { border: 1px solid #e5e7eb; background: #fff; color: #111827; }
.btn-like.active { border-color: #60a5fa; background: #eff6ff; }
.btn-dislike.active { border-color: #fda4af; background: #fff1f2; }
.count { margin-left: 6px; font-weight: 700; }
.actions { margin-top: 12px; display: flex; gap: 8px; justify-content: flex-end; flex-wrap: wrap; }

/* 스켈레톤 */
@keyframes shimmer { 0% { background-position: -400px 0; } 100% { background-position: 400px 0; } }
.skeleton { border-radius: 10px; background: #f3f4f6; background-image: linear-gradient(90deg, #f3f4f6 0px, #e5e7eb 40px, #f3f4f6 80px); background-size: 600px 100%; animation: shimmer 1.2s infinite linear; }
.skeleton.title { height: 28px; width: 70%; margin-bottom: 10px; }
.skeleton.small { height: 16px; width: 180px; }
.skeleton.chip { height: 22px; width: 90px; border-radius: 999px; }
.skeleton.body { height: 14px; width: 100%; margin-top: 12px; }
.skeleton.body + .skeleton.body { width: 85%; }

/* ================= 댓글 ================= */
.comments { margin-top: 18px; }

/* 입력창 */
.c-editor { display: grid; grid-template-columns: 1fr 90px; gap: 8px; align-items: stretch; }
.editor-text {
  width: 100%; min-height: 110px; resize: vertical;
  border: 1px solid #e5e7eb; border-radius: 8px; padding: 12px; font-size: 14px;
}
.editor-text:focus { outline: none; border-color: #93c5fd; box-shadow: 0 0 0 4px rgba(59,130,246,0.15); }
.editor-side { display: grid; grid-auto-rows: 1fr auto; gap: 6px; }
.editor-submit {
  height: 100%;
  border: 1px solid #111827; background: #111827; color: #fff;
  border-radius: 8px; cursor: pointer; font-weight: 800;
}
.editor-submit:disabled { opacity: .5; cursor: not-allowed; }
.total { font-size: 12px; color: #6b7280; text-align: center; }

/* 목록 */
.c-list { list-style: none; padding: 0; margin: 8px 0 0; }
.c-item {
  padding: 14px 0;
  border-top: 1px solid #eaeaea;
}

/* 헤더 라인: 이름(좌) 날짜(우) */
.c-headline { display:flex; align-items:center; gap:8px; }
.c-name { font-weight: 900; color: #111827; }
.c-date { color: #9ca3af; font-size: 12px; }
.flex-1 { flex: 1; }

/* 본문 & 액션 */
.c-body { margin-top: 6px; line-height: 1.7; font-size: 15px; color: #111827; }
.c-tail { display: flex; gap: 10px; margin-top: 6px; }
.link {
  border: 0; background: transparent; padding: 0; cursor: pointer;
  color: #6b7280; font-size: 12px;
}
.link:hover { color: #111827; text-decoration: underline; }

/* 대댓글(들여쓰기 + 연한 세로선) */
.c-children {
  list-style: none;
  margin: 8px 0 0 12px;
  padding-left: 12px;
  border-left: 2px dashed #e5e7eb;
}

.c-children .c-item{padding: 20px;border-top: 1px solid #6b7280;}

/* 편집/답글 입력 공통 */
.input {
  width: 100%; padding: 10px; border: 1px solid #e5e7eb; border-radius: 8px;
  background: #fff; color: #111827; font-size: 14px; outline: none;
}
.input:focus { border-color: #93c5fd; box-shadow: 0 0 0 4px rgba(59,130,246,0.15); }
.c-reply { margin-top: 8px; display: grid; gap: 6px; }
.c-edit { margin-top: 8px; display: grid; gap: 6px; }
.line-actions { display: flex; gap: 8px; justify-content: flex-end; }
.btn-ghost-sm {
  padding: 6px 10px; font-size: 12px; border-radius: 6px;
  border: 1px solid #e5e7eb; background: #fff; color: #374151; cursor: pointer;
}
.btn-ghost-sm:hover { background: #f9fafb; border-color: #d1d5db; }
.btn-primary-sm {
  padding: 6px 10px; font-size: 12px; border-radius: 6px; border: none;
  background: #2563eb; color: #fff; cursor: pointer;
}

/* ===== 댓글 리스트 래퍼/툴바 ===== */
.c-list-wrap{
  border:1px solid #eaeaea;
  border-radius:12px;
  background:#fff;
  overflow:hidden;
  margin-top:8px;
}
.c-list-toolbar{
  display:flex; align-items:center; justify-content:space-between;
  padding:10px 12px;
  background:#f8fafc;
  border-bottom:1px solid #f1f5f9;
}
.tl-left{ display:flex; align-items:center; gap:8px; }
.muted{ color:#6b7280; font-size:12px; }

/* 뱃지(댓글 라벨) */
.badge{
  display:inline-flex; align-items:center; gap:6px;
  padding:3px 8px; border-radius:999px;
  background:#ffedd5; color:#9a3412; font-weight:900; font-size:12px;
  border:1px solid #fed7aa;
}
.badge::before{ content:""; width:6px; height:6px; border-radius:50%; background:#f97316; }

/* 새로고침 버튼 */
.icon-btn{
  padding:6px 10px; border:1px solid #e5e7eb; border-radius:8px;
  background:#fff; color:#374151; font-size:13px; cursor:pointer;
}
.icon-btn:hover{ background:#f9fafb; border-color:#d1d5db; }

/* ===== 리스트 자체 꾸미기 ===== */
.c-list.smooth{ padding:0; margin:0; list-style:none; }
.c-list.smooth > .c-item{
  padding:14px 16px;
  border-top:1px solid #f1f5f9;
  transition: background .15s ease;
}
.c-list.smooth > .c-item:first-child{ border-top:0; }
.c-list.smooth > .c-item:hover{ background:#fafafa; }

/* 비어있음 */
.empty { color: #6b7280; text-align: center; padding: 12px 8px; }

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card { --card-bg: #0b1220; --card-border: #1f2937; color: #e5e7eb; }
  .content { color: #e5e7eb; }
  .chip { background: #0f172a; border-color: #1f2937; color: #e2e8f0; }
  .btn-ghost { border-color: #334155; color: #e5e7eb; }
  .btn-ghost:hover { background: #0b1220; border-color: #475569; }
  .skeleton { background: #111827; background-image: linear-gradient(90deg, #111827 0px, #1f2937 40px, #111827 80px); }

  .btn-like, .btn-dislike { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .editor-text { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .editor-submit { background: #0f172a; border-color: #0f172a; }
  .c-item { display: flex;align-items: center; border-color: #1f2937; }
  .c-date { color: #9ca3af; }
  .link { color: #9ca3af; }
  .link:hover { color: #e5e7eb; }
  .c-children { border-color: #334155; }
  .input { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .input:focus { border-color: #60a5fa; box-shadow: 0 0 0 4px rgba(96,165,250,0.2); }

  .c-list-wrap{ background:#0b1220; border-color:#1f2937; }
  .c-list-toolbar{ background:#0f172a; border-color:#1f2937; }
  .icon-btn{ background:#0b1220; border-color:#334155; color:#e5e7eb; }
  .icon-btn:hover{ background:#0b1220; border-color:#475569; }
  .badge{ background:#1f2937; color:#fde68a; border-color:#374151; }
  .muted{ color:#9ca3af; }
  .c-list.smooth > .c-item{ border-color:#1f2937; }
  .c-list.smooth > .c-item:hover{ background:#0b1220; }
}
</style>

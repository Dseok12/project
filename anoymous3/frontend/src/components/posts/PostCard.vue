<script setup>
import { computed } from 'vue'

const props = defineProps({
  post: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['select'])

const authorId = computed(() => {
  const candidate =
    props.post?.authorActivityId ||
    props.post?.authorId ||
    props.post?.author?.activityId ||
    props.post?.activityId ||
    null
  if (!candidate) return '익명'
  const value = String(candidate)
  if (value.length <= 2) return value
  return `${value[0]}${'*'.repeat(value.length - 2)}${value[value.length - 1]}`
})

const createdAt = computed(() => {
  if (!props.post?.createdAt) return ''
  try {
    return new Date(props.post.createdAt).toLocaleString()
  } catch (error) {
    console.warn('createdAt parse failed', error)
    return props.post.createdAt
  }
})

const excerpt = computed(() => {
  const text = props.post?.content ?? ''
  if (!text) return ''
  const plain = String(text).replace(/\s+/g, ' ').trim()
  return plain.length > 120 ? `${plain.slice(0, 117)}…` : plain
})

const handleSelect = () => emit('select', props.post)

const onKeyup = (event) => {
  if (event.key === 'Enter' || event.key === ' ') {
    event.preventDefault()
    handleSelect()
  }
}
</script>

<template>
  <article
    class="post-card"
    role="button"
    tabindex="0"
    @click="handleSelect"
    @keyup="onKeyup"
  >
    <header class="post-card__header">
      <h3 class="post-card__title">{{ post.title }}</h3>
      <p v-if="excerpt" class="post-card__excerpt">{{ excerpt }}</p>
    </header>
    <footer class="post-card__meta">
      <span class="post-card__badge">#{{ post.id }}</span>
      <span class="post-card__dot" aria-hidden="true">•</span>
      <span class="post-card__date">{{ createdAt }}</span>
      <span class="post-card__dot" aria-hidden="true">•</span>
      <span class="post-card__author">{{ authorId }}</span>
    </footer>
  </article>
</template>

<style scoped>
.post-card {
  display: grid;
  gap: 12px;
  padding: 18px 18px 16px;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  background: var(--card-bg, #ffffff);
  box-shadow: 0 12px 28px rgba(15, 23, 42, 0.08);
  transition: transform 0.16s ease, box-shadow 0.2s ease, border-color 0.2s ease;
  cursor: pointer;
  outline: none;
}

.post-card:focus-visible {
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.45);
  border-color: #60a5fa;
}

.post-card:hover {
  transform: translateY(-2px);
  border-color: rgba(148, 163, 184, 0.65);
  box-shadow: 0 14px 36px rgba(30, 41, 59, 0.16);
}

.post-card__header {
  display: grid;
  gap: 6px;
}

.post-card__title {
  margin: 0;
  font-size: clamp(16px, 2.4vw, 18px);
  font-weight: 800;
  color: #0f172a;
  word-break: keep-all;
}

.post-card__excerpt {
  margin: 0;
  font-size: 13px;
  line-height: 1.55;
  color: #475569;
}

.post-card__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
  font-size: 12px;
  color: #64748b;
}

.post-card__badge {
  padding: 2px 6px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-weight: 700;
}

.post-card__dot {
  opacity: 0.6;
}

.post-card__author {
  font-weight: 600;
}

@media (prefers-color-scheme: dark) {
  .post-card {
    --card-bg: rgba(15, 23, 42, 0.85);
    border-color: rgba(51, 65, 85, 0.7);
    box-shadow: 0 12px 28px rgba(2, 6, 23, 0.65);
    color: #e2e8f0;
  }
  .post-card__title { color: #e2e8f0; }
  .post-card__excerpt { color: #cbd5f5; }
  .post-card__meta { color: #cbd5f5; }
  .post-card__badge { background: rgba(37, 99, 235, 0.2); color: #bfdbfe; }
}
</style>

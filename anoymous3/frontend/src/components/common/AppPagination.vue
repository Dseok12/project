<script setup>
import { computed } from 'vue'

const props = defineProps({
  current: {
    type: Number,
    required: true,
  },
  total: {
    type: Number,
    required: true,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  window: {
    type: Number,
    default: 2,
  },
})

const emit = defineEmits(['change'])

const pages = computed(() => {
  const total = Math.max(1, props.total)
  const current = Math.min(Math.max(1, props.current), total)
  const window = Math.max(1, props.window)
  const range = []

  const start = Math.max(1, current - window)
  const end = Math.min(total, current + window)

  if (start > 1) {
    range.push(1)
    if (start > 2) range.push('…')
  }

  for (let i = start; i <= end; i += 1) {
    range.push(i)
  }

  if (end < total) {
    if (end < total - 1) range.push('…')
    range.push(total)
  }

  return range
})

const go = (page) => {
  if (props.disabled) return
  const target = Math.min(Math.max(1, page), Math.max(1, props.total))
  emit('change', target)
}
</script>

<template>
  <nav class="pagination" role="navigation" aria-label="페이지네이션">
    <button class="pagination__btn" :disabled="disabled || current <= 1" @click="go(current - 1)">
      이전
    </button>

    <template v-for="(page, idx) in pages" :key="idx">
      <span v-if="page === '…'" class="pagination__ellipsis">…</span>
      <button
        v-else
        class="pagination__btn"
        :class="{ 'is-active': page === current }"
        :disabled="disabled || page === current"
        @click="go(page)"
      >
        {{ page }}
      </button>
    </template>

    <button
      class="pagination__btn"
      :disabled="disabled || current >= total"
      @click="go(current + 1)"
    >
      다음
    </button>
  </nav>
</template>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  justify-content: center;
  margin-top: 24px;
}

.pagination__btn {
  appearance: none;
  border: 1px solid rgba(203, 213, 225, 0.9);
  background: rgba(248, 250, 252, 0.85);
  border-radius: 999px;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
  cursor: pointer;
  transition: transform 0.1s ease, background 0.2s ease, border-color 0.2s ease;
}

.pagination__btn:hover {
  border-color: rgba(148, 163, 184, 0.9);
  background: #e2e8f0;
}

.pagination__btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination__btn.is-active {
  background: #2563eb;
  border-color: #2563eb;
  color: #ffffff;
  box-shadow: 0 10px 20px rgba(37, 99, 235, 0.25);
}

.pagination__ellipsis {
  padding: 0 4px;
  color: #94a3b8;
}

@media (prefers-color-scheme: dark) {
  .pagination__btn {
    border-color: rgba(71, 85, 105, 0.6);
    background: rgba(30, 41, 59, 0.85);
    color: #e2e8f0;
  }
  .pagination__btn:hover {
    border-color: rgba(148, 163, 184, 0.6);
    background: rgba(51, 65, 85, 0.9);
  }
}
</style>

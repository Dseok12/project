<script setup>
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'

import useAuth from '@/composables/useAuth'
import AppPagination from '@/components/common/AppPagination.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'
import PostList from '@/components/posts/PostList.vue'

const store = useStore()
const route = useRoute()
const router = useRouter()
const { isAuthed } = useAuth()

const posts = computed(() => store.getters['posts/list'])
const loading = computed(() => store.getters['posts/isLoading'])
const errorMsg = computed(() => store.getters['posts/error'])
const pagination = computed(() => store.getters['posts/pagination'])

const currentPage = computed(() => pagination.value.page ?? 1)
const totalPages = computed(() => pagination.value.totalPages ?? 1)
const totalElements = computed(() => pagination.value.totalElements ?? 0)

const goPage = (page) => {
  const target = Math.max(1, page)
  if (Number(route.query.page || 1) === target) return
  router.push({ path: '/posts', query: { page: target } })
}

const fetchPage = async (pageValue) => {
  const fallback = Number(pageValue ?? 1)
  const safe = Number.isFinite(fallback) && fallback > 0 ? Math.floor(fallback) : 1
  try {
    const { page: resolvedPage, corrected } = await store.dispatch('posts/fetchList', {
      page: safe,
    })
    if (corrected && resolvedPage !== safe) {
      router.replace({ path: '/posts', query: { page: resolvedPage } })
    }
  } catch (error) {
    console.error('posts/fetchPage failed', error)
  }
}

watch(
  () => route.query.page,
  (value) => {
    fetchPage(value)
  },
  { immediate: true }
)

const openPost = (post) => {
  if (!post?.id) return
  router.push({ name: 'post-detail', params: { id: post.id } })
}
</script>

<template>
  <section class="posts">
    <header class="posts__header">
      <div>
        <h1 class="posts__title">익명 게시판</h1>
        <p class="posts__subtitle">익명으로 진솔한 이야기를 나누고 공감해요.</p>
      </div>
      <router-link v-if="isAuthed" to="/posts/new" class="posts__cta">
        새 글 쓰기
      </router-link>
    </header>

    <SkeletonList v-if="loading" :rows="6" />

    <div v-else-if="errorMsg" class="posts__error">{{ errorMsg }}</div>

    <template v-else>
      <PostList v-if="posts.length" :posts="posts" @select="openPost" />
      <p v-else class="posts__empty">첫 게시물을 작성해보세요!</p>

      <footer class="posts__footer">
        <AppPagination
          v-if="totalPages > 1"
          :current="currentPage"
          :total="totalPages"
          :disabled="loading"
          @change="goPage"
        />
        <p class="posts__total">총 {{ totalElements.toLocaleString() }}개의 글</p>
      </footer>
    </template>
  </section>
</template>

<style scoped>
.posts {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px 40px;
  display: grid;
  gap: 24px;
}

.posts__header {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: center;
  justify-content: space-between;
}

.posts__title {
  margin: 0;
  font-size: clamp(22px, 4vw, 28px);
  font-weight: 900;
  color: #0f172a;
}

.posts__subtitle {
  margin: 6px 0 0;
  font-size: 14px;
  color: #64748b;
}

.posts__cta {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 12px 18px;
  font-size: 14px;
  font-weight: 700;
  border-radius: 14px;
  background: linear-gradient(135deg, #6366f1, #2563eb);
  color: #ffffff;
  text-decoration: none;
  box-shadow: 0 16px 32px rgba(79, 70, 229, 0.25);
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.posts__cta:hover {
  transform: translateY(-1px);
  box-shadow: 0 18px 36px rgba(37, 99, 235, 0.28);
}

.posts__cta:focus-visible {
  outline: 2px solid #93c5fd;
  outline-offset: 4px;
}

.posts__error,
.posts__empty {
  margin: 0;
  padding: 20px;
  border-radius: 18px;
  border: 1px dashed rgba(148, 163, 184, 0.4);
  background: rgba(248, 250, 252, 0.7);
  color: #475569;
  text-align: center;
  font-size: 14px;
}

.posts__footer {
  display: grid;
  gap: 12px;
  justify-items: center;
}

.posts__total {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
}

@media (prefers-color-scheme: dark) {
  .posts__title { color: #e2e8f0; }
  .posts__subtitle { color: #cbd5e1; }
  .posts__error,
  .posts__empty {
    border-color: rgba(71, 85, 105, 0.6);
    background: rgba(15, 23, 42, 0.85);
    color: #cbd5e1;
  }
  .posts__total { color: #cbd5e1; }
}
</style>

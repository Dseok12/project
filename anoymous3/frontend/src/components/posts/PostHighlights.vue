<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import client from '@/api/client'
import { normalizePostsResponse } from '@/utils/posts'
import PostList from './PostList.vue'
import SkeletonList from '@/components/common/SkeletonList.vue'

const router = useRouter()

const loading = ref(true)
const error = ref('')
const posts = ref([])

const fetchHighlights = async () => {
  loading.value = true
  error.value = ''
  try {
    const { data } = await client.get('/posts', {
      params: {
        page: 0,
        size: 4,
        sort: 'createdAt,desc',
      },
    })
    const parsed = normalizePostsResponse(data)
    posts.value = parsed.items.slice(0, 4)
  } catch (err) {
    console.error('fetchHighlights error', err)
    error.value = err?.response?.status
      ? `최근 게시물을 불러오지 못했습니다. (HTTP ${err.response.status})`
      : '최근 게시물을 불러오지 못했습니다.'
  } finally {
    loading.value = false
  }
}

const openPost = (post) => {
  if (!post?.id) return
  router.push({ name: 'post-detail', params: { id: post.id } })
}

onMounted(fetchHighlights)
</script>

<template>
  <section class="highlights">
    <header class="highlights__header">
      <div>
        <h2 class="highlights__title">방금 올라온 글</h2>
        <p class="highlights__subtitle">따끈따끈한 게시물을 바로 확인해보세요.</p>
      </div>
      <router-link to="/posts" class="highlights__more">전체 보기</router-link>
    </header>

    <SkeletonList v-if="loading" :rows="4" />

    <p v-else-if="error" class="highlights__error">{{ error }}</p>

    <template v-else>
      <PostList v-if="posts.length" :posts="posts" @select="openPost" />
      <p v-else class="highlights__empty">첫 게시물을 작성해보세요!</p>
    </template>
  </section>
</template>

<style scoped>
.highlights {
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
  display: grid;
  gap: 20px;
  padding: 18px 0 0;
}

.highlights__header {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
}

.highlights__title {
  margin: 0;
  font-size: clamp(18px, 3vw, 22px);
  font-weight: 800;
  color: #0f172a;
}

.highlights__subtitle {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 13px;
}

.highlights__more {
  font-size: 13px;
  font-weight: 700;
  color: #2563eb;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(37, 99, 235, 0.15);
  transition: background 0.2s ease, border-color 0.2s ease;
}

.highlights__more:hover {
  background: rgba(37, 99, 235, 0.18);
  border-color: rgba(37, 99, 235, 0.3);
}

.highlights__error,
.highlights__empty {
  margin: 0;
  padding: 20px;
  border-radius: 18px;
  border: 1px dashed rgba(148, 163, 184, 0.4);
  background: rgba(248, 250, 252, 0.7);
  color: #475569;
  text-align: center;
  font-size: 13px;
}

@media (prefers-color-scheme: dark) {
  .highlights__title { color: #e2e8f0; }
  .highlights__subtitle { color: #cbd5e1; }
  .highlights__more {
    color: #bfdbfe;
    background: rgba(59, 130, 246, 0.12);
    border-color: rgba(59, 130, 246, 0.3);
  }
  .highlights__error,
  .highlights__empty {
    border-color: rgba(71, 85, 105, 0.6);
    background: rgba(15, 23, 42, 0.85);
    color: #cbd5e1;
  }
}
</style>

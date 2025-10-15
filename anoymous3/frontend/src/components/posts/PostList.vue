<script setup>
import PostCard from './PostCard.vue'

const props = defineProps({
  posts: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['select'])

const handleSelect = (post) => emit('select', post)
</script>

<template>
  <div class="post-list">
    <PostCard
      v-for="item in posts"
      :key="item.id"
      :post="item"
      @select="handleSelect"
    />
  </div>
</template>

<style scoped>
.post-list {
  display: grid;
  gap: 18px;
}

@media (min-width: 720px) {
  .post-list {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}
</style>

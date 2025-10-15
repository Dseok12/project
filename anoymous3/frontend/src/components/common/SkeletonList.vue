<script setup>
const props = defineProps({
  rows: {
    type: Number,
    default: 6,
  },
})
</script>

<template>
  <div class="skeleton-list">
    <div v-for="i in rows" :key="i" class="skeleton-card">
      <div class="skeleton-line title"></div>
      <div class="skeleton-line meta"></div>
      <div class="skeleton-line body"></div>
    </div>
  </div>
</template>

<style scoped>
.skeleton-list {
  display: grid;
  gap: 18px;
}

.skeleton-card {
  padding: 18px;
  border-radius: 18px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  background: linear-gradient(110deg, #f8fafc 8%, #e2e8f0 18%, #f8fafc 33%);
  background-size: 200% 100%;
  animation: shimmer 1.4s ease infinite;
}

.skeleton-line {
  height: 12px;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.2);
}

.skeleton-line + .skeleton-line {
  margin-top: 12px;
}

.skeleton-line.title {
  width: 60%;
  height: 16px;
}

.skeleton-line.meta {
  width: 40%;
}

.skeleton-line.body {
  width: 80%;
}

@keyframes shimmer {
  0% { background-position: -120% 0; }
  100% { background-position: 120% 0; }
}

@media (prefers-color-scheme: dark) {
  .skeleton-card {
    border-color: rgba(71, 85, 105, 0.6);
    background: linear-gradient(110deg, rgba(15, 23, 42, 0.9) 8%, rgba(30, 41, 59, 0.9) 18%, rgba(15, 23, 42, 0.9) 33%);
  }
  .skeleton-line {
    background: rgba(148, 163, 184, 0.25);
  }
}
</style>

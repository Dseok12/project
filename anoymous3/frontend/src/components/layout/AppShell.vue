<script setup>
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'
</script>

<template>
  <div class="app-shell">
    <NavBar />
    <main class="app-shell__main">
      <slot />
    </main>
    <Footer />
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, rgba(248, 250, 252, 0.95) 0%, #ffffff 40%);
}

.app-shell__main {
  flex: 1;
  width: 100%;
  padding: 0 16px;
  box-sizing: border-box;
}

@media (min-width: 768px) {
  .app-shell__main {
    padding: 0 24px;
  }
}

@media (prefers-color-scheme: dark) {
  .app-shell {
    background: linear-gradient(180deg, rgba(15, 23, 42, 0.95) 0%, #0f172a 60%);
  }
}
</style>

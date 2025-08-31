<template>
  <div>
    <NavBar />
    <main style="max-width: 960px; margin: 24px auto; padding: 0 16px;">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import NavBar from '@/components/NavBar.vue'
import Footer from '@/components/Footer.vue'

const year = new Date().getFullYear()
</script>

<style>
  body { margin: 0; font-family: system-ui, sans-serif; }
  a { text-decoration: none; color: inherit; }
  nav a.router-link-active { font-weight: 700; text-decoration: underline; }
  button { cursor: pointer; }
  input, textarea { width: 100%; box-sizing: border-box; padding: 8px; }
</style>

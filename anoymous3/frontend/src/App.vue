<template>
  <AppShell>
    <router-view />
  </AppShell>
</template>

<script setup>
import AppShell from '@/components/layout/AppShell.vue'
</script>

<style>
:root {
  color-scheme: light dark;
}

body {
  margin: 0;
  font-family: 'Pretendard Variable', 'Noto Sans KR', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #f8fafc;
  color: #0f172a;
}

a {
  text-decoration: none;
  color: inherit;
}

a:focus-visible,
button:focus-visible,
input:focus-visible,
textarea:focus-visible {
  outline: 2px solid #93c5fd;
  outline-offset: 2px;
}

button {
  cursor: pointer;
}

input,
textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 8px;
}

@media (prefers-color-scheme: dark) {
  body {
    background: #0f172a;
    color: #e2e8f0;
  }
}
</style>

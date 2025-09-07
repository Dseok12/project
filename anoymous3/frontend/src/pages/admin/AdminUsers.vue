<template>
  <section class="admin-page">
    <h2>관리자 · 사용자 관리</h2>
    <p class="muted">유저 정지/해제/삭제 등 기능을 붙일 자리입니다. (임시 스텁)</p>
    <router-link class="btn" to="/admin">← 대시보드로</router-link>
  </section>
</template>

<script setup></script>

<style scoped>
.admin-page{max-width:960px;margin:24px auto;padding:0 16px}
h2{margin:0 0 10px;font-size:22px;font-weight:900}
.muted{color:#6b7280;margin-bottom:12px}
.btn{display:inline-block;padding:8px 12px;border:1px solid #e5e7eb;border-radius:10px;text-decoration:none}
</style>

<template>
  <section class="admin-page">
    <h2>관리자 · 게시물</h2>
    <p class="muted">여기는 관리자 전용 게시물 관리 화면입니다. (임시 스텁)</p>
    <router-link class="btn" to="/admin">← 대시보드로</router-link>
  </section>
</template>

<script setup>
// TODO: 추후 /api/admin 쪽 연동해서 목록/삭제/공지전환 등을 붙이면 됩니다.
</script>

<style scoped>
.admin-page{max-width:960px;margin:24px auto;padding:0 16px}
h2{margin:0 0 10px;font-size:22px;font-weight:900}
.muted{color:#6b7280;margin-bottom:12px}
.btn{display:inline-block;padding:8px 12px;border:1px solid #e5e7eb;border-radius:10px;text-decoration:none}
</style>

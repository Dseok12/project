<template>
  <div class="All-Wrapper">
    <!-- 모든 페이지에서 공통으로 표시되는 Header -->
    <Header />

    <!-- 라우터 페이지 출력 -->
    <router-view />
  </div>
</template>

<script setup>
import Header from './components/Header.vue'
</script>

<style lang="scss" scoped>
@use './css/base/_Reset.scss';
@use './css/base/_Common.scss';
</style>

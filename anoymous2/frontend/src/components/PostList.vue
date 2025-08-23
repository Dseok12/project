<template>
  <div>
    <h1>게시물 목록</h1>
    <ul>
      <li v-for="post in posts" :key="post.id">
        <router-link :to="`/posts/${post.id}`">{{ post.title }}</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'PostList',
  data() {
    return {
      posts: [
        { id: 1, title: '첫 번째 게시물' },
        { id: 2, title: '두 번째 게시물' },
      ],
    };
  },
  // 실제 API 호출은 mounted() 훅 등에서 구현 가능
};
</script>

<style scoped>
h1 {
  margin-bottom: 20px;
}
ul {
  padding-left: 0;
  list-style: none;
}
li {
  margin-bottom: 10px;
}
</style>

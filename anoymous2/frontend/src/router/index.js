import { createRouter, createWebHistory } from 'vue-router';

// 라우터에 연결할 컴포넌트 임포트 또는 동적 임포트
import Main from '@views/Main.vue';
import SignupForm from '@components/SignupForm.vue';
import LoginForm from '@components/LoginForm.vue';
import PostList from '@components/PostList.vue';
import PostCreate from '@components/PostCreate.vue';
import MyPage from '@components/MyPage.vue';

const routes = [
  {
    path: '/',
    name: 'Main',
    component: Main,
  },
  {
    path: '/signup',
    name: 'Signup',
    component: SignupForm,
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginForm,
  },
  {
    path: '/posts',
    name: 'PostList',
    component: PostList,
  },
  {
    path: '/posts/create',
    name: 'PostCreate',
    component: PostCreate,
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: MyPage,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

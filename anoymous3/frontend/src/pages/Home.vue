<script setup>
import { useRouter } from 'vue-router'
import useAuth from '@/composables/useAuth'
import PostHighlights from '@/components/posts/PostHighlights.vue'

const router = useRouter()
const { isAuthed, activityId } = useAuth()

const go = (path) => router.push(path)


</script>

<template>
  <section class="home">
    <!-- 배경 그라데이션 -->
    <div class="bg"></div>

    <!-- 히어로 카드 -->
    <div class="card">
      <h1 class="title">어노이(anoy) — 익명 게시판</h1>
      <p class="sub">이곳은 익명으로 소통하는 커뮤니티입니다. 줄바꿈 그대로, 생각 그대로.</p>

      <!-- CTA -->
      <div class="cta">
        <template v-if="!isAuthed">
          <button class="btn btn-primary" @click="go('/signup')">회원가입</button>
          <button class="btn btn-ghost" @click="go('/login')">로그인</button>
          <button class="btn btn-soft" @click="go('/posts')">게시물 보기</button>
        </template>
        <template v-else>
          <button class="btn btn-primary" @click="go('/posts/new')">새 글 쓰기</button>
          <button class="btn btn-ghost" @click="go(`/mypage/${activityId}`)">마이페이지</button>
          <button class="btn btn-soft" @click="go('/posts')">게시물 보기</button>
        </template>
      </div>
    </div>
    
    <!-- 특징 섹션 -->
    <div class="features">
      <article class="feature">
        <div class="icon">🕵️</div>
        <h3>익명성</h3>
        <p>활동 아이디만 노출. 실제 이메일은 보호됩니다.</p>
      </article>
      <!--
      <article class="feature">
        <div class="icon">📝</div>
        <h3>간편한 작성</h3>
        <p>엔터 줄바꿈 그대로 저장되어 읽기 편합니다.</p>
      </article>
      -->
      <article class="feature">
        <div class="icon">📌</div>
        <h3>공지 & 게시판</h3>
        <p>공지사항과 자유 게시판으로 깔끔하게 분리.</p>
      </article>
    </div>

    <PostHighlights class="posts" />


  </section>
</template>

<style scoped>
/* 레이아웃 */
.home {
  position: relative;
  min-height: calc(100vh - 120px);
  padding: 32px 16px 48px;
  display: grid;
  place-items: start center;
  gap: 24px;
}

/* 배경 그라데이션 + 블러 */
.bg {
  position: fixed; inset: 0;
  background: radial-gradient(1200px 600px at 50% -10%, #dbeafe 0%, transparent 60%),
              radial-gradient(800px 400px at 80% 20%, #e9d5ff 0%, transparent 60%),
              radial-gradient(700px 350px at 20% 10%, #fde68a 0%, transparent 60%);
  filter: blur(20px);
  opacity: .55;
  pointer-events: none;
}

/* 카드 */
.card {
  width: 100%;
  max-width: 880px;
  background: var(--card-bg, rgba(255,255,255,0.82));
  backdrop-filter: saturate(140%) blur(6px);
  border: 1px solid var(--card-border, rgba(234,234,234,0.9));
  border-radius: 20px;
  padding: 28px 24px 22px;
  box-shadow: 0 14px 40px rgba(0,0,0,0.08);
  text-align: center;
}

.title {
  margin: 0;
  font-size: clamp(22px, 3vw, 32px);
  font-weight: 900;
  letter-spacing: -0.02em;
  color: #0f172a; /* slate-900 */
}

.sub {
  margin: 8px 0 16px;
  color: #475569; /* slate-600 */
  font-size: 14px;
}

/* 버튼 */
.cta { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; margin-top: 6px; }

.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 11px 14px;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  user-select: none;
  transition: transform .03s ease, background .2s ease, color .2s ease, border-color .2s ease, box-shadow .2s ease;
}
.btn:active { transform: translateY(1px); }

.btn-primary {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 10px 24px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; border-color: #1d4ed8; }

.btn-ghost {
  background: transparent; color: #1f2937; border: 1px solid #e5e7eb;
}
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }

.btn-soft {
  background: #f1f5f9; color: #0f172a; border: 1px solid #e2e8f0;
}
.btn-soft:hover { background: #e2e8f0; }

/* 특징 섹션 */
.features {
  width: 100%;
  max-width: 960px;
  display: grid;
  grid-template-columns: repeat(1, minmax(0,1fr));
  gap: 12px;
}
.feature {
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 16px;
  padding: 16px 14px;
  text-align: center;
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
  transition: transform .06s ease, box-shadow .2s ease, border-color .2s ease;
}
.feature:hover {
  transform: translateY(-2px);
  border-color: #dbeafe; /* blue-100 */
  box-shadow: 0 14px 32px rgba(0,0,0,0.08);
}
.icon { font-size: 22px; line-height: 1; }
.feature h3 {
  margin: 8px 0 4px;
  font-size: 15px; font-weight: 800; color: #0f172a;
}
.feature p {
  margin: 0; color: #64748b; font-size: 13px;
}

/* 반응형 */
@media (max-width: 760px) {
  .features { grid-template-columns: 1fr; }
}

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card {
    --card-bg: rgba(11,18,32,0.8);
    --card-border: rgba(31,41,55,0.9);
    color: #e5e7eb;
  }
  .title { color: #e5e7eb; }
  .sub { color: #94a3b8; }
  .btn-ghost { color: #e5e7eb; border-color: #334155; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
  .btn-soft { background: #0b1220; color: #e5e7eb; border-color: #334155; }
  .btn-soft:hover { background: #0f172a; }
  .feature {
    --card-bg: #0b1220;
    --card-border: #1f2937;
    color: #e5e7eb;
  }
  .feature h3 { color: #e5e7eb; }
  .feature p { color: #cbd5e1; }
}

.posts{width: 100%;}
</style>

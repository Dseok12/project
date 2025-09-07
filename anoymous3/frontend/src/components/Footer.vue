<script setup>
const year = new Date().getFullYear()
</script>

<template>
  <footer class="footer">
    <div class="inner">
      <div class="brand">
        <strong class="logo">Anoy</strong>
        <span class="tagline">익명 커뮤니티</span>
      </div>

      <nav class="links" aria-label="푸터 링크">
        <router-link to="/terms">이용약관</router-link>
        <!-- 필요하면 추가 -->
        <!-- <router-link to="/privacy">개인정보처리방침</router-link> -->
        <!-- <router-link to="/guidelines">커뮤니티 가이드라인</router-link> -->
      </nav>

      <p class="disclaimer">
        어노이는 이용자 간 익명 소통 플랫폼입니다. 성매매 알선·유인, 불법 약물/의약품 거래, 협박·갈취 등
        <b>불법 행위는 엄격히 금지</b>되며, 관련 게시물/계정에 대해 사전 통지 없이 제한될 수 있습니다.
        서비스 제공자는 이용자의 불법 행위에 대해 법령상 허용되는 범위 내에서만 책임을 부담합니다.
        자세한 내용은 <router-link to="/terms"><b>이용약관</b></router-link>을 확인해 주세요.
      </p>

      <div class="meta">
        <span>© {{ year }} Anoy. All rights reserved.</span>
        <span class="dot">•</span>
        <a href="mailto:wkqxhd2020@naver.com" class="contact">문의: wkqxhd2020@naver.com</a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  margin-top: 28px;
  border-top: 1px solid #e5e7eb;
  background: #fafafa;
  color: #475569;
}
.inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 18px 16px 26px;
}
.brand {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.logo { font-weight: 900; letter-spacing: -0.01em; }
.tagline { font-size: 12px; color: #64748b; }

.links {
  display: flex;
  gap: 14px;
  margin: 10px 0 8px;
}
.links a {
  color: #334155;
  text-decoration: none;
  border-bottom: 1px dashed transparent;
}
.links a:hover { border-bottom-color: #94a3b8; }

.disclaimer {
  font-size: 12px;
  line-height: 1.7;
  color: #64748b;
  margin: 6px 0 10px;
}
.disclaimer b { color: #0f172a; }

.meta {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 12px;
  color: #94a3b8;
}
.dot { opacity: .7; }
.contact { color: inherit; text-decoration: none; }
.contact:hover { text-decoration: underline; }

@media (prefers-color-scheme: dark) {
  .footer { background: #0b1220; border-top-color: #1f2937; color: #94a3b8; }
  .links a { color: #cbd5e1; }
  .disclaimer b { color: #e5e7eb; }
}
</style>

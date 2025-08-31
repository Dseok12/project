<script setup>
const updated = '2025-08-31'
</script>

<template>
  <section class="wrap">
    <h1>이용약관</h1>
    <p class="updated">최종 업데이트: {{ updated }}</p>

    <h2>1. 서비스 성격</h2>
    <p>
      어노이(이하 “서비스”)는 이용자 간 익명 소통을 위한 플랫폼입니다.
      서비스 제공자는 게시물의 사전 검열을 하지 않으며, 이용자가 게시·전송한 모든 콘텐츠(이하 “이용자 콘텐츠”)는
      해당 이용자의 책임하에 게시됩니다.
    </p>

    <h2>2. 금지 행위</h2>
    <p>아래 행위를 엄격히 금지합니다. 위반 시 사전 통지 없이 게시물 삭제, 이용 제한, 수사기관 협조 등 조치가 가능합니다.</p>
    <ul>
      <li>성매매 알선·유인, 음란물·불법 촬영물 유통</li>
      <li>불법 약물/의약품 거래, 불법 무기/도구 거래, 청소년 유해물 배포</li>
      <li>협박·갈취·스토킹·사기 등 범죄를 구성하는 행위</li>
      <li>저작권·상표권·초상권 등 제3자 권리 침해</li>
      <li>개인정보·사생활 침해, 명예훼손, 차별·혐오표현</li>
      <li>불법 도박·불법 리베이트·다단계 권유 등 불법 경제행위</li>
      <li>악성 코드·피싱·스팸, 서비스/시스템 장애 유발 행위</li>
    </ul>

    <h2>3. 책임의 한계(면책)</h2>
    <p>
      서비스 제공자는 이용자 콘텐츠에 대해 법령상 요구되는 경우를 제외하고 사실 확인의무 또는
      일반 감시의무를 부담하지 않습니다. 이용자의 불법 행위로 인한 손해에 대하여
      서비스 제공자는 <b>법령상 허용되는 범위 내에서만 책임</b>을 부담합니다.
      서비스 제공자는 직접적·간접적·부수적 손해, 영업상 손실 등에 대해 책임지지 않습니다.
    </p>

    <h2>4. 이용자 책임</h2>
    <p>
      이용자는 자신이 게시·전송하는 모든 콘텐츠와 그로부터 발생하는 결과에 대해 전적으로 책임집니다.
      이용자는 관련 법령과 본 약관, 운영정책을 준수해야 합니다.
    </p>

    <h2>5. 조치 및 통지</h2>
    <p>
      서비스 제공자는 신고 접수, 권리침해 주장, 법령·수사기관 요청, 내부 정책 위반 판단 시
      게시물 삭제, 노출 제한, 계정 정지 등 필요한 조치를 취할 수 있습니다.
      긴급한 경우 사후 통지할 수 있습니다.
    </p>

    <h2>6. 신고/문의</h2>
    <p>
      불법·유해 게시물 신고 및 권리침해 고지는
      <a href="mailto:support@anoy.example">support@anoy.example</a> 로 접수해 주세요.
      (가능하면 URL, 스크린샷, 사유를 함께 제공해 주세요.)
    </p>

    <h2>7. 준거법 및 분쟁해결</h2>
    <p>
      본 약관은 서비스 제공자가 정한 관할 지역의 법률에 따르며, 분쟁은 해당 법원에 전속 관할로 합니다.
    </p>

    <p class="legal-note">
      * 본 문서는 일반적인 참고용 예시입니다. 실제 서비스 운영 시 사업자 정보, 관할, 청소년 보호정책,
      전자상거래 고지사항, 위치기반서비스 약관, 광고정책 등 추가 문서가 필요할 수 있습니다.
    </p>
  </section>
</template>

<style scoped>
.wrap {
  max-width: 860px;
  margin: 24px auto;
  padding: 0 16px 40px;
  line-height: 1.7;
  color: #111827;
}
h1 { font-size: 28px; font-weight: 900; margin: 6px 0 2px; letter-spacing: -0.01em; }
.updated { color: #6b7280; font-size: 13px; margin-bottom: 18px; }
h2 { font-size: 18px; font-weight: 800; margin: 18px 0 8px; }
ul { padding-left: 18px; }
li { margin: 4px 0; }
a { color: #2563eb; text-decoration: none; }
a:hover { text-decoration: underline; }
.legal-note {
  margin-top: 20px;
  font-size: 12px;
  color: #6b7280;
  background: #f8fafc;
  border: 1px dashed #e2e8f0;
  padding: 10px 12px;
  border-radius: 10px;
}

@media (prefers-color-scheme: dark) {
  .wrap { color: #e5e7eb; }
  .updated, .legal-note { color: #94a3b8; }
  .legal-note { background: #0b1220; border-color: #1f2937; }
  a { color: #60a5fa; }
}
</style>

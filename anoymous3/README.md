# 📝 어노이(Anoy) - 익명 커뮤니티 플랫폼

> 익명 활동 아이디로 느슨하지만 안전하게 연결되는 커뮤니티. 따끈한 글 피드를 즐기고, 탄탄한 백오피스로 운영자를 돕는 풀스택 프로젝트입니다.

## 🗂️ 목차
1. [서비스 한눈에 보기](#-서비스-한눈에-보기)
2. [핵심 기능](#-핵심-기능)
3. [아름답고 반응형 UI](#-아름답고-반응형-ui)
4. [백엔드 아키텍처 & 보안](#-백엔드-아키텍처--보안)
5. [프론트엔드 구조](#-프론트엔드-구조)
6. [기술 스택](#-기술-스택)
7. [로컬 실행 가이드](#-로컬-실행-가이드)
8. [향후 로드맵](#-향후-로드맵)
9. [라이선스](#-라이선스)

---

## 🚀 서비스 한눈에 보기
- **익명성 강화**: 가입 시 발급되는 활동 아이디만 노출되며, 이메일은 안전하게 보관됩니다.
- **피드 중심 경험**: 홈 히어로 섹션과 최신 글 하이라이트가 자연스럽게 글 읽기·작성 흐름으로 안내합니다.
- **운영자 친화**: 공지, 게시물, 댓글, 사용자까지 하나의 관리자 API로 통합 관리할 수 있습니다.
- **보안 기본기**: JWT 기반 인증, 역할/상태 제어, 소프트 삭제, CORS 정책 등 실제 서비스 수준의 방어선이 갖춰져 있습니다.

---

## 🎯 핵심 기능
### 👤 일반 사용자
- 이메일 인증 + 활동 아이디 중복 검사 후 회원가입, JWT 로그인, 자동 세션 복원
- 게시물 CRUD, 줄바꿈 그대로 저장되는 롱폼 콘텐츠, 나의 글만 필터링
- 댓글/대댓글 작성·수정·삭제, 좋아요/싫어요 토글 반응, 마이페이지에서 활동 아이디 변경 예정

### 🛡️ 관리자(Admin)
- 사용자 목록 검색, 역할(USER/ADMIN) 변경, 상태(Active/Suspended/Deleted) 관리 및 소프트 삭제
- 공지 토글/작성/수정/삭제, 게시글/댓글 일괄 관리, 운영 품질 모니터링을 위한 관리자 전용 페이징 API 제공

### 💬 커뮤니케이션 & 상호작용
- 댓글 트리 & 반응 집계 API로 활발한 피드백 루프를 구성
- 게시글·댓글은 삭제해도 소프트 삭제로 데이터 추적 가능, 필요 시 복구 전략 수립 용이

---

## 🎨 아름답고 반응형 UI
- **AppShell 레이아웃**으로 네비게이션, 본문, 푸터를 감싸 PC·태블릿·모바일에서 균형 잡힌 여백과 그라데이션 배경을 제공합니다.
- **Home 히어로 카드**는 가입/로그인/글쓰기 CTA를 상황별로 바꾸고, 익명 커뮤니티 컨셉을 감성적인 색감으로 표현합니다.
- **PostHighlights + PostList** 조합이 최신 글 4건을 스켈레톤 로딩과 함께 보여주고, 카드형 UI는 접근성 키보드 포커스를 지원합니다.
- **SkeletonList**는 로딩 구간에도 일관된 카드 레이아웃과 쉐이머 애니메이션으로 체감 속도를 높입니다.
- **PostCard**의 익명 아이디 마스킹, 다크 모드 컬러, 반응형 그리드가 간결하지만 탄탄한 읽기 경험을 만들어 줍니다.

---

## 🔐 백엔드 아키텍처 & 보안
- **Spring Boot 3 + JPA** 기반 도메인: User, Post, Comment, Reaction, EmailVerification 등으로 구성되어 있고, 게시물은 소프트 삭제 전략을 사용합니다.
- **Auth 파이프라인**: 활동 아이디 중복 확인, 이메일 인증, 회원가입, JWT 로그인까지 `/api/auth/**`로 일원화되어 있습니다.
- **JWT 보안**: 최소 256비트 비밀키 검증, 만료 시간 서명, 파싱 실패 시 안전하게 무시하도록 설계되었습니다.
- **SecurityConfig**: SPA 환경에 맞춘 stateless 설정, 공개/보호/관리자 라우트 세분화, Vite 개발 서버를 위한 CORS 허용을 제공합니다.
- **관리 도구**: `/api/admin/**` 엔드포인트가 사용자·게시물·댓글·공지 토글/생성/삭제 등 운영 기능을 폭넓게 제공합니다.

---

## 🧭 프론트엔드 구조
- **Vue 3 + Vite** 조합으로 빠른 개발 경험과 빌드 성능 확보
- **Vue Router**: 홈, 공지, 게시판, 상세, 새 글 작성/수정, 로그인/회원가입, 마이페이지, 관리자 전용 뷰로 구성
- **Vuex 모듈화**: `auth` 모듈은 토큰/활동 아이디/역할을 세션·로컬 스토리지와 동기화하고, `posts` 모듈은 페이지네이션·에러·로딩 상태를 통합 관리합니다.
- **Composable (`useAuth`)**로 뷰 컴포지션에서 인증 로직 재사용
- **공통 컴포넌트**: `AppShell`, `AppPagination`, `SkeletonList`, `PostCard`, `PostHighlights` 등으로 재사용성과 일관성 강화

---

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-59666C?style=flat&logo=hibernate)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat&logo=mysql&logoColor=white)
![JavaMail](https://img.shields.io/badge/Spring_Mail-007396?style=flat&logo=gmail&logoColor=white)

### Frontend
![Vue.js](https://img.shields.io/badge/Vue_3-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vuex](https://img.shields.io/badge/Vuex-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vue Router](https://img.shields.io/badge/Vue_Router-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=FFD62E)
![Axios](https://img.shields.io/badge/Axios-671DDF?style=flat&logo=axios&logoColor=white)
![SCSS](https://img.shields.io/badge/SCSS-CC6699?style=flat&logo=sass&logoColor=white)

### Infra & Ops
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)
![FTP](https://img.shields.io/badge/FTP-FF8C00?style=flat&logo=files&logoColor=white)
![Actuator](https://img.shields.io/badge/Spring_Actuator-6DB33F?style=flat&logo=spring)

---

## 🧪 로컬 실행 가이드
### 1) Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
> 실행 전에 `application.yml` 또는 환경 변수로 DB 접속 정보, `jwt.secret`, `jwt.expirationMillis`, 메일 서버 설정을 주입하세요.

### 2) Frontend (Vite)
```bash
cd frontend
npm install
npm run dev
```
> 개발 서버는 기본적으로 http://localhost:5173 에서 구동되며, 백엔드 CORS는 http://localhost:3000 기준으로 열려 있으니 필요 시 `SecurityConfig`의 허용 오리진을 조정해주세요.

### 3) Build & Preview
```bash
# 프론트 빌드
npm run build
npm run preview
```

---

## 🗺 향후 로드맵
- 프로필 intro 필드 & 이미지 업로드
- 게시물 검색/태그 시스템
- 실시간 알림(Notification) & 메일 요약
- 관리자 통계 대시보드 및 감사 로그
- Docker 기반 CI/CD 파이프라인 자동화

---

## 📄 라이선스
MIT License © 2025

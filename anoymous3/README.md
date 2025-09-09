# 📝 어노이(Anoy) - 익명 커뮤니티 플랫폼

## 📌 서비스 개요
어노이(Anoy)는 **익명 소통 중심 커뮤니티 플랫폼**입니다.  
사용자는 익명 기반의 **활동 아이디(Activity ID)**로 글을 쓰고, 댓글을 달며, 반응(좋아요/싫어요)을 남길 수 있습니다.  
관리자는 유저/게시물/공지 관리를 통해 서비스 운영을 안정적으로 유지합니다.

---

## 🛠 기술 스택

### Backend
![Java](https://img.shields.io/badge/Java_17-007396?style=flat&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=spring-security&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat&logo=mysql&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=flat&logo=hibernate)

### Frontend
![Vue.js](https://img.shields.io/badge/Vue_3-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vuex](https://img.shields.io/badge/Vuex-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vue Router](https://img.shields.io/badge/Vue_Router-35495E?style=flat&logo=vuedotjs&logoColor=4FC08D)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=flat&logo=vite&logoColor=FFD62E)
![Axios](https://img.shields.io/badge/Axios-671DDF?style=flat&logo=axios&logoColor=white)
![SCSS](https://img.shields.io/badge/SCSS-CC6699?style=flat&logo=sass&logoColor=white)

### Infra
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white)
![FTP](https://img.shields.io/badge/FTP-FF8C00?style=flat&logo=files&logoColor=white)
![Spring Mail](https://img.shields.io/badge/JavaMailSender-007396?style=flat&logo=gmail&logoColor=white)

---

## 🎯 주요 기능

### 👤 사용자
- 회원가입 (이메일 인증 코드 필수, 활동 아이디 자동 생성/중복 확인 가능)
- 로그인 (JWT 기반, 세션 유지)
- 게시물 CRUD (공지글은 읽기 전용)
- 댓글/대댓글 작성 및 수정/삭제
- 반응(좋아요/싫어요)
- 마이페이지 (내 정보 조회, 활동 아이디 변경, 프로필 수정 예정)

### 🛡 관리자(Admin)
- 공지 관리 (등록/수정/삭제/토글)
- 게시물 관리 (목록 조회, 삭제)
- 댓글 관리 (삭제)
- 사용자 관리
  - 검색(이메일, 활동 아이디)
  - 권한 변경 (USER ↔ ADMIN)
  - 계정 정지 (기간 지정 or 영구)
  - 정지 해제
  - 탈퇴 처리(소프트 삭제)

---

## 🔒 보안/인증
- JWT 기반 인증 (`ROLE_USER` / `ROLE_ADMIN`)
- 사용자 상태: ACTIVE / SUSPENDED / DELETED
- Spring Security 필터 기반 인증/인가
- CORS 지원 (Vite 개발 서버 `localhost:3000` 허용)

---


## 🧭 향후 개선 계획
- 프로필 `intro` 필드 추가
- 게시물 검색/태그 기능
- 알림(Notification) 시스템
- 관리자 통계 대시보드
- Docker 기반 CI/CD 자동화

---

## 📄 라이선스
MIT License  
Copyright (c) 2025
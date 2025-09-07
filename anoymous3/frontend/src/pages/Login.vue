<script setup>
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import client from '@/api/client'

const router = useRouter()
const route = useRoute()
const store = useStore()

const email = ref('')
const password = ref('')
const showPw = ref(false)
const loading = ref(false)
const errorMsg = ref('')

const doLogin = async () => {
  if (!email.value?.trim() || !password.value) {
    errorMsg.value = '이메일과 비밀번호를 모두 입력하세요.'
    return
  }
  try {
    loading.value = true
    errorMsg.value = ''
    const { data } = await client.post('/auth/login', {
      email: email.value.trim(),
      password: password.value
    })
    // ✅ 백엔드 LoginRes: { token, activityId, role }
    const role = data.role || 'USER'

    await store.dispatch('login', {
      token: data.token,
      activityId: data.activityId,
      role,                // ✅ 꼭 저장
      persist: 'local'
    })

    // ✅ redirect 파라미터가 있으면 우선 사용
    const redirect = route.query.redirect
    if (typeof redirect === 'string' && redirect.startsWith('/')) {
      router.push(redirect)
    } else {
      // ADMIN이면 관리자 대시보드로, 아니면 홈으로
      router.push(role === 'ADMIN' ? '/admin' : '/')
    }
  } catch (e) {
    console.error(e)
    const msg = e?.response?.data?.error || '로그인 실패. 이메일/비밀번호를 확인하세요.'
    errorMsg.value = msg
  } finally {
    loading.value = false
  }
}
</script>


<template>
  <section class="login">
    <div class="card">
      <h2 class="title">로그인</h2>
      <p class="subtitle">어노이에 오신 걸 환영합니다.</p>

      <form @submit.prevent="doLogin" class="form">
        <div v-if="errorMsg" class="error-box">{{ errorMsg }}</div>

        <label for="email" class="label">이메일</label>
        <input
          id="email"
          v-model="email"
          type="email"
          class="input"
          :disabled="loading"
          autocomplete="username"
          placeholder="you@example.com"
        />

        <label for="password" class="label">비밀번호</label>
        <div class="pw-wrap">
          <input
            id="password"
            v-model="password"
            :type="showPw ? 'text' : 'password'"
            class="input pw"
            :disabled="loading"
            autocomplete="current-password"
            placeholder="••••••••"
          />
          <button
            type="button"
            class="btn btn-ghost pw-toggle"
            @click="showPw = !showPw"
            :aria-pressed="showPw"
            :disabled="loading"
          >
            {{ showPw ? '숨기기' : '표시' }}
          </button>
        </div>

        <button
          type="submit"
          class="btn btn-primary submit"
          :disabled="loading"
        >
          {{ loading ? '로그인 중…' : '로그인' }}
        </button>

        <div class="hint">
          아직 계정이 없으신가요?
          <router-link to="/signup" class="link">회원가입</router-link>
        </div>
      </form>
    </div>
  </section>
</template>

<style scoped>
.login {
  min-height: calc(100vh - 120px);
  display: grid;
  place-items: center;
  padding: 24px 16px;
}

.card {
  width: 100%;
  max-width: 480px;
  background: var(--card-bg, #fff);
  border: 1px solid var(--card-border, #eaeaea);
  border-radius: 18px;
  padding: 24px 22px 20px;
  box-shadow: 0 10px 28px rgba(0,0,0,0.07);
}

.title {
  margin: 0 0 6px;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: -0.01em;
}
.subtitle {
  margin: 0 0 18px;
  color: #6b7280; /* slate-500 */
  font-size: 13px;
}

.form {
  display: grid;
  gap: 10px;
}

.label {
  font-size: 12px;
  color: #374151;  /* gray-700 */
  font-weight: 600;
}

.input {
  width: 100%;
  padding: 12px 12px;
  border: 1px solid #e5e7eb; /* gray-200 */
  background: #fff;
  color: #111827; /* gray-900 */
  border-radius: 12px;
  font-size: 14px;
  outline: none;
  transition: border-color .2s ease, box-shadow .2s ease, background .2s ease;
}
.input:focus {
  border-color: #93c5fd; /* blue-300 */
  box-shadow: 0 0 0 4px rgba(59,130,246,0.15); /* blue-500/15% */
}

.pw-wrap {
  position: relative;
  display: flex;
  align-items: center;
}
.pw {
  padding-right: 72px;
}
.pw-toggle {
  position: absolute;
  right: 6px;
  height: 32px;
  padding: 0 10px;
}

/* 버튼 */
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  transition: transform .03s ease, box-shadow .2s ease, background .2s ease, color .2s ease, border-color .2s ease;
  user-select: none;
}
.btn:active { transform: translateY(1px); }

.btn-ghost {
  background: transparent;
  border-color: #e5e7eb;
  color: #374151;
}
.btn-ghost:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}

.btn-primary {
  background: #2563eb; /* blue-600 */
  color: #fff;
  box-shadow: 0 8px 20px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; } /* blue-700 */

.submit {
  margin-top: 8px;
}

.error-box {
  border: 1px solid #fecaca; /* red-200 */
  background: #fff1f2;       /* rose-50 */
  color: #991b1b;            /* red-800 */
  padding: 10px 12px;
  border-radius: 12px;
  font-size: 13px;
}

/* 힌트/링크 */
.hint {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
  text-align: right;
}
.link {
  color: #2563eb;
  font-weight: 700;
  text-decoration: none;
  margin-left: 4px;
}
.link:hover { text-decoration: underline; }

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card {
    --card-bg: #0b1220;
    --card-border: #1f2937;
    color: #e5e7eb;
  }
  .subtitle { color: #9ca3af; }
  .label { color: #e5e7eb; }
  .input {
    background: #0b1220;
    border-color: #334155;
    color: #e5e7eb;
  }
  .input:focus {
    border-color: #60a5fa;
    box-shadow: 0 0 0 4px rgba(96,165,250,0.2);
  }
  .btn-ghost { border-color: #334155; color: #e5e7eb; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
}
</style>

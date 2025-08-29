<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import client from '@/api/client'

const router = useRouter()
const route = useRoute()

// ===== 상태 =====
const email = ref('')
const password = ref('')
const passwordConfirm = ref('')
const showPw = ref(false)
const activityId = ref('')
const step = ref(1)

const checkIdMsg = ref('')
const idOk = ref(false)
const checkingId = ref(false)
const signingUp = ref(false)

// ===== 유틸: 랜덤 아이디 생성 =====
const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
const digits = '0123456789'
const randChars = (pool, n) => Array.from({ length: n }, () => pool[Math.floor(Math.random() * pool.length)]).join('')
const makeCandidateId = () => randChars(letters, 8) + randChars(digits, 3)  // [A-Za-z]{8}[0-9]{3}

// ===== 서버 중복 확인 API =====
const apiCheckAvailable = async (id) => {
  const { data } = await client.get('/auth/activity-id/available', { params: { activityId: id } })
  return !!data?.available
}

// ===== 비워두면 자동 생성해서 세팅 + 가용한 아이디 보장 =====
const ensureActivityId = async () => {
  const current = activityId.value?.trim()
  if (current) return current

  for (let i = 0; i < 10; i++) {
    const candidate = makeCandidateId()
    try {
      const ok = await apiCheckAvailable(candidate)
      if (ok) {
        activityId.value = candidate
        idOk.value = true
        checkIdMsg.value = `자동 생성된 아이디가 할당되었습니다: ${candidate}`
        return candidate
      }
    } catch (e) {
      console.error('auto-generate check error', e)
      break
    }
  }
  // 모두 실패 시 마지막 후보를 넣되, 사용 불가 표시
  const fallback = makeCandidateId()
  activityId.value = fallback
  idOk.value = false
  checkIdMsg.value = '자동 생성 아이디가 모두 중복됩니다. 다른 아이디를 입력하고 중복 확인을 눌러주세요.'
  return fallback
}

// ===== 수동/자동 모두 대응하는 중복 확인 =====
const checkActivityId = async () => {
  const id = activityId.value?.trim()
  if (!id) {
    await ensureActivityId()
    return
  }
  try {
    checkingId.value = true
    const ok = await apiCheckAvailable(id)
    idOk.value = ok
    checkIdMsg.value = ok ? '사용 가능한 아이디입니다.' : '이미 사용 중인 아이디입니다.'
  } catch (e) {
    idOk.value = false
    checkIdMsg.value = '중복 확인에 실패했습니다. 잠시 후 다시 시도해주세요.'
    console.error('activityId check error', e)
  } finally {
    checkingId.value = false
  }
}

// ===== 이메일 인증 단계(개발 모드 비활성) =====
const sendCode = async () => {
  alert('개발 모드: 이메일 인증을 임시로 사용하지 않습니다.')
}
const verify = async () => {
  alert('개발 모드: 인증 과정을 생략합니다.')
}

// ===== 비밀번호 강도/검증 =====
const pwLenOk = computed(() => (password.value?.length || 0) >= 8)
const pwHasLetter = computed(() => /[A-Za-z]/.test(password.value || ''))
const pwHasDigit = computed(() => /\d/.test(password.value || ''))
const pwMatch = computed(() => (password.value || '') === (passwordConfirm.value || ''))
const pwScore = computed(() => [pwLenOk.value, pwHasLetter.value, pwHasDigit.value].filter(Boolean).length) // 0~3

const canSubmit = computed(() =>
  !!email.value?.trim() &&
  !!password.value &&
  !!passwordConfirm.value &&
  pwMatch.value
)

// ===== 회원가입 =====
const signup = async () => {
  // 아이디가 비었으면 자동 생성 + 중복 회피
  await ensureActivityId()
  // 최종 중복 체크(수동 입력 케이스 포함)
  await checkActivityId()
  // 정책상 강제 차단을 원하면 다음 줄 주석 해제
  // if (!idOk.value) return alert('활동 아이디 중복 확인을 통과해야 합니다.')

  if (!email.value?.trim()) return alert('이메일을 입력하세요.')
  if (!password.value || !passwordConfirm.value) return alert('비밀번호를 입력하세요.')
  if (!pwMatch.value) return alert('비밀번호 확인이 일치하지 않습니다.')

  try {
    signingUp.value = true
    await client.post('/auth/signup', {
      email: email.value.trim(),
      password: password.value,
      activityId: activityId.value.trim()
    })
    alert('회원가입 완료! 로그인 페이지로 이동합니다.')
    router.push({ path: '/login', query: { email: email.value.trim() } })
  } catch (e) {
    const msg = e?.response?.data?.error || '회원가입에 실패했습니다.'
    alert(msg)
    console.error('signup error', e)
  } finally {
    signingUp.value = false
  }
}

// ===== 엔터 제출 지원 =====
const onSubmit = (e) => {
  e.preventDefault()
  if (!signingUp.value) signup()
}

// ===== 초기값: 로그인 페이지에서 전달한 이메일 자동 채움 =====
watch(
  () => route.query.email,
  (v) => {
    if (typeof v === 'string' && v) email.value = v
  },
  { immediate: true }
)
</script>

<template>
  <section class="signup">
    <div class="bg"></div>

    <div class="card">
      <header class="head">
        <h2 class="title">회원가입</h2>
        <p class="sub">익명 커뮤니티 <b>anoy</b>에 오신 걸 환영합니다.</p>
      </header>

      <form class="form" @submit.prevent="onSubmit">
        <!-- 이메일 -->
        <label class="label" for="email">이메일</label>
        <input
          id="email"
          v-model="email"
          type="email"
          class="input"
          placeholder="you@example.com"
          autocomplete="username"
          required
        />
        <button class="btn btn-ghost small" type="button" @click="sendCode">이메일 인증 코드 받기(비활성화)</button>

        <hr class="hr" />

        <!-- 활동 아이디 -->
        <div class="row">
          <div class="col">
            <label class="label" for="aid">활동 아이디</label>
            <input
              id="aid"
              v-model="activityId"
              class="input"
              placeholder="미입력 시 자동 생성됩니다 (영문 8 + 숫자 3)"
              @blur="checkActivityId"
            />
          </div>
          <div class="col fit">
            <label class="label hidden"> </label>
            <div class="btns">
              <button class="btn btn-ghost" type="button" @click="checkActivityId" :disabled="checkingId">
                {{ checkingId ? '확인 중…' : '중복 확인' }}
              </button>
              <button class="btn btn-soft" type="button" @click="ensureActivityId">자동 생성</button>
            </div>
          </div>
        </div>
        <div class="id-msg" :class="{ ok: idOk }">{{ checkIdMsg }}</div>

        <!-- 비밀번호 -->
        <label class="label" for="pw">비밀번호</label>
        <div class="pw-wrap">
          <input
            :type="showPw ? 'text' : 'password'"
            id="pw"
            v-model="password"
            class="input"
            placeholder="8자 이상, 영문+숫자 권장"
            autocomplete="new-password"
            required
          />
          <button type="button" class="pw-toggle" @click="showPw = !showPw">{{ showPw ? '숨기기' : '보기' }}</button>
        </div>

        <label class="label" for="pw2">비밀번호 확인</label>
        <input
          :type="showPw ? 'text' : 'password'"
          id="pw2"
          v-model="passwordConfirm"
          class="input"
          placeholder="다시 입력"
          autocomplete="new-password"
          required
        />

        <!-- 강도 표시 -->
        <div class="meter">
          <span :class="{ on: pwScore >= 1 }"></span>
          <span :class="{ on: pwScore >= 2 }"></span>
          <span :class="{ on: pwScore >= 3 }"></span>
        </div>
        <ul class="hints">
          <li :class="{ ok: pwLenOk }">8자 이상</li>
          <li :class="{ ok: pwHasLetter }">영문 포함</li>
          <li :class="{ ok: pwHasDigit }">숫자 포함</li>
          <li :class="{ ok: pwMatch }">비밀번호 확인 일치</li>
        </ul>

        <!-- 제출 -->
        <button class="btn btn-primary submit" type="submit" :disabled="signingUp || !canSubmit">
          {{ signingUp ? '가입 중…' : '회원가입 완료' }}
        </button>

        <p class="after">
          이미 계정이 있나요?
          <router-link to="/login">로그인</router-link>
        </p>
      </form>
    </div>
  </section>
</template>

<style scoped>
.signup {
  position: relative;
  min-height: calc(100vh - 120px);
  display: grid;
  place-items: start center;
  padding: 32px 16px 48px;
}
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
  max-width: 760px;
  background: var(--card-bg, rgba(255,255,255,0.82));
  border: 1px solid var(--card-border, rgba(234,234,234,0.9));
  backdrop-filter: saturate(140%) blur(6px);
  border-radius: 20px;
  padding: 22px 20px 18px;
  box-shadow: 0 14px 40px rgba(0,0,0,0.08);
}
.head { text-align: center; margin-bottom: 8px; }
.title { margin: 0; font-size: 24px; font-weight: 900; letter-spacing: -0.02em; color: #0f172a; }
.sub { margin: 6px 0 0; color: #475569; font-size: 13px; }

.form { display: grid; gap: 10px; margin-top: 12px; }
.label { font-size: 12px; color: #374151; font-weight: 700; }
.input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  background: #fff; color: #111827;
  font-size: 14px; outline: none;
  transition: border-color .2s, box-shadow .2s;
}
.input:focus { border-color: #93c5fa; box-shadow: 0 0 0 4px rgba(59,130,246,0.15); }
.hr { border: none; border-top: 1px dashed #e5e7eb; margin: 8px 0; }

.row { display: grid; gap: 10px; grid-template-columns: 1fr; }
.col.fit { display: grid; align-content: end; }
.btns { display: flex; gap: 8px; }
.hidden { visibility: hidden; }

/* 버튼 */
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
  user-select: none;
  transition: transform .03s, background .2s, color .2s, border-color .2s, box-shadow .2s;
}
.btn:active { transform: translateY(1px); }
.btn-ghost { background: transparent; color: #374151; border: 1px solid #e5e7eb; }
.btn-ghost:hover { background: #f9fafb; border-color: #d1d5db; }
.btn-soft { background: #f1f5f9; color: #0f172a; border: 1px solid #e2e8f0; }
.btn-soft:hover { background: #e2e8f0; }
.btn-primary {
  background: #2563eb; color: #fff; border-color: #2563eb;
  box-shadow: 0 10px 24px rgba(37,99,235,0.25);
}
.btn-primary:hover { background: #1d4ed8; border-color: #1d4ed8; }
.small { padding: 6px 10px; font-size: 12px; }

/* 아이디 중복 메시지 */
.id-msg { min-height: 20px; font-size: 13px; color: crimson; }
.id-msg.ok { color: green; }

/* 비밀번호 */
.pw-wrap { position: relative; }
.pw-toggle {
  position: absolute; right: 8px; top: 50%; transform: translateY(-50%);
  padding: 6px 8px; font-size: 12px; border-radius: 8px;
  background: #f3f4f6; border: 1px solid #e5e7eb; color: #374151; cursor: pointer;
}
.pw-toggle:hover { background: #e5e7eb; }

/* 강도 인디케이터 */
.meter { display: grid; grid-template-columns: repeat(3, 1fr); gap: 6px; margin-top: 2px; }
.meter span {
  height: 6px; border-radius: 8px; background: #e5e7eb; transition: background .2s, box-shadow .2s;
}
.meter span.on { background: #60a5fa; box-shadow: 0 4px 10px rgba(96,165,250,0.35); }

.hints {
  display: flex; gap: 10px; flex-wrap: wrap; margin: 0; padding: 0; list-style: none;
  color: #6b7280; font-size: 12px;
}
.hints li.ok { color: #16a34a; }

/* 제출/후행 */
.submit { margin-top: 6px; }
.after { text-align: center; font-size: 13px; color: #64748b; }
.after a { color: #2563eb; text-decoration: none; }
.after a:hover { text-decoration: underline; }

/* 반응형 */
@media (min-width: 680px) {
  .row { grid-template-columns: 1fr auto; align-items: end; }
}

/* 다크 모드 */
@media (prefers-color-scheme: dark) {
  .card { --card-bg: rgba(11,18,32,0.82); --card-border: rgba(31,41,55,0.9); color: #e5e7eb; }
  .title { color: #e5e7eb; }
  .sub { color: #94a3b8; }
  .input { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .input:focus { border-color: #60a5fa; box-shadow: 0 0 0 4px rgba(96,165,250,0.2); }
  .btn-ghost { color: #e5e7eb; border-color: #334155; }
  .btn-ghost:hover { background: #0f172a; border-color: #475569; }
  .btn-soft { background: #0b1220; color: #e5e7eb; border-color: #334155; }
  .btn-soft:hover { background: #0f172a; }
  .pw-toggle { background: #0b1220; border-color: #334155; color: #e5e7eb; }
  .meter span { background: #1f2937; }
}
</style>

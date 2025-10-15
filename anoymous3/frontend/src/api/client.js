// frontend/src/api/client.js
import axios from 'axios'
import store from '@/store'

// env 값 정리(뒤의 / 제거)
const normalize = (v) => (typeof v === 'string' ? v.replace(/\/+$/, '') : '')
const envBase = normalize(import.meta.env.VITE_API_BASE)

// env 없으면 /api 폴백 -> Vite proxy가 8080으로 전달
const baseURL = envBase || '/api'

const client = axios.create({
  baseURL,
  withCredentials: false,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    'X-Requested-With': 'XMLHttpRequest',
  },
})

// ✅ 요청 인터셉터: 토큰 자동 첨부 (store 우선, 그다음 session/localStorage)
client.interceptors.request.use((config) => {
  const token =
    store.state.auth?.token ||
    sessionStorage.getItem('token') ||
    localStorage.getItem('token')

  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ✅ 응답 인터셉터: 401/403 공통 처리 (세션 정리 + 로그인 이동)
client.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status
    if (status === 401) {
      // 인증 만료/무효 → 스토어와 저장소 정리
      try { store.dispatch('auth/logout') } catch {}
      // 현재 페이지를 redirect로 전달
      const redirect = encodeURIComponent(location.pathname + location.search)
      window.location.assign(`/login?redirect=${redirect}`)
    } else if (status === 403) {
      // 권한 부족: 알림만 띄우고 홈으로 보낼지 결정
      // 필요 시 아래 사용
      // alert('권한이 없습니다.')
    }
    return Promise.reject(err)
  }
)

export default client

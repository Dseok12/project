// frontend/src/api/client.js
import axios from 'axios'

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

// ✅ 요청 인터셉터: 토큰 자동 첨부 (sessionStorage 우선, 없으면 localStorage)
client.interceptors.request.use((config) => {
  const token =
    sessionStorage.getItem('token') || localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ✅ 응답 인터셉터: 필요 시 공통 에러 처리 지점
client.interceptors.response.use(
  (res) => res,
  (err) => {
    // 예) 401이면 저장소 정리 (원하면 주석 해제)
    // if (err?.response?.status === 401) {
    //   sessionStorage.removeItem('token')
    //   sessionStorage.removeItem('activityId')
    //   localStorage.removeItem('token')
    //   localStorage.removeItem('activityId')
    // }
    return Promise.reject(err)
  }
)

export default client

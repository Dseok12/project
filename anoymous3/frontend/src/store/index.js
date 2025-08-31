// src/store/index.js
import { createStore } from 'vuex'
import client from '@/api/client' // ✅ 토큰을 Axios 헤더와 동기화

// JWT payload 파서 (role/exp 추출)
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch {
    return null
  }
}

// Axios Authorization 헤더 갱신
function updateAuthHeader(token) {
  if (token) {
    client.defaults.headers.common.Authorization = `Bearer ${token}`
  } else {
    delete client.defaults.headers.common.Authorization
  }
}

export default createStore({
  state: {
    token: null,
    activityId: null,
    role: null, // 'USER' | 'ADMIN'
  },
  getters: {
    isAuthed: (state) => !!state.token,
    isAdmin: (state) => state.role === 'ADMIN',
    authHeader: (state) => (state.token ? `Bearer ${state.token}` : null),
  },
  mutations: {
    SET_AUTH(state, { token, activityId, role }) {
      state.token = token
      state.activityId = activityId ?? null
      state.role = role ?? null
    },
    CLEAR_AUTH(state) {
      state.token = null
      state.activityId = null
      state.role = null
    },
  },
  actions: {
    // ✅ 앱 시작 시 저장소에서 복원(+JWT에서 role/만료 자동 보정)
    initFromStorage({ commit, dispatch }) {
      const token =
        sessionStorage.getItem('token') || localStorage.getItem('token')
      let activityId =
        sessionStorage.getItem('activityId') || localStorage.getItem('activityId')
      let role =
        sessionStorage.getItem('role') || localStorage.getItem('role')

      if (!token) {
        updateAuthHeader(null)
        return
      }

      // JWT에서 role/exp 보정
      const payload = parseJwt(token)
      if (payload?.exp && Date.now() / 1000 >= Number(payload.exp)) {
        // 만료 → 강제 로그아웃
        dispatch('logout')
        return
      }
      if (!role) {
        role = typeof payload?.role === 'string' ? payload.role : 'USER'
      }
      if (!activityId) {
        activityId = typeof payload?.activityId === 'string' ? payload.activityId : null
      }

      commit('SET_AUTH', { token, activityId, role })
      updateAuthHeader(token)
    },

    // ✅ 로그인 성공 시 저장 + 상태 반영 (백엔드에서 role도 내려옴)
    async login({ commit }, { token, activityId, role = 'USER', persist = 'local' }) {
      // 토큰 만료 체크(있으면)
      const payload = parseJwt(token)
      if (payload?.exp && Date.now() / 1000 >= Number(payload.exp)) {
        throw new Error('토큰이 만료되었습니다.')
      }
      if (!role) role = typeof payload?.role === 'string' ? payload.role : 'USER'
      if (!activityId) activityId = typeof payload?.activityId === 'string' ? payload.activityId : activityId

      if (persist === 'session') {
        sessionStorage.setItem('token', token)
        sessionStorage.setItem('activityId', activityId ?? '')
        sessionStorage.setItem('role', role ?? 'USER')
        localStorage.removeItem('token')
        localStorage.removeItem('activityId')
        localStorage.removeItem('role')
      } else {
        localStorage.setItem('token', token)
        localStorage.setItem('activityId', activityId ?? '')
        localStorage.setItem('role', role ?? 'USER')
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('activityId')
        sessionStorage.removeItem('role')
      }

      commit('SET_AUTH', { token, activityId, role })
      updateAuthHeader(token)
    },

    // ✅ 로그아웃
    async logout({ commit }) {
      localStorage.removeItem('token')
      localStorage.removeItem('activityId')
      localStorage.removeItem('role')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('activityId')
      sessionStorage.removeItem('role')
      commit('CLEAR_AUTH')
      updateAuthHeader(null)
    },

    // ✅ activityId만 갱신
    setActivityId({ commit, state }, activityId) {
      localStorage.setItem('activityId', activityId ?? '')
      sessionStorage.setItem('activityId', activityId ?? '')
      commit('SET_AUTH', { token: state.token, activityId, role: state.role })
    },

    // ✅ role만 갱신(관리자 승격 등)
    setRole({ commit, state }, role) {
      localStorage.setItem('role', role ?? 'USER')
      sessionStorage.setItem('role', role ?? 'USER')
      commit('SET_AUTH', { token: state.token, activityId: state.activityId, role })
    },
  },
})

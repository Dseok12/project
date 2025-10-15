import client from '@/api/client'

function parseJwt(token) {
  try {
    const [, base64Url] = token.split('.')
    if (!base64Url) return null
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.warn('parseJwt failed', error)
    return null
  }
}

function updateAuthHeader(token) {
  if (token) {
    client.defaults.headers.common.Authorization = `Bearer ${token}`
  } else {
    delete client.defaults.headers.common.Authorization
  }
}

const initialState = () => ({
  token: null,
  activityId: null,
  role: null,
})

export default {
  namespaced: true,
  state: initialState,
  getters: {
    isAuthed: (state) => Boolean(state.token),
    isAdmin: (state) => state.role === 'ADMIN',
    activityId: (state) => state.activityId,
    role: (state) => state.role ?? 'USER',
    authHeader: (state) => (state.token ? `Bearer ${state.token}` : null),
  },
  mutations: {
    SET_AUTH(state, { token, activityId, role }) {
      state.token = token
      state.activityId = activityId ?? null
      state.role = role ?? null
    },
    CLEAR_AUTH(state) {
      Object.assign(state, initialState())
    },
  },
  actions: {
    initFromStorage({ commit, dispatch }) {
      const token = sessionStorage.getItem('token') || localStorage.getItem('token')
      let activityId = sessionStorage.getItem('activityId') || localStorage.getItem('activityId')
      let role = sessionStorage.getItem('role') || localStorage.getItem('role')

      if (!token) {
        updateAuthHeader(null)
        return
      }

      const payload = parseJwt(token)
      if (payload?.exp && Date.now() / 1000 >= Number(payload.exp)) {
        dispatch('logout')
        return
      }

      if (!role) role = typeof payload?.role === 'string' ? payload.role : 'USER'
      if (!activityId) {
        activityId = typeof payload?.activityId === 'string' ? payload.activityId : null
      }

      commit('SET_AUTH', { token, activityId, role })
      updateAuthHeader(token)
    },

    async login({ commit }, { token, activityId, role = 'USER', persist = 'local' }) {
      const payload = parseJwt(token)
      if (payload?.exp && Date.now() / 1000 >= Number(payload.exp)) {
        throw new Error('토큰이 만료되었습니다.')
      }
      if (!role) role = typeof payload?.role === 'string' ? payload.role : 'USER'
      if (!activityId) activityId = typeof payload?.activityId === 'string' ? payload.activityId : activityId

      const targetStorage = persist === 'session' ? sessionStorage : localStorage
      const otherStorage = persist === 'session' ? localStorage : sessionStorage

      targetStorage.setItem('token', token)
      targetStorage.setItem('activityId', activityId ?? '')
      targetStorage.setItem('role', role ?? 'USER')

      otherStorage.removeItem('token')
      otherStorage.removeItem('activityId')
      otherStorage.removeItem('role')

      commit('SET_AUTH', { token, activityId, role })
      updateAuthHeader(token)
    },

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

    setActivityId({ commit, state }, activityId) {
      localStorage.setItem('activityId', activityId ?? '')
      sessionStorage.setItem('activityId', activityId ?? '')
      commit('SET_AUTH', { token: state.token, activityId, role: state.role })
    },

    setRole({ commit, state }, role) {
      localStorage.setItem('role', role ?? 'USER')
      sessionStorage.setItem('role', role ?? 'USER')
      commit('SET_AUTH', { token: state.token, activityId: state.activityId, role })
    },
  },
}

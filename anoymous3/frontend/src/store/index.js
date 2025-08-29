// src/store/index.js
import { createStore } from 'vuex'

export default createStore({
  state: {
    token: null,
    activityId: null,
  },
  getters: {
    isAuthed: (state) => !!state.token,
  },
  mutations: {
    SET_AUTH(state, { token, activityId }) {
      state.token = token
      state.activityId = activityId ?? null
    },
    CLEAR_AUTH(state) {
      state.token = null
      state.activityId = null
    },
  },
  actions: {
    // ⬇️ 에러의 원인인 이 액션을 추가
    initFromStorage({ commit }) {
      const token =
        sessionStorage.getItem('token') || localStorage.getItem('token')
      const activityId =
        sessionStorage.getItem('activityId') || localStorage.getItem('activityId')

      if (token) {
        commit('SET_AUTH', { token, activityId })
      }
    },

    // 이미 사용 중인 'login', 'logout'과 맞춰 작성
    async login({ commit }, { token, activityId, persist = 'local' }) {
      if (persist === 'session') {
        sessionStorage.setItem('token', token)
        sessionStorage.setItem('activityId', activityId)
        localStorage.removeItem('token')
        localStorage.removeItem('activityId')
      } else {
        localStorage.setItem('token', token)
        localStorage.setItem('activityId', activityId)
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('activityId')
      }
      commit('SET_AUTH', { token, activityId })
    },

    async logout({ commit }) {
      localStorage.removeItem('token')
      localStorage.removeItem('activityId')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('activityId')
      commit('CLEAR_AUTH')
    },

    // 선택: activityId만 갱신하고 싶을 때
    setActivityId({ commit, state }, activityId) {
      // 저장 위치는 상황에 맞게 (여기선 둘 다)
      localStorage.setItem('activityId', activityId)
      sessionStorage.setItem('activityId', activityId)
      commit('SET_AUTH', { token: state.token, activityId })
    },
  },
})

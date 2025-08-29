import { createStore } from 'vuex'

const getFromStorage = (key) =>
  sessionStorage.getItem(key) ?? localStorage.getItem(key)

export default createStore({
  state: () => ({
    token: getFromStorage('token') || null,
    activityId: getFromStorage('activityId') || null
  }),
  getters: {
    isAuthed: (state) => !!state.token
  },
  mutations: {
    SET_AUTH(state, { token, activityId }) {
      state.token = token
      state.activityId = activityId
    },
    CLEAR_AUTH(state) {
      state.token = null
      state.activityId = null
    }
  },
  actions: {
    /**
     * 로그인 성공 시 호출
     * persist: 'local' | 'session' (기본: 'local')
     */
    login({ commit }, { token, activityId, persist = 'local' }) {
      if (persist === 'session') {
        // 세션 유지(탭/브라우저 종료 시 소멸)
        sessionStorage.setItem('token', token)
        sessionStorage.setItem('activityId', activityId)
        // 다른 저장소는 정리
        localStorage.removeItem('token')
        localStorage.removeItem('activityId')
      } else {
        // 로컬에 영속 저장
        localStorage.setItem('token', token)
        localStorage.setItem('activityId', activityId)
        // 다른 저장소는 정리
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('activityId')
      }
      commit('SET_AUTH', { token, activityId })
    },

    /** 로그아웃: 두 스토리지 모두 정리 */
    logout({ commit }) {
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('activityId')
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('activityId')
      } finally {
        commit('CLEAR_AUTH')
      }
    }
  },
  modules: {}
})

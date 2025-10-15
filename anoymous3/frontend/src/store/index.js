import { createStore } from 'vuex'

import auth from './modules/auth'
import posts from './modules/posts'

const store = createStore({
  modules: {
    auth,
    posts,
  },
  getters: {
    isAuthed: (_, __, ___, rootGetters) => rootGetters['auth/isAuthed'],
    isAdmin: (_, __, ___, rootGetters) => rootGetters['auth/isAdmin'],
    authHeader: (_, __, ___, rootGetters) => rootGetters['auth/authHeader'],
  },
  actions: {
    initFromStorage({ dispatch }) {
      return dispatch('auth/initFromStorage')
    },
    login({ dispatch }, payload) {
      return dispatch('auth/login', payload)
    },
    logout({ dispatch }) {
      return dispatch('auth/logout')
    },
    setActivityId({ dispatch }, payload) {
      return dispatch('auth/setActivityId', payload)
    },
    setRole({ dispatch }, payload) {
      return dispatch('auth/setRole', payload)
    },
  },
})

export default store

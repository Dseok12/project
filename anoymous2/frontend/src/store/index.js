import { createStore } from 'vuex';

const store = createStore({
  state() {
    return {
      user: null,  // 로그인 사용자 정보 저장 (없으면 null)
    };
  },
  mutations: {
    setUser(state, userData) {
      state.user = userData;
    },
    clearUser(state) {
      state.user = null;
    }
  },
  actions: {
    login({ commit }, userData) {
      commit('setUser', userData);
    },
    logout({ commit }) {
      commit('clearUser');
    }
  },
  getters: {
    isLoggedIn(state) {
      return !!state.user;
    }
  }
});

export default store;

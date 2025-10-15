import { computed } from 'vue'
import { useStore } from 'vuex'

export function useAuth() {
  const store = useStore()

  const isAuthed = computed(() => store.getters['auth/isAuthed'])
  const isAdmin = computed(() => store.getters['auth/isAdmin'])
  const activityId = computed(() => store.state.auth.activityId)
  const role = computed(() => store.state.auth.role ?? 'USER')

  const initFromStorage = () => store.dispatch('auth/initFromStorage')
  const login = (payload) => store.dispatch('auth/login', payload)
  const logout = () => store.dispatch('auth/logout')
  const setActivityId = (payload) => store.dispatch('auth/setActivityId', payload)
  const setRole = (payload) => store.dispatch('auth/setRole', payload)

  return {
    store,
    isAuthed,
    isAdmin,
    activityId,
    role,
    initFromStorage,
    login,
    logout,
    setActivityId,
    setRole,
  }
}

export default useAuth

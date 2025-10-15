import client from '@/api/client'
import { normalizePostsResponse } from '@/utils/posts'

const initialState = () => ({
  items: [],
  page: 1,
  size: 20,
  totalPages: 1,
  totalElements: 0,
  loading: false,
  error: '',
})

export default {
  namespaced: true,
  state: initialState,
  getters: {
    list: (state) => state.items,
    isLoading: (state) => state.loading,
    error: (state) => state.error,
    pagination: (state) => ({
      page: state.page,
      size: state.size,
      totalPages: state.totalPages,
      totalElements: state.totalElements,
    }),
  },
  mutations: {
    SET_LOADING(state, value) {
      state.loading = value
    },
    SET_ERROR(state, message) {
      state.error = message ?? ''
    },
    SET_DATA(state, { items, page, size, totalPages, totalElements }) {
      state.items = items
      state.page = page
      state.size = size
      state.totalPages = totalPages
      state.totalElements = totalElements
    },
  },
  actions: {
    async fetchList({ commit, state }, { page = 1, size = state.size } = {}) {
      const safePage = Number.isFinite(page) && page > 0 ? Math.floor(page) : 1
      const safeSize = Number.isFinite(size) && size > 0 ? Math.floor(size) : state.size

      commit('SET_LOADING', true)
      commit('SET_ERROR', '')
      try {
        const { data } = await client.get('/posts', {
          params: {
            page: Math.max(0, safePage - 1),
            size: safeSize,
            sort: 'createdAt,desc',
          },
        })

        const parsed = normalizePostsResponse(data)
        const totalPages = Math.max(1, parsed.totalPages)
        const resolvedPage = Math.min(Math.max(parsed.pageNumber, 1), totalPages)

        commit('SET_DATA', {
          items: parsed.items,
          page: resolvedPage,
          size: safeSize,
          totalPages,
          totalElements: parsed.totalElements,
        })

        const corrected = resolvedPage !== safePage
        return { page: resolvedPage, totalPages, totalElements: parsed.totalElements, corrected }
      } catch (error) {
        const message = error?.response?.status
          ? `목록 로드 실패 (HTTP ${error.response.status})`
          : '게시물을 불러오지 못했습니다.'
        commit('SET_ERROR', message)
        console.error('posts/fetchList failed', error)
        return { page: safePage, totalPages: state.totalPages, totalElements: state.totalElements, error: message }
      } finally {
        commit('SET_LOADING', false)
      }
    },
  },
}

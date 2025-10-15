export function toNumber(value, fallback) {
  const num = Number(value)
  return Number.isFinite(num) ? num : fallback
}

export function normalizePostsResponse(payload) {
  if (payload && Array.isArray(payload.content)) {
    return {
      items: payload.content,
      totalPages: toNumber(payload.totalPages, 1),
      totalElements: toNumber(payload.totalElements, payload.content.length),
      pageNumber: toNumber(payload.number, 0) + 1,
    }
  }

  if (Array.isArray(payload)) {
    return {
      items: payload,
      totalPages: 1,
      totalElements: payload.length,
      pageNumber: 1,
    }
  }

  return { items: [], totalPages: 1, totalElements: 0, pageNumber: 1 }
}

export default {
  toNumber,
  normalizePostsResponse,
}

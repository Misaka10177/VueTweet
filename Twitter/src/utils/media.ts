import { API_BASE } from '@/config'

function apiOrigin() {
  try {
    return new URL(API_BASE, window.location.origin).origin
  } catch {
    return 'http://localhost:3000'
  }
}

/**
 * 解析后端返回的图片路径：
 * - /uploads/... 由 Spring Boot 提供，使用后端 origin
 * - /images/... 前端 public 资源，使用 Vite base
 * - 完整 URL / data URL 直接返回
 */
export function mediaUrl(path?: string | null) {
  if (!path) return ''
  if (/^(https?:)?\/\//i.test(path) || path.startsWith('data:')) return path
  if (path.startsWith('/uploads/')) return `${apiOrigin()}${path}`
  return `${import.meta.env.BASE_URL}${path.replace(/^\//, '')}`
}

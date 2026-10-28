// axios 实例与请求封装
import axios from 'axios'
import type { AxiosError, InternalAxiosRequestConfig } from 'axios'
import router from '@/router'
import { API_BASE } from '@/config'
import { useAuthStore } from '@/stores/auth'
import type { ApiError } from '@/types'

const isGitHubPages = import.meta.env.PROD

axios.defaults.baseURL = API_BASE
axios.defaults.timeout = 10000
axios.defaults.responseType = 'json'
axios.defaults.withCredentials = true

// Mock data for GitHub Pages static deployment
import { mockResponses } from './mockData'

type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean }

function getMockKey(method: string, url: string) {
  return `${method.toUpperCase()} ${url}`
}

if (isGitHubPages) {
  axios.defaults.adapter = (config) => {
    const method = (config.method || 'get').toUpperCase()
    const url = config.url || ''
    const key = getMockKey(method, url)
    const data = (mockResponses as Record<string, unknown>)[key]

    if (data !== undefined) {
      return Promise.resolve({
        data,
        status: 200,
        statusText: 'OK',
        headers: {},
        config,
      })
    }
    return Promise.reject(new Error(`Mock: no response for ${key}`))
  }
}

function isAuthEndpoint(url: string | undefined, endpoint: string) {
  return Boolean(url && url.includes(endpoint))
}

axios.interceptors.request.use((cfg) => {
  // Login and refresh must not carry a stale access token.
  if (isAuthEndpoint(cfg.url, '/auth/login') || isAuthEndpoint(cfg.url, '/auth/refresh')) {
    return cfg
  }
  const token = useAuthStore().accessToken
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

export function get<T>(url: string, params?: object): Promise<T> {
  return axios.get<T>(url, { params }).then((res) => res.data)
}

export function put<T>(url: string, data?: unknown): Promise<T> {
  return axios.put<T>(url, data).then((res) => res.data)
}

export function post<T>(url: string, data?: unknown): Promise<T> {
  return axios.post<T>(url, data).then((res) => res.data)
}

export function del<T>(url: string): Promise<T> {
  return axios.delete<T>(url).then((res) => res.data)
}

/**
 * multipart/form-data 专用请求。
 * 不手动设置 Content-Type，交给浏览器/axios 生成带 boundary 的头。
 */
export function postForm<T>(url: string, data: FormData): Promise<T> {
  return axios.post<T>(url, data).then((res) => res.data)
}

export function toApiError(err: unknown): ApiError | null {
  if (axios.isAxiosError<ApiError>(err)) {
    return err.response?.data ?? null
  }
  return null
}

axios.interceptors.response.use(
  (res) => res,
  async (err: AxiosError) => {
    const original = (err.config ?? {}) as RetryConfig
    const url: string = original.url || ''

    if (err.response?.status !== 401 || original._retry) {
      return Promise.reject(err)
    }
    // Login errors are shown by the form; refresh errors are handled by the store.
    if (isAuthEndpoint(url, '/auth/login') || isAuthEndpoint(url, '/auth/refresh')) {
      return Promise.reject(err)
    }

    original._retry = true
    const auth = useAuthStore()

    try {
      const token = await auth.refreshSession()
      original.headers = original.headers || {}
      original.headers.Authorization = `Bearer ${token}`
      return axios(original)
    } catch (refreshError) {
      auth.clearSession()
      const current = router.currentRoute.value
      if (current.path !== '/join' && current.path !== '/join/login') {
        router.push({ path: '/join', query: { redirect: current.fullPath } })
      }
      return Promise.reject(refreshError)
    }
  },
)

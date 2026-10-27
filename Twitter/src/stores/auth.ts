import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import axios from 'axios'

import { API_BASE } from '@/config'
import type { AuthResponse, AuthUser } from '@/types'

const isMock = import.meta.env.PROD
const REFRESH_MARGIN_MS = 5_000
const MOCK_USER: AuthUser = {
  id: 'Misaka10177',
  name: 'Misaka 10177',
  profilePhoto: '/images/img.png',
}

type SessionMessage = {
  type: 'session'
  accessToken: string
  expiresAt: number
  user: AuthUser
}

type LogoutMessage = { type: 'logout' }

type BroadcastMessage = SessionMessage | LogoutMessage

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref('')
  const expiresAt = ref(0)
  const user = ref<AuthUser | null>(null)
  const ready = ref(false)

  let bootstrapPromise: Promise<boolean> | null = null
  let refreshPromise: Promise<string> | null = null
  let restoreAttempted = false
  let channel: BroadcastChannel | null = null

  const isAuthenticated = computed(
    () => Boolean(accessToken.value) && Date.now() < expiresAt.value,
  )

  function initChannel() {
    if (channel || typeof BroadcastChannel === 'undefined') return
    channel = new BroadcastChannel('twitter-auth')
    channel.onmessage = (event: MessageEvent<BroadcastMessage>) => {
      const message = event.data
      if (!message) return
      if (message.type === 'session') {
        if (message.expiresAt > Date.now()) {
          accessToken.value = message.accessToken
          expiresAt.value = message.expiresAt
          user.value = message.user
          ready.value = true
        }
      } else if (message.type === 'logout') {
        clearLocal()
        import('@/router').then(({ default: router }) => {
          if (!router.currentRoute.value.path.startsWith('/join')) {
            router.push('/join')
          }
        })
      }
    }
  }

  function applySession(payload: AuthResponse, broadcast = true) {
    accessToken.value = payload.accessToken
    expiresAt.value = Date.now() + payload.expiresIn * 1000
    user.value = payload.user
    ready.value = true
    if (broadcast && channel) {
      channel.postMessage({
        type: 'session',
        accessToken: payload.accessToken,
        expiresAt: expiresAt.value,
        user: payload.user,
      } satisfies SessionMessage)
    }
  }

  function clearLocal() {
    accessToken.value = ''
    expiresAt.value = 0
    user.value = null
    ready.value = true
  }

  function clearSession(broadcast = true) {
    clearLocal()
    if (broadcast && channel) {
      channel.postMessage({ type: 'logout' } satisfies LogoutMessage)
    }
  }

  async function requestRefresh(): Promise<string> {
    const { data } = await axios.post<AuthResponse>(
      `${API_BASE}/auth/refresh`,
      null,
      { withCredentials: true },
    )
    applySession(data)
    return data.accessToken
  }

  /**
   * Single-flight refresh shared by the router guard and the axios 401
   * interceptor. If another tab rotated the cookie first, the backend answers
   * REFRESH_RETRY; wait briefly so the shared cookie is updated, then retry.
   */
  function refreshSession(): Promise<string> {
    if (!refreshPromise) {
      refreshPromise = requestRefresh()
        .catch(async (error) => {
          if (axios.isAxiosError(error) && error.response?.data?.code === 'REFRESH_RETRY') {
            await new Promise((resolve) => setTimeout(resolve, 300))
            return requestRefresh()
          }
          throw error
        })
        .finally(() => {
          refreshPromise = null
        })
    }
    return refreshPromise
  }

  async function ensureSession(): Promise<boolean> {
    if (isMock) {
      if (!accessToken.value) {
        applySession(
          {
            accessToken: 'mock-token-gh-pages',
            tokenType: 'Bearer',
            expiresIn: 86_400,
            user: MOCK_USER,
          },
          false,
        )
      }
      return true
    }

    if (accessToken.value && Date.now() < expiresAt.value - REFRESH_MARGIN_MS) {
      return true
    }
    if (restoreAttempted && !accessToken.value) {
      ready.value = true
      return false
    }
    if (bootstrapPromise) {
      return bootstrapPromise
    }

    restoreAttempted = true
    bootstrapPromise = refreshSession()
      .then(() => true)
      .catch(() => {
        clearLocal()
        return false
      })
      .finally(() => {
        bootstrapPromise = null
        ready.value = true
      })
    return bootstrapPromise
  }

  async function login(username: string, password: string) {
    if (isMock) {
      applySession({
        accessToken: 'mock-token-gh-pages',
        tokenType: 'Bearer',
        expiresIn: 86_400,
        user: MOCK_USER,
      })
      return
    }
    const { data } = await axios.post<AuthResponse>(
      `${API_BASE}/auth/login`,
      { username, password },
      { withCredentials: true },
    )
    applySession(data)
  }

  async function logout() {
    if (!isMock) {
      try {
        await axios.post(`${API_BASE}/auth/logout`, null, { withCredentials: true })
      } catch {
        // The local session is cleared even if the network call fails.
      }
    }
    clearSession()
  }

  initChannel()

  return {
    accessToken,
    expiresAt,
    user,
    ready,
    isAuthenticated,
    ensureSession,
    refreshSession,
    login,
    logout,
    clearSession,
  }
})

import { del, get, post, postForm, put } from './http'
import type {
  ActionState,
  AuthResponse,
  AuthSession,
  CheckUserResponse,
  Me,
  Trend,
  TweetItem,
  TweetPage,
} from '@/types'

type LoginRequest = { username: string; password: string }
type CheckUserRequest = { username: string }
type ChangePasswordRequest = { currentPassword: string; newPassword: string }

const pageParams = (cursor?: string | null, limit = 20) => ({
  ...(cursor ? { cursor } : {}),
  limit,
})

export const getTweets = (cursor?: string | null, limit = 20): Promise<TweetPage> =>
  get('/tweets', pageParams(cursor, limit))

export const getTweet = (tweetId: string): Promise<TweetItem> => get(`/tweets/${tweetId}`)

export const getReplies = (
  tweetId: string,
  cursor?: string | null,
  limit = 20,
): Promise<TweetPage> => get(`/tweets/${tweetId}/replies`, pageParams(cursor, limit))

export const createTweet = (form: FormData): Promise<TweetItem> =>
  postForm('/tweets', form)

export const deleteTweet = (tweetId: string): Promise<void> => del(`/tweets/${tweetId}`)

export const like = (tweetId: string): Promise<ActionState> =>
  put(`/tweets/${tweetId}/like`)

export const unlike = (tweetId: string): Promise<ActionState> =>
  del(`/tweets/${tweetId}/like`)

export const repost = (tweetId: string): Promise<ActionState> =>
  put(`/tweets/${tweetId}/repost`)

export const unrepost = (tweetId: string): Promise<ActionState> =>
  del(`/tweets/${tweetId}/repost`)

export const bookmark = (tweetId: string): Promise<ActionState> =>
  put(`/tweets/${tweetId}/bookmark`)

export const unbookmark = (tweetId: string): Promise<ActionState> =>
  del(`/tweets/${tweetId}/bookmark`)

export const getTrends = (): Promise<Trend[]> => get('/trends')

export const getMe = (): Promise<Me> => get('/me')

export const getUserTweets = (
  userId: string,
  cursor?: string | null,
  limit = 20,
): Promise<TweetPage> => get(`/users/${userId}/tweets`, pageParams(cursor, limit))

export const getUserReplies = (
  userId: string,
  cursor?: string | null,
  limit = 20,
): Promise<TweetPage> => get(`/users/${userId}/replies`, pageParams(cursor, limit))

export const getBookmarks = (cursor?: string | null, limit = 20): Promise<TweetPage> =>
  get('/me/bookmarks', pageParams(cursor, limit))

export const getLikes = (cursor?: string | null, limit = 20): Promise<TweetPage> =>
  get('/me/likes', pageParams(cursor, limit))

export const checkUser = (body: CheckUserRequest): Promise<CheckUserResponse> =>
  post('/checkUser', body)

export const login = (body: LoginRequest): Promise<AuthResponse> => post('/auth/login', body)
export const logout = (): Promise<void> => post('/auth/logout')
export const refresh = (): Promise<AuthResponse> => post('/auth/refresh')

export const getSessions = (): Promise<AuthSession[]> => get('/auth/sessions')
export const revokeSession = (id: string): Promise<void> => del(`/auth/sessions/${id}`)
export const changePassword = (body: ChangePasswordRequest): Promise<void> =>
  post('/auth/change-password', body)

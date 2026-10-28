import { del, get, post, postForm, put } from './http'
import type {
  ActionState,
  AuthResponse,
  AuthSession,
  CheckUserResponse,
  FollowPage,
  FollowState,
  FollowUser,
  Me,
  SearchOverview,
  Trend,
  TweetItem,
  TweetPage,
  UserProfile,
} from '@/types'

type LoginRequest = { username: string; password: string }
type CheckUserRequest = { username: string }
type ChangePasswordRequest = { currentPassword: string; newPassword: string }

const pageParams = (cursor?: string | null, limit = 20) => ({
  ...(cursor ? { cursor } : {}),
  limit,
})

export const getTweets = (
  cursor?: string | null,
  limit = 20,
  feed?: 'following',
): Promise<TweetPage> =>
  get('/tweets', { ...pageParams(cursor, limit), ...(feed ? { feed } : {}) })

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

export const getSearchOverview = (query: string): Promise<SearchOverview> =>
  get('/search', { q: query })

export const searchUsers = (query: string, limit = 20): Promise<FollowUser[]> =>
  get('/search/users', { q: query, limit })

export const searchTweets = (
  query: string,
  cursor?: string | null,
  limit = 20,
): Promise<TweetPage> =>
  get('/search/tweets', { q: query, ...pageParams(cursor, limit) })

export const searchTopics = (query: string, limit = 20): Promise<Trend[]> =>
  get('/search/topics', { q: query, limit })

export const getMe = (): Promise<Me> => get('/me')

export const getProfile = (userId: string): Promise<UserProfile> =>
  get(`/users/${encodeURIComponent(userId)}`)

export const getFollowSuggestions = (): Promise<FollowUser[]> => get('/users/suggestions')

export const getFollowers = (
  userId: string,
  cursor?: string | null,
  limit = 20,
): Promise<FollowPage> =>
  get(`/users/${encodeURIComponent(userId)}/followers`, pageParams(cursor, limit))

export const getFollowing = (
  userId: string,
  cursor?: string | null,
  limit = 20,
): Promise<FollowPage> =>
  get(`/users/${encodeURIComponent(userId)}/following`, pageParams(cursor, limit))

export const followUser = (userId: string): Promise<FollowState> =>
  put(`/users/${encodeURIComponent(userId)}/follow`)

export const unfollowUser = (userId: string): Promise<FollowState> =>
  del(`/users/${encodeURIComponent(userId)}/follow`)

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
